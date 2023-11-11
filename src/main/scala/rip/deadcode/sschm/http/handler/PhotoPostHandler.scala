package rip.deadcode.sschm.http.handler

import cats.effect.IO
import com.google.common.net.{HttpHeaders, MediaType}
import org.apache.commons.fileupload2.core.DiskFileItemFactory
import org.apache.commons.fileupload2.jakarta.JakartaServletDiskFileUpload
import org.eclipse.jetty.server.Request
import org.slf4j.LoggerFactory
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.HttpResponse.EmptyHttpResponse
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}
import rip.deadcode.sschm.service.photo.{WritePhotoParams, writePhoto}

import java.nio.charset.StandardCharsets
import scala.jdk.CollectionConverters.*
import scala.util.matching.compat.Regex

object PhotoPostHandler extends HttpHandler:

  private val logger = LoggerFactory.getLogger(classOf[PhotoPostHandler.type])

  override def url: Regex = "^/photo/upload$".r

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

      carIdItem <- IO.fromOption(items.find(_.getFieldName == "car_id"))(???)
      carId = carIdItem.getString(StandardCharsets.UTF_8)

      fileItem <- IO.fromOption(items.find(_.getFieldName == "file"))(???)
      file = fileItem.get()
      mediaType = MediaType.parse(fileItem.getContentType)

      _ <- writePhoto(ctx)(
        WritePhotoParams(
          carId,
          file,
          mediaType
        )
      )
    yield EmptyHttpResponse(
      303,
      Map(HttpHeaders.LOCATION -> "/health")
    )
