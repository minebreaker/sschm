package rip.deadcode.sschm.http

import cats.effect.IO
import com.google.common.net.MediaType
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.StringHttpResponse

import scala.util.matching.compat.Regex

object NotFoundHandler extends HttpHandler:

  override def url(): Regex = "^.*$".r
  override def handle(url: String, ctx: AppContext): IO[HttpResponse] = IO.pure(
    StringHttpResponse(
      status = 404,
      contentType = MediaType.HTML_UTF_8,
      body = "<h1>404 Not Found</h1>"
    )
  )
