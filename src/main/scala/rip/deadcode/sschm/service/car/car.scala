package rip.deadcode.sschm.service.car

import cats.effect.IO
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.service.car.model.Car

import java.time.ZonedDateTime

def readCar(ctx: AppContext)(id: String): IO[Car] =
  for car <- IO.blocking {
      ctx.jdbi.withHandle { handle =>
        handle
          // language=sql
          .createQuery("select id, name, photo_id, note, created_at, updated_at from car where id::text = :id")
          .bind("id", id)
          .map(r =>
            Car(
              r.getColumn("id", classOf[String]),
              r.getColumn("name", classOf[String]),
              Option(r.getColumn("photo_id", classOf[String])),
              r.getColumn("created_at", classOf[ZonedDateTime]),
              r.getColumn("updated_at", classOf[ZonedDateTime])
            )
          )
          .one()
      }
    }
  yield car

case class WriteCarParams(
    name: String,
    odo: Option[Int],
    price: Option[Int],
    note: String,
    eventDate: ZonedDateTime
)

case class WriteCarResult(
    carId: String,
    eventId: String
)

def writeCar(ctx: AppContext)(params: WriteCarParams): IO[WriteCarResult] =
  for
    carId <- IO.randomUUID
    eventId <- IO.randomUUID
    _ <- IO.blocking {
      ctx.jdbi.inTransaction { handle =>
        handle
          // language=sql
          .createUpdate("""insert into car(id, name, note, created_at, updated_at)
                          |values (:id, :name, :note, current_timestamp, current_timestamp)
                          |""".stripMargin)
          .bind("id", carId)
          .bind("name", params.name)
          .bind("note", params.note)
          .execute()
        handle
          // language=sql
          .createUpdate("""insert into event(id, car_id, odo, price, note, event_date, created_at, updated_at)
                          |values (:event_id, :car_id, :odo, :price, '', :event_date, current_timestamp, current_timestamp)
                          |""".stripMargin)
          .bind("event_id", eventId)
          .bind("car_id", carId)
          .bind("odo", params.odo.getOrElse(0)) // Assumes 0 if not specified
          .bind("price", params.price.orNull)
          .bind("event_date", params.eventDate)
          .execute()
      }
    }
  yield WriteCarResult(
    carId.toString,
    eventId.toString
  )
