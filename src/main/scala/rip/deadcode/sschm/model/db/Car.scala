package rip.deadcode.sschm.model.db

import org.jdbi.v3.core.result.RowView

import java.time.ZonedDateTime

case class Car(
    id: String,
    name: String,
    photoId: Option[String],
    note: String,
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime
)
