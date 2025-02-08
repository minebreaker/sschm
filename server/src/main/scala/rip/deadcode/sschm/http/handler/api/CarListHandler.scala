package rip.deadcode.sschm.http.handler.api

import cats.effect.IO
import io.circe.Encoder
import io.circe.syntax.*
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.JsonResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.service.car.listCars

import scala.util.matching.compat.Regex

object CarListHandler extends HttpHandler:

  override def url: Regex = "^/api/cars$".r

  override def method: String = "GET"

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] =
    for {
      result <- listCars(ctx)
      response = CarListResponse(
        result.map(c =>
          CarListResponseCar(
            c.id,
            c.name,
            c.photoId,
            c.note
          )
        )
      )
    } yield JsonResponse(
      200, response.asJson.noSpaces
    ) 
  private case class CarListResponse(
      items: Seq[CarListResponseCar]
  )

  private object CarListResponse:
    implicit val encoder: Encoder[CarListResponse] = io.circe.generic.semiauto.deriveEncoder

  private case class CarListResponseCar(
      id: String,
      name: String,
      photoId: Option[String],
      note: String
  )

  private object CarListResponseCar:
    implicit val encoder: Encoder[CarListResponseCar] = io.circe.generic.semiauto.deriveEncoder
