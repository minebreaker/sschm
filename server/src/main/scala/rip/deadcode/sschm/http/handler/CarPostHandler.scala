package rip.deadcode.sschm.http.handler

import cats.effect.IO
import com.google.common.net.HttpHeaders
import org.eclipse.jetty.server.Request
import org.slf4j.LoggerFactory
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.EmptyHttpResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.service.car.{WriteCarParams, writeCar}

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId}
import scala.util.matching.compat.Regex

object CarPostHandler extends HttpHandler:

  private val logger = LoggerFactory.getLogger(classOf[CarPostHandler.type])

  override def url: Regex = "^/car$".r

  override def method: String = "POST"

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] =
    for
      name <- IO.fromOption(Option(request.getParameter("name")))(???)
      odo = Option(request.getParameter("odo")).flatMap(_.toIntOption)
      price = Option(request.getParameter("price")).flatMap(_.toIntOption)
      eventDate = LocalDateTime
        .parse(request.getParameter("event_date"), DateTimeFormatter.ISO_DATE_TIME)
        .atZone(ZoneId.systemDefault()) // FIXME
      note = Option(request.getParameter("note")).getOrElse("")

      result <- writeCar(ctx)(
        WriteCarParams(
          name,
          odo,
          price,
          note,
          eventDate
        )
      )
    yield EmptyHttpResponse(
      303,
      Map(HttpHeaders.LOCATION -> s"/car/${result.carId}")
    )
