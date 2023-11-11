package rip.deadcode.sschm.service.photo

import cats.effect.IO
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
