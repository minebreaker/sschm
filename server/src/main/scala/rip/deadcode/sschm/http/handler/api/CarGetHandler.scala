package rip.deadcode.sschm.http.handler.api

import cats.effect.IO
import io.circe.Encoder
import io.circe.syntax.*
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.JsonResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.model.db.{EventImpl, MaintenanceEvent, Refuel}
import rip.deadcode.sschm.service.car.{ReadCarResult, readCarAndEvents}

import java.time.format.DateTimeFormatter
import scala.util.matching.compat.Regex

object CarGetHandler extends HttpHandler:

  override def url: Regex = "^/api/car/([^/]+)$".r

  override def method: String = "GET"

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] =

    val id = url.findFirstMatchIn(request.getOriginalURI) match
      case Some(m) => m.group(1)
      case None    => ???

    for
      ReadCarResult(
        car,
        events,
        efficiency
      ) <- readCarAndEvents(ctx)(id)
      response = CarGetResponse(
        car.id,
        car.name,
        car.photoId,
        car.note,
        events.flatMap(_.odo).headOption.fold("- km")(odo => s"$odo km"),
        if (efficiency.effective) {
          s"${efficiency.efficiency} km/L"
        } else {
          "- km/L"
        },
        events.map(e =>
          CarGetResponseEvent(
            e.id,
            e match {
              case _: EventImpl        => "event"
              case _: MaintenanceEvent => "maintenance"
              case _: Refuel           => "refuel"
            },
            e.carId,
            e.odo.fold("-")(_.toString),
            e.price.fold("-")(_.toString),
            e.note,
            e.eventDate.format(DateTimeFormatter.ISO_DATE_TIME)
          )
        )
      )
    yield JsonResponse(200, response.asJson.noSpaces)

  private case class CarGetResponse(
      id: String,
      name: String,
      photoId: Option[String],
      note: String,
      odo: String,
      efficiency: String,
      events: Seq[CarGetResponseEvent]
  )

  private object CarGetResponse:
    implicit val encoder: Encoder[CarGetResponse] = io.circe.generic.semiauto.deriveEncoder

  private case class CarGetResponseEvent(
      id: String,
      `type`: String,
      carId: String,
      odo: String,
      price: String,
      note: String,
      eventDate: String
  )

  private object CarGetResponseEvent:
    implicit val encoder: Encoder[CarGetResponseEvent] = io.circe.generic.semiauto.deriveEncoder
