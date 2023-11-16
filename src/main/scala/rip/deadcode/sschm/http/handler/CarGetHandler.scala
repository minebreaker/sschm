package rip.deadcode.sschm.http.handler

import cats.effect.IO
import com.github.jknack.handlebars.Context
import com.google.common.net.MediaType
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.StringHttpResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.lib.handlebars.ScalaValueResolver
import rip.deadcode.sschm.model.db.{Car, Event}
import rip.deadcode.sschm.service.car.readCar

import scala.jdk.CollectionConverters.*
import scala.util.matching.compat.Regex

object CarGetHandler extends HttpHandler:

  override def url: Regex = "^/car/([^/]+)$".r

  override def method: String = "GET"

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] =

    val template = ctx.handlebars.compile("car")

    val id = url.findFirstMatchIn(request.getOriginalURI) match
      case Some(m) => m.group(1)
      case None    => ???

    for
      result <- readCar(ctx)(id)
      html = template.apply(
        Context
          .newBuilder(result)
          .resolver(new ScalaValueResolver)
          .build()
      )
    yield StringHttpResponse(
      200,
      MediaType.HTML_UTF_8,
      html
    )
