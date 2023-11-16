package rip.deadcode.sschm.service.car

import cats.effect.IO
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.model.db.{Car, Event, EventImpl, MaintenanceEvent, Refuel}

import java.time.ZonedDateTime
import scala.jdk.CollectionConverters.*

case class ReadCarResult(
    car: Car,
    events: Seq[Event]
)

def readCar(ctx: AppContext)(carId: String): IO[ReadCarResult] =
  for result <- IO.blocking {
      ctx.jdbi.withHandle { handle =>
        val car = handle
          // language=sql
          .createQuery("select id, name, photo_id, note, created_at, updated_at from car where id::text = :id")
          .bind("id", carId)
          .mapTo(classOf[Car])
          .one()

        val events = handle
          // language=sql
          .createQuery("""select id, car_id, odo, price, note, event_date, created_at, updated_at
                         |from event
                         |where car_id::text = :car_id
                         |""".stripMargin)
          .bind("car_id", carId)
          .mapTo(classOf[EventImpl])
          .list()
          .asScala
          .toList

        val refuels = handle
          // language=sql
          .createQuery("""select id, car_id, odo, price, note, amount, no_previous_refuel, event_date, created_at, updated_at
                         |from refuel
                         |where car_id::text = :car_id
                         |""".stripMargin)
          .bind("car_id", carId)
          .mapTo(classOf[Refuel])
          .list()
          .asScala
          .toList

        val maintenanceEvents = handle
          // language=sql
          .createQuery("""select id, car_id, odo, note, price, maintenance_id, event_date, created_at, updated_at
                         |from maintenance_event
                         |where car_id::text = :car_id
                         |""".stripMargin)
          .bind("car_id", carId)
          .mapTo(classOf[MaintenanceEvent])
          .list()
          .asScala
          .toList

        val es = (events ++ refuels ++ maintenanceEvents).sorted
        ReadCarResult(car, es)
      }
    }
  yield result

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
