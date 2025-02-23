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
        events.map {
          case e: EventImpl =>
            CarGetResponseEventEvent(
              e.id,
              "event",
              e.carId,
              e.odo.fold("- ")(_.toString) + "km",
              e.price.fold("-")("¥" + _.toString.formatted("%,d")),
              e.note,
              e.eventDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
          case e: MaintenanceEvent =>
            CarGetResponseEventMaintenance(
              e.id,
              "maintenance",
              e.carId,
              e.odo.fold("- ")(_.toString) + "km",
              e.price.fold("-")("¥" + _.toString.formatted("%,d")),
              e.note,
              e.eventDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
          case e: Refuel =>
            CarGetResponseEventRefuel(
              e.id,
              "refuel",
              e.carId,
              e.odo.fold("- ")(_.toString) + "km",
              e.price.fold("-")("¥%,d".format(_)),
              "%,.2fL".format(e.amount.toFloat / 100),
              e.noPreviousRefuel,
              e.note,
              e.eventDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        }
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

  private sealed trait CarGetResponseEvent

  private object CarGetResponseEvent:
    implicit val encoder: Encoder[CarGetResponseEvent] = Encoder.instance {
      case event: CarGetResponseEventEvent             => event.asJson
      case maintenance: CarGetResponseEventMaintenance => maintenance.asJson
      case refuel: CarGetResponseEventRefuel           => refuel.asJson
    }

  private case class CarGetResponseEventEvent(
      id: String,
      `type`: String,
      carId: String,
      odo: String,
      price: String,
      note: String,
      eventDate: String
  ) extends CarGetResponseEvent

  private object CarGetResponseEventEvent:
    implicit val encoder: Encoder[CarGetResponseEventEvent] = io.circe.generic.semiauto.deriveEncoder

  private case class CarGetResponseEventMaintenance(
      id: String,
      `type`: String,
      carId: String,
      odo: String,
      price: String,
      note: String,
      eventDate: String
  ) extends CarGetResponseEvent

  private object CarGetResponseEventMaintenance:
    implicit val encoder: Encoder[CarGetResponseEventMaintenance] = io.circe.generic.semiauto.deriveEncoder

  private case class CarGetResponseEventRefuel(
      id: String,
      `type`: String,
      carId: String,
      odo: String,
      price: String,
      amount: String,
      noPreviousRefuel: Boolean,
      note: String,
      eventDate: String
  ) extends CarGetResponseEvent

  private object CarGetResponseEventRefuel:
    implicit val encoder: Encoder[CarGetResponseEventRefuel] = io.circe.generic.semiauto.deriveEncoder
