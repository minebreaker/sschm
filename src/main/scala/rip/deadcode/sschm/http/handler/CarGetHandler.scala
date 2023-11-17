package rip.deadcode.sschm.http.handler

import cats.effect.IO
import com.google.common.net.MediaType
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl.*
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.StringHttpResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.lib.handlebars.{TemplateContext, render}
import rip.deadcode.sschm.model.db.{Car, Event, EventImpl, MaintenanceEvent, Refuel}
import rip.deadcode.sschm.service.car.{CalcFuelEfficiencyResult, readCarAndEvents}

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import scala.util.matching.compat.Regex

object CarGetHandler extends HttpHandler:

  override def url: Regex = "^/car/([^/]+)$".r

  override def method: String = "GET"

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] =

    val template = ctx.handlebars.compile("car")

    val id = url.findFirstMatchIn(request.getOriginalURI) match
      case Some(m) => m.group(1)
      case None    => ???

    // FIXME
    implicit val dateTransformer: Transformer[ZonedDateTime, String] = zdt =>
      zdt.format(DateTimeFormatter.ISO_DATE_TIME)
    implicit val eventTransformer: Transformer[Event, EventTemplate] = e =>
      e.into[EventTemplate]
        .withFieldComputed(
          _.`type`,
          {
            case _: EventImpl        => "event"
            case _: MaintenanceEvent => "maintenance"
            case _: Refuel           => "refuel"
          }
        )
        .withFieldComputed(_.odo, _.odo.fold("-")(_.toString))
        .withFieldComputed(_.price, _.price.fold("-")(_.toString))
        .transform
    implicit val efficiencyTransformer: Transformer[CalcFuelEfficiencyResult, EfficiencyTemplate] = e =>
      EfficiencyTemplate(
        efficiency = if (e.effective) {
          s"${e.efficiency} km/L"
        } else {
          "- km/L"
        }
      )

    for
      result <- readCarAndEvents(ctx)(id)
      templateContext = {
        implicit val carTransformer: Transformer[Car, CarTemplate] = car =>
          car
            .into[CarTemplate]
            .withFieldConst(_.odo, result.events.flatMap(_.odo).headOption.fold("- km")(odo => s"$odo km"))
            .transform
        result.into[PageCtx].transform
      }
      html = template.render(templateContext)
    yield StringHttpResponse(
      200,
      MediaType.HTML_UTF_8,
      html
    )

  private case class PageCtx(
      car: CarTemplate,
      events: Seq[EventTemplate],
      efficiency: EfficiencyTemplate
  ) extends TemplateContext

  private case class CarTemplate(
      id: String,
      name: String,
      photoId: Option[String],
      note: String,
      odo: String
  )

  private case class EventTemplate(
      id: String,
      `type`: String,
      carId: String,
      odo: String,
      price: String,
      note: String,
      eventDate: String
  )

  private case class EfficiencyTemplate(
      efficiency: String
  )
