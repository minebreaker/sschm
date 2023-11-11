package rip.deadcode.sschm.http.handler

import cats.effect.IO
import com.google.common.net.MediaType
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.BinaryHttpResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.service.photo.readPhoto

import scala.util.matching.compat.Regex

object PhotoGetHandler extends HttpHandler:

  private val regex = "^/api/photo/([^/]+)$".r

  override def url: Regex = regex

  override def method: String = "GET"

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] =

    val id = regex.findFirstMatchIn(request.getOriginalURI) match
      case Some(m) => m.group(1)
      case None    => ???

    for photo <- readPhoto(ctx)(id)
    yield BinaryHttpResponse(200, MediaType.parse(photo.contentType), photo.data)
