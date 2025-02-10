import { Refuel as RefuelC } from "../components/refuel"
import { Route } from "router5"
import { useCallback } from "react"
import { useRouter } from "react-router5"

export function Refuel(props: { carId: string }) {

  const router = useRouter()

  const onFinished = useCallback(() => {
    router.navigate("car", { id: props.carId, refueled: true })
  }, [router, props.carId])

  return (
    <div>
      <h1>Refuel</h1>

      <RefuelC carId={props.carId} onFinished={onFinished} />
    </div>
  )
}

export const RefuelRoute = {
  name: "refuel",
  path: "/car/:carId/refuel"
} as const satisfies Route
