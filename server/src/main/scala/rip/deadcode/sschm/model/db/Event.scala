package rip.deadcode.sschm.model.db

import java.time.ZonedDateTime

trait Event:
  val id: String
  val carId: String
  val odo: Option[Int]
  val price: Option[Int]
  val note: String
  val eventDate: ZonedDateTime
  val createdAt: ZonedDateTime
  val updatedAt: ZonedDateTime

object Event:

  import scala.math.Ordering.Implicits._

  implicit val ordering: Ordering[Event] =
    Ordering.fromLessThan[Event]((e1, e2) => e1.eventDate <= e2.eventDate) orElse
      Ordering.fromLessThan((e1, e2) => e1.updatedAt <= e2.updatedAt) orElse
      Ordering.fromLessThan((e1, e2) => e1.createdAt <= e2.createdAt)

case class EventImpl(
    id: String,
    carId: String,
    odo: Option[Int],
    price: Option[Int],
    note: String,
    eventDate: ZonedDateTime,
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime
) extends Event
