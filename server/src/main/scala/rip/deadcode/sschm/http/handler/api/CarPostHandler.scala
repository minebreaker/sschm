package rip.deadcode.sschm.http.handler.api

import cats.effect.IO
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}
import org.eclipse.jetty.server.Request
import org.slf4j.LoggerFactory
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.JsonResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.service.car.{WriteCarParams, writeCar}

import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId}
import scala.util.matching.compat.Regex

object CarPostHandler extends HttpHandler:

  private val logger = LoggerFactory.getLogger(classOf[CarPostHandler.type])

  override def url: Regex = "^/api/car$".r

  override def method: String = "POST"

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] =
    val is = request.getInputStream.readAllBytes()
    for
      requestBody <- IO.fromEither(decode[CarPostRequest](new String(is, StandardCharsets.UTF_8)))

      name = requestBody.name
      odo = requestBody.odo
      price = requestBody.price
      eventDate = LocalDateTime
        .parse(requestBody.eventDate, DateTimeFormatter.ISO_DATE_TIME)
        .atZone(ZoneId.systemDefault()) // FIXME
      note = requestBody.note.getOrElse("")

      result <- writeCar(ctx)(
        WriteCarParams(
          name,
          odo,
          price,
          note,
          eventDate
        )
      )
      response = CarPostResponse(
        result.carId,
        name,
        None, // FIXME
        note
      )
    yield JsonResponse(
      200,
      response.asJson.noSpaces
    )

  private case class CarPostRequest(
      name: String,
      photoId: Option[String],
      note: Option[String],
      odo: Option[Int],
      price: Option[Int],
      eventDate: String
  )

  private object CarPostRequest:
    implicit val decoder: Decoder[CarPostRequest] = io.circe.generic.semiauto.deriveDecoder

  private case class CarPostResponse(
      id: String,
      name: String,
      photoId: Option[String],
      note: String
  )

  private object CarPostResponse:
    implicit val encoder: Encoder[CarPostResponse] = io.circe.generic.semiauto.deriveEncoder
