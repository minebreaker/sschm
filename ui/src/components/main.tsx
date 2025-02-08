import { useRoute } from "react-router5"
import { Dashboard, DashboardRoute } from "../pages/dashboard"
import { Car, CarRoute } from "../pages/car"
import { AddCar, AddCarRoute } from "../pages/addCar"
import { RouteNames } from "../lib/routing"
import { Refuel, RefuelRoute } from "../pages/refuel"

export function Main() {
  return (
    <main>
      <Routing />
    </main>
  )
}

function Routing() {
  const route = useRoute()

  const name = route.route.name as RouteNames

  switch (name) {
    case DashboardRoute.name:
      return <Dashboard />
    case CarRoute.name:
      return <Car id={route.route.params["id"]} />
    case AddCarRoute.name:
      return <AddCar />
    case RefuelRoute.name:
      return <Refuel carId={route.route.params["id"]} />
    default:
      return <h1>404 Not Found</h1>
  }
}
