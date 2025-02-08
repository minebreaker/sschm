import { Refuel as RefuelC } from "../components/refuel"
import { Route } from "router5"

export function Refuel(props: { carId: string }) {

  return (
    <div>
      <h1>Refuel</h1>
      <hr />

      <RefuelC carId={props.carId} />
    </div>
  )
}

export const RefuelRoute = {
  name: "refuel",
  path: "/car/:id/refuel"
} as const satisfies Route
