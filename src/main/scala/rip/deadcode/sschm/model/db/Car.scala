package rip.deadcode.sschm.model.db

import java.time.ZonedDateTime

case class Car(
    id: String,
    name: String,
    photoId: Option[String],
    note: String,
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime
)
