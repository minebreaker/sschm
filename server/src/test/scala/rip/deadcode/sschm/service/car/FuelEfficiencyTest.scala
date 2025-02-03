package rip.deadcode.sschm.service.car

import cats.effect.unsafe.IORuntime
import org.scalatest.funspec.AnyFunSpec
import rip.deadcode.sschm.model.db.Refuel
import rip.deadcode.sschm.service.car.FuelEfficiencyTest.refuel

import java.time.ZonedDateTime

class FuelEfficiencyTest extends AnyFunSpec:

  implicit val catsRuntime: IORuntime = IORuntime.global

  describe("calcFuelEfficiency") {
    it("should report accurate efficiencies") {
      val params = Seq(
        refuel(100, 10, 100),
        refuel(200, 10, 100),
        refuel(300, 10, 100)
      )
      val result = calcFuelEfficiency(params).unsafeRunSync()

      assert(result.effective)
      assert(result.efficiency == "10.0")
      assert(result.lastEfficiency == "10.0")
      assert(result.totalPrice == 30)
      assert(result.pricePerDistance == "10")
    }

    it("should correctly process noPreviousRefuel flags") {
      val params = Seq(
        refuel(100, 10, 100),
        refuel(300, 10, 100, true),
        refuel(400, 10, 100, true),
        refuel(500, 10, 100)
      )
      val result = calcFuelEfficiency(params).unsafeRunSync()

      assert(result.effective)
      assert(result.efficiency == "10.0")
      assert(result.lastEfficiency == "10.0")
      assert(result.totalPrice == 20, "Price for noPreviousRefuel is ignored")
      assert(result.pricePerDistance == "10")
    }

    it("more realistic examples") {
      val params = Seq(
        refuel(100, 1800, 49),
        refuel(195, 2000, 48),
        refuel(280, 1500, 43),
        refuel(500, 1400, 42, true),
        refuel(590, 1800, 42)
      )
      val result = calcFuelEfficiency(params).unsafeRunSync()

      assert(result.effective)
      assert(result.efficiency == "20.3")
      assert(result.lastEfficiency == "21.4")
      assert(result.totalPrice == 7100)
      assert(result.pricePerDistance == "1919")
    }
  }

object FuelEfficiencyTest:
  private val dummyDate = ZonedDateTime.parse("2000-10-10T00:00:00+00:00")
  private def refuel(odo: Int, price: Int, amount: Int, noPreviousRefuel: Boolean = false): Refuel = Refuel(
    "dummy",
    "dummy",
    Some(odo),
    Some(price),
    "",
    Some(amount),
    noPreviousRefuel,
    dummyDate,
    dummyDate,
    dummyDate
  )

  private def refuel(
      odo: Option[Int],
      price: Option[Int],
      amount: Option[Int],
      noPreviousRefuel: Boolean
  ): Refuel = Refuel(
    "dummy",
    "dummy",
    odo,
    price,
    "",
    amount,
    noPreviousRefuel,
    dummyDate,
    dummyDate,
    dummyDate
  )
