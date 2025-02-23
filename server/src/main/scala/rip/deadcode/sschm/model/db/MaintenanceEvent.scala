package rip.deadcode.sschm.model.db

import java.time.ZonedDateTime

case class MaintenanceEvent(
    id: String,
    carId: String,
    odo: Option[Int],
    note: String,
    price: Option[Int],
    maintenanceId: Option[String],
    eventDate: ZonedDateTime,
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime
) extends Event
