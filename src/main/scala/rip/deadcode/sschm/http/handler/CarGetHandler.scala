package rip.deadcode.sschm.http.handler

import cats.effect.IO
import com.google.common.net.MediaType
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.StringHttpResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.service.car.readCar

import javax.annotation.Nullable
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
      car <- readCar(ctx)(id)
      html = template.apply(
        CarGetTemplate(
          car.name,
          car.photoId.orNull
        )
      )
    yield StringHttpResponse(
      200,
      MediaType.HTML_UTF_8,
      html
    )

  private case class CarGetTemplate(
      name: String,
      photoId: String @Nullable
  )
