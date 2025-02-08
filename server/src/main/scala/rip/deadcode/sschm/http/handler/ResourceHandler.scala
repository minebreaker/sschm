package rip.deadcode.sschm.http.handler

import cats.effect.IO
import com.google.common.io.Resources
import com.google.common.net.MediaType
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.StringHttpResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}

import java.nio.charset.StandardCharsets
import scala.util.matching.compat.Regex

object ResourceHandler extends HttpHandler:

  override def url: Regex = "^(/)|(/car(/.*)?)|(/index\\.css)|(/index\\.js)$".r

  override def method: String = "GET"

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] =

    val (fileName, contentType): (String, MediaType) =
      request.getOriginalURI match
        case "/" =>
          (
            "index.html",
            MediaType.HTML_UTF_8
          )
        case "/index.css" =>
          (
            "index.css",
            MediaType.CSS_UTF_8
          )
        case "/index.js" =>
          (
            "index.js",
            MediaType.JAVASCRIPT_UTF_8
          )
        case s if s.startsWith("/car") =>
          (
            "index.html",
            MediaType.HTML_UTF_8
          )
        case _ => throw Error("unreachable")

    IO {
      StringHttpResponse(
        200,
        contentType,
        // TODO: use stream
        Resources.toString(Resources.getResource("ui/" + fileName), StandardCharsets.UTF_8)
      )
    }
