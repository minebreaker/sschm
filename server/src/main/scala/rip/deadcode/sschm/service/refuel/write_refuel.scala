package rip.deadcode.sschm.service.refuel

import cats.effect.IO
import rip.deadcode.sschm.AppContext

import java.time.ZonedDateTime

case class WriteRefuelParams(
    carId: String,
    odo: Option[Int],
    price: Option[Int],
    note: String,
    amount: Int,
    noPreviousRefuel: Boolean,
    eventDate: ZonedDateTime
)

def writeRefuel(ctx: AppContext)(params: WriteRefuelParams): IO[Unit] =
  for
    refuelId <- IO.randomUUID
    _ <- IO.blocking {
      ctx.jdbi.inTransaction { handle =>
        handle
          // language=sql
          .createUpdate("""insert into refuel(id, car_id, odo, price, note, amount, no_previous_refuel, event_date, created_at, updated_at)
              |values (:refuel_id, :car_id::uuid, :odo, :price, :note, :amount, :no_previous_refuel, :event_date, current_timestamp, current_timestamp)
              |""".stripMargin)
          .bind("refuel_id", refuelId)
          .bind("car_id", params.carId)
          .bind("odo", params.odo.orNull)
          .bind("price", params.price.orNull)
          .bind("note", params.note)
          .bind("amount", params.amount)
          .bind("no_previous_refuel", params.noPreviousRefuel)
          .bind("event_date", params.eventDate)
          .execute()
      }
    }
  yield ()
