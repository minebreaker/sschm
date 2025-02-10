import { useFetch, UseFetchState } from "../lib/net"
import { LoadingPage } from "../components/loading"
import { Route } from "router5"
import { Link } from "react-router5"
import { InfoPanel } from "../components/panel"
import { CarGetResponse, CarGetResponseEventType } from "../types/api/car"
import { createUseStyles } from "react-jss"
import { RefuelRoute } from "./refuel"


const useStyles = createUseStyles({
  th: {
    // minimize header size
    width: 0
  },
  events: {
    paddingTop: "2em"
  }
})

export function Car(props: { id: string, refueled?: boolean }) {

  const classes = useStyles()

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
        <table>
          <tbody>
          <tr>
            <th className={classes.th}>Odo</th>
            <td>{car.data.odo}</td>
          </tr>
          <tr>
            <th className={classes.th}>Efficiency</th>
            <td>{car.data.efficiency}</td>
          </tr>
          </tbody>
        </table>
        {car.data.note && <p>{car.data.note}</p>}
      </div>

      <p>
        <Link routeName={RefuelRoute.name} routeParams={{ carId: props.id }}>Refuel</Link>
      </p>

      <div className={classes.events}>
        <h2>Events</h2>

        {car.data.events.map(e => (
          <div key={e.id}>
            {e.type === CarGetResponseEventType.Event && (
              <>
                <h4>Event</h4>
                <p>{e.eventDate}</p>
              </>
            )}
            {e.type === CarGetResponseEventType.Maintenance && (
              <>
                <h4>Maintenance</h4>
                <p>{e.eventDate}</p>
              </>
            )}
            {e.type === CarGetResponseEventType.Refuel && (
              <>
                <h4>Refuel</h4>
                <p>{e.eventDate}</p>
              </>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}

export const CarRoute = {
  name: 'car',
  path: "/car/:id?refueled"
} as const satisfies Route
