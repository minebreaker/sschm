import { useFetch, UseFetchState } from "../lib/net"
import { LoadingPage } from "../components/loading"
import { Route } from "router5"
import { Link } from "react-router5"
import { InfoPanel } from "../components/panel"

export function Car(props: { id: string, refueled?: boolean }) {

  const car = useFetch<CarGetResponse>(`/api/car/${props.id}`)

  if (car.type === UseFetchState.Loading) {
    return <LoadingPage />
  }

  if (car.type === UseFetchState.Error) {
    return (
      <div>
        <p>Failed to load data.</p>
      </div>
    )
  }

  return (
    <div>
      <h1>{car.data.name}</h1>

      {props.refueled && (
        <InfoPanel>
          New refueling is added.
        </InfoPanel>
      )}

      <div>
        {car.data.photoId && <img src={`/api/photo/${car.data.photoId}`} alt="car photo" />}
        {car.data.note && <p>{car.data.note}</p>}
      </div>

      <Link routeName="refuel" routeParams={{ carId: props.id }}>Refuel</Link>

      <div>
        <h2>Events</h2>
        TODO
      </div>
    </div>
  )
}

export const CarRoute = {
  name: 'car',
  path: "/car/:id?refueled"
} as const satisfies Route
