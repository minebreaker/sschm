package rip.deadcode.sschm.http.handler

import cats.effect.IO
import com.google.common.net.HttpHeaders
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.EmptyHttpResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.service.refuel.{WriteRefuelParams, writeRefuel}

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId}
import scala.util.matching.Regex

object RefuelPostHandler extends HttpHandler:

  override def url: Regex = "^/car/([^/]+)/refuel$".r

  override def method: String = "POST"

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] =

    val id = url.findFirstMatchIn(request.getOriginalURI) match
      case Some(m) => m.group(1)
      case None    => ???

    for
      _ <- IO.unit
      odo = Option(request.getParameter("odo")).flatMap(_.toIntOption)
      price = Option(request.getParameter("price")).flatMap(_.toIntOption)
      note = Option(request.getParameter("note")).getOrElse("")
      amount = Option(request.getParameter("amount")).map { amount =>
        (BigDecimal(amount) * 10).setScale(0).toIntExact // FIXME
      }
      noPreviousRefuel = request.getParameter("no_previous_refuel") == "true"
      eventDate = LocalDateTime
        .parse(request.getParameter("event_date"), DateTimeFormatter.ISO_DATE_TIME)
        .atZone(ZoneId.systemDefault()) // FIXME

      _ <- writeRefuel(ctx)(
        WriteRefuelParams(
          id,
          odo,
          price,
          note,
          amount,
          noPreviousRefuel,
          eventDate
        )
      )
    yield EmptyHttpResponse(
      303,
      Map(HttpHeaders.LOCATION -> s"/car/$id")
    )
