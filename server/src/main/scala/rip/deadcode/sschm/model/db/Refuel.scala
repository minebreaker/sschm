package rip.deadcode.sschm.model.db

import java.time.ZonedDateTime

case class Refuel(
    id: String,
    carId: String,
    odo: Option[Int],
    price: Option[Int],
    note: String,
    amount: Option[Int],
    noPreviousRefuel: Boolean,
    eventDate: ZonedDateTime,
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime
) extends Event
