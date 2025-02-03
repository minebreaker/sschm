package rip.deadcode.sschm.http.handler

import cats.effect.IO
import com.google.common.net.MediaType
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.StringHttpResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}

import scala.util.matching.compat.Regex

object HealthHandler extends HttpHandler:

  override def url: Regex = "^/health$".r

  override def method: String = "GET"

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] = IO.pure(
    StringHttpResponse(
      200,
      MediaType.HTML_UTF_8,
      "OK"
    )
  )
