import { useRoute } from "react-router5"
import { Dashboard, DashboardRoute } from "../pages/dashboard"
import { Car, CarRoute } from "../pages/car"
import { AddCar, AddCarRoute } from "../pages/addCar"
import { RouteNames } from "../lib/routing"
import { Refuel, RefuelRoute } from "../pages/refuel"
import { createUseStyles } from "react-jss"


const useStyles = createUseStyles({
  main: {
    padding: "2rem"
  }
})

export function Main() {

  const classes = useStyles()

  return (
    <main className={classes.main}>
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
      return <Car id={route.route.params["id"]}
                  refueled={route.route.params["refueled"]} />
    case AddCarRoute.name:
      return <AddCar />
    case RefuelRoute.name:
      return <Refuel carId={route.route.params["carId"]} />
    default:
      return <h1>404 Not Found</h1>
  }
}
