package rip.deadcode.sschm.http

import cats.effect.IO
import com.google.common.net.MediaType
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.StringHttpResponse

import scala.util.matching.compat.Regex

object HelloWorldHandler extends HttpHandler:

  override def url(): Regex = "^/health$".r

  override def handle(url: String, ctx: AppContext): IO[HttpResponse] = IO.pure(
    StringHttpResponse(
      200,
      MediaType.HTML_UTF_8,
      "OK"
    )
  )
