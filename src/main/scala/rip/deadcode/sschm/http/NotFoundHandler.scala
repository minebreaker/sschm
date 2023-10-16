package rip.deadcode.sschm.http

import cats.effect.IO
import com.google.common.net.MediaType

import scala.util.matching.compat.Regex

object NotFoundHandler extends HttpHandler:

  override def url(): Regex = ".*".r
  override def handle(): IO[HttpResponse] = IO.pure(
    HttpResponse(
      status = 404,
      contentType = MediaType.HTML_UTF_8,
      body = "<h1>404 Not Found</h1>"
    )
  )
