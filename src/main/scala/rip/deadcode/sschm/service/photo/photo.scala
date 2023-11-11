package rip.deadcode.sschm.service.photo

import cats.effect.IO
import com.google.common.net.MediaType
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.service.photo.model.Photo

def readPhoto(ctx: AppContext)(id: String): IO[Photo] =
  IO.blocking {
    ctx.jdbi.withHandle { handle =>
      handle
        // language=sql
        .createQuery("select id, content_type, data from photo where id = :id")
        .bind("id", id)
        .map(r =>
          Photo(
            r.getColumn("id", classOf[String]),
            r.getColumn("content_type", classOf[String]),
            r.getColumn("data", classOf[Array[Byte]])
          )
        )
        .one()
    }
  }

case class WritePhotoParams(
    carId: String,
    binary: Array[Byte],
    mediaType: MediaType
)

def writePhoto(ctx: AppContext)(params: WritePhotoParams): IO[Unit] =
  for
    id <- IO.randomUUID
    _ <- IO.blocking {
      ctx.jdbi.inTransaction { handle =>

        handle
          // language=sql
          .createUpdate(
            """update car set
              |  photo_id = :photo_id,
              |  updated_at = current_timestamp
              |where
              |  id = :car_id""".stripMargin)

        handle
          // language=sql
          .createUpdate("insert into photo(id, content_type, data) values (:id, :content_type, :data);")
          .bind("id", id)
          .bind("content_type", params.mediaType.toString)
          .bind("data", params.binary)
          .execute()
      }
    }
  yield ()
