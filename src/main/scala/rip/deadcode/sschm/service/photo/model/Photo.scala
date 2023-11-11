package rip.deadcode.sschm.service.photo.model

import cats.effect.{IO, Ref}
import rip.deadcode.sschm.AppContext

case class Photo(
    id: String,
    contentType: String,
    data: Array[Byte]
)
