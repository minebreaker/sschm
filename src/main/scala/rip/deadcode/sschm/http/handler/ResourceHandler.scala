package rip.deadcode.sschm.http.handler

import cats.effect.IO
import com.github.jknack.handlebars.Template
import com.google.common.net.MediaType
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.StringHttpResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}

import scala.util.matching.compat.Regex

object ResourceHandler extends HttpHandler:

  override def url: Regex = "^(/style\\.css)$".r

  override def method: String = "GET"

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] =

    import scala.jdk.CollectionConverters.*

    val (template, contentType, properties): (Template, MediaType, java.util.Map[String, String]) =
      request.getOriginalURI match
        case "/style.css" =>
          (
            ctx.handlebars.compile("style.css"),
            MediaType.CSS_UTF_8,
            Map("defaultFontSize" -> "14px").asJava // TODO
          )
        case _ => throw Error("unreachable")

    IO {
      StringHttpResponse(200, contentType, template.apply(properties))
    }
