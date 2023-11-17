package rip.deadcode.sschm.service.car

import cats.effect.IO
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.model.db.{Car, Event, EventImpl, MaintenanceEvent, Refuel}

import java.time.ZonedDateTime
import scala.jdk.CollectionConverters.*

case class ReadCarResult(
    car: Car,
    events: Seq[Event],
    efficiency: CalcFuelEfficiencyResult
)

def readCar(ctx: AppContext)(carId: String): IO[ReadCarResult] =
  for
    result <- IO.blocking {
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
          .createQuery(
            """select id, car_id, odo, price, note, amount, no_previous_refuel, event_date, created_at, updated_at
                         |from refuel
                         |where car_id::text = :car_id
                         |""".stripMargin
          )
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

        (car, es, refuels)
      }
    }
    (car, es, refuels) = result
    efficiency <- calcFuelEfficiency(refuels)
  yield ReadCarResult(car, es, efficiency)
