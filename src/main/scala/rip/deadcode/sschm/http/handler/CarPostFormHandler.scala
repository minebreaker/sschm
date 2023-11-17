package rip.deadcode.sschm.http.handler

import cats.effect.IO
import com.google.common.net.MediaType
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.StringHttpResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.lib.handlebars.render

import scala.util.matching.compat.Regex

object CarPostFormHandler extends HttpHandler:

  override def url: Regex = "^/car/new$".r

  override def method: String = "GET"

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] =
    val template = ctx.handlebars.compile("car_post_form") // TODO: share template instance
    IO.pure(
      StringHttpResponse(
        200,
        MediaType.HTML_UTF_8,
        template.render(Map.empty)
      )
    )
