package rip.deadcode.sschm.http

import cats.effect.IO
import com.google.common.net.MediaType

import scala.util.matching.compat.Regex

object HelloWorldHandler extends HttpHandler:

  override def url(): Regex = "/health".r

  override def handle(): IO[HttpResponse] = IO.pure(HttpResponse(
    200, MediaType.HTML_UTF_8, "OK"
  ))
