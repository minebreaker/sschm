package rip.deadcode.sschm.service.car.model

import java.time.ZonedDateTime

case class Car(
    id: String,
    name: String,
    photoId: Option[String],
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime
)
