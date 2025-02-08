package rip.deadcode.sschm.http.handler.api

import cats.effect.IO
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.JsonResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.service.refuel.{WriteRefuelParams, writeRefuel}

import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId}
import scala.util.matching.Regex

object RefuelPostHandler extends HttpHandler:

  override def url: Regex = "^/api/car/([^/]+)/refuel$".r

  override def method: String = "POST"

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] =

    val carId = url.findFirstMatchIn(request.getOriginalURI) match
      case Some(m) => m.group(1)
      case None    => ???

    val is = request.getInputStream.readAllBytes()

    for
      requestBody <- IO.fromEither(decode[RefuelPostRequest](new String(is, StandardCharsets.UTF_8)))

      note = requestBody.note.getOrElse("")
      amount = (BigDecimal(requestBody.amount) * 10).setScale(0).toIntExact // FIXME
      eventDate = requestBody.eventDate
        .atZone(ZoneId.systemDefault()) // FIXME

      _ <- writeRefuel(ctx)(
        WriteRefuelParams(
          carId,
          requestBody.odo,
          requestBody.price,
          note,
          amount,
          requestBody.noPreviousRefuel,
          eventDate
        )
      )
      response = RefuelPostResponse()
    yield JsonResponse(
      200,
      response.asJson.noSpaces
    )

  private case class RefuelPostRequest(
      odo: Option[Int],
      price: Option[Int],
      note: Option[String],
      amount: Int,
      noPreviousRefuel: Boolean,
      eventDate: LocalDateTime
  )

  private object RefuelPostRequest:
    implicit val decoder: Decoder[RefuelPostRequest] = io.circe.generic.semiauto.deriveDecoder

  private case class RefuelPostResponse()

  private object RefuelPostResponse:
    implicit val encoder: Encoder[RefuelPostResponse] = io.circe.generic.semiauto.deriveEncoder
