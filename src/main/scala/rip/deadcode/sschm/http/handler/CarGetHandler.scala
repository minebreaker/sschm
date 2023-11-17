package rip.deadcode.sschm.http.handler

import cats.effect.IO
import com.github.jknack.handlebars.Context
import com.google.common.net.MediaType
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl.*
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.StringHttpResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.lib.handlebars.ScalaValueResolver
import rip.deadcode.sschm.model.db.{Event, EventImpl, MaintenanceEvent, Refuel}
import rip.deadcode.sschm.service.car.readCar

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

    for
      result <- readCar(ctx)(id)
      templateContext = result.into[TemplateContext].transform
      html = template.apply(
        Context
          .newBuilder(templateContext)
          .resolver(new ScalaValueResolver)
          .build()
      )
    yield StringHttpResponse(
      200,
      MediaType.HTML_UTF_8,
      html
    )

  private case class TemplateContext(
      car: CarTemplate,
      events: Seq[EventTemplate]
  )

  private case class CarTemplate(
      id: String,
      name: String,
      photoId: Option[String],
      note: String
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
