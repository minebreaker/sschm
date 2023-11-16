package rip.deadcode.sschm.model.db

import cats.effect.{IO, Ref}
import rip.deadcode.sschm.AppContext

case class Photo(
    id: String,
    contentType: String,
    data: Array[Byte]
)
