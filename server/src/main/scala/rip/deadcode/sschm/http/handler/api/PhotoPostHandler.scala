package rip.deadcode.sschm.http.handler.api

import cats.effect.IO
import com.google.common.net.{HttpHeaders, MediaType}
import io.circe.Encoder
import io.circe.syntax.*
import org.apache.commons.fileupload2.core.DiskFileItemFactory
import org.apache.commons.fileupload2.jakarta.JakartaServletDiskFileUpload
import org.eclipse.jetty.server.Request
import org.slf4j.LoggerFactory
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.{EmptyHttpResponse, JsonResponse}
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.service.photo.{WritePhotoParams, writePhoto}

import scala.jdk.CollectionConverters.*
import scala.util.matching.compat.Regex

object PhotoPostHandler extends HttpHandler:

  private val logger = LoggerFactory.getLogger(classOf[PhotoPostHandler.type])

  override def url: Regex = "^/api/photo$".r

  override def method: String = "POST"

  private val itemFactory = DiskFileItemFactory.builder().get()

  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] =
    for
      // We don't check file size here.
      // Request body size limit of Jetty should be changed.

      items <- IO.blocking {
        JakartaServletDiskFileUpload(itemFactory).parseRequest(request).asScala
      }

      _ = logger.debug("Uploaded items: {}", items)

      fileItem <- IO.fromOption(items.find(_.getFieldName == "file"))(???)
      file = fileItem.get()
      mediaType = MediaType.parse(fileItem.getContentType)

      photoId <- writePhoto(ctx)(
        WritePhotoParams(
          file,
          mediaType
        )
      )
      response = PhotoPostResponse(photoId)
    yield JsonResponse(
      200,
      response.asJson.noSpaces
    )

  private case class PhotoPostResponse(
      id: String
  )

  private object PhotoPostResponse:
    implicit val encoder: Encoder[PhotoPostResponse] = io.circe.generic.semiauto.deriveEncoder
