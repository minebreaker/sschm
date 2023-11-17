package rip.deadcode.sschm.http.handler

import cats.effect.IO
import com.google.common.net.MediaType
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.StringHttpResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.lib.handlebars.{TemplateContext, render}

import scala.util.matching.Regex
import rip.deadcode.sschm.service.car.readCar

object RefuelPostFormHandler extends HttpHandler:

  override def url: Regex = "^/car/([^/]+)/refuel/new$".r

  override def method: String = "GET"

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] =

    val template = ctx.handlebars.compile("refuel_post_form")

    val id = url.findFirstMatchIn(request.getOriginalURI) match
      case Some(m) => m.group(1)
      case None    => ???

    for car <- readCar(ctx)(id)
    yield
      val templateContext = PageCtx(
        id,
        car.name
      )

      StringHttpResponse(200, MediaType.HTML_UTF_8, template.render(templateContext))

  private case class PageCtx(
      carId: String,
      carName: String
  ) extends TemplateContext
