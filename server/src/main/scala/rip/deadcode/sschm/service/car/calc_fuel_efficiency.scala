package rip.deadcode.sschm.service.car

import cats.effect.IO
import rip.deadcode.sschm.model.db.Refuel
import rip.deadcode.sschm.service.car.CalcFuelEfficiencyResult.Unit

import java.time.ZonedDateTime
import scala.math.addExact

case class CalcFuelEfficiencyResult(
    effective: Boolean, // if values could be calculated
    efficiency: String,
    lastEfficiency: String,
    //    efficiencyUnit: UnitType, // TODO
    totalPrice: Int,
    pricePerDistance: String // price/km
)

object CalcFuelEfficiencyResult:
  val Unit = CalcFuelEfficiencyResult(false, "-", "-", 0, "-")

private case class Eff(
    distance: Int,
    amount: Int,
    price: Int
)

private val dummyDate = ZonedDateTime.parse("2000-10-10T00:00:00+00:00")
private val dummyRefuel =
  Refuel("dummy", "dummy", Some(0), Some(0), "", 0, false, dummyDate, dummyDate, dummyDate)

def calcFuelEfficiency(refuels: Seq[Refuel]): IO[CalcFuelEfficiencyResult] =

  if (refuels.isEmpty) {
    return IO.pure(Unit)
  }

  val records = (Seq(dummyRefuel) ++ refuels)
    // always have two or more refuels so that we don't need to care about small number seq
    .sliding(2)
    .flatMap {
      case Seq(
            Refuel(_, _, Some(previousOdo), _, _, _, _, _, _, _),
            Refuel(_, _, Some(odo), Some(price), _, amount, false, _, _, _)
          ) =>
        Some(Eff(odo - previousOdo, amount, price))
      case _ => None
    }
    .toList

  if (records.isEmpty) {
    return IO.pure(Unit)
  }

  // Car numbers are unlikely to overflow, so we use addExact but not handle exceptions
  val totalDistance = records.foldLeft(0)((acc, e) => addExact(acc, e.distance))
  val totalPrice = records.foldLeft(0)((acc, e) => addExact(acc, e.price))
  val totalAmounts = records.foldLeft(0)((acc, e) => addExact(acc, e.amount))

  if (totalDistance == 0) {
    return IO.raiseError(???)
  }

  // divide by 1/10 since amount is decilitre
  println(s"totalDistance: $totalDistance, totalPrice: $totalPrice, totalAmounts: $totalAmounts")
  val efficiency =
    (BigDecimal(totalDistance) / BigDecimal(totalAmounts) * 10).setScale(1, BigDecimal.RoundingMode.HALF_UP).toString()

  val lastRecord = records.last
  val lastEfficiency = (BigDecimal(lastRecord.distance) / BigDecimal(lastRecord.amount) * 10)
    .setScale(1, BigDecimal.RoundingMode.HALF_UP)
    .toString()

  // price per 100km
  val pricePerDistance =
    (BigDecimal(totalPrice) * 100 / BigDecimal(totalDistance)).setScale(0, BigDecimal.RoundingMode.HALF_UP).toString()

  IO.pure(CalcFuelEfficiencyResult(true, efficiency, lastEfficiency, totalPrice, pricePerDistance))
