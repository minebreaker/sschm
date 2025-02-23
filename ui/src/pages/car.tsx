import { useFetch, UseFetchState } from "../lib/net"
import { LoadingPage } from "../components/loading"
import { Route } from "router5"
import { Link } from "react-router5"
import { InfoPanel } from "../components/panel"
import { CarGetResponse, CarGetResponseEvent, CarGetResponseEventType } from "../types/api/car"
import { createUseStyles } from "react-jss"
import { RefuelRoute } from "./refuel"
import { DateTime } from "luxon"


const useStyles = createUseStyles({
  image: {
    maxWidth: "80vw",
    maxHeight: "40vh",
    objectFit: "cover"
  },
  imageContainer: {
    display: "flex",
    justifyContent: "center"
  },
  th: {
    // minimize header size
    width: 0
  },
  events: {
    paddingTop: "2em"
  },
  eventHr: {
    borderTopColor: "#e1e1e1"
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
        <div className={classes.imageContainer}>
          {car.data.photoId && <img src={`/api/photo/${car.data.photoId}`}
                                    className={classes.image}
                                    alt="car photo" />}
        </div>
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
        {car.data.events.map((e, i) => (
          <div key={e.id}>
            {i !== 0 && <hr className={classes.eventHr} />}
            <Event event={e} />
          </div>
        ))}
      </div>
    </div>
  )
}

const useEventStyles = createUseStyles({
  head: {
    display: "flex",
    justifyContent: "space-between"
  },
  odoAndDateContainer: {
    display: "flex",
    flexDirection: "column"
  },
  odoAndDate: {
    display: "flex",
    justifyContent: "end"
  }
})

function Event({ event }: { event: CarGetResponseEvent }) {
  const classes = useEventStyles()

  return (
    <>
      {event.type === CarGetResponseEventType.Event && (
        <>
          <div className={classes.head}>
            <h4>Event</h4>
            <div className={classes.odoAndDateContainer}>
              <Date className={classes.odoAndDate} date={event.eventDate} />
              <p className={classes.odoAndDate}>{event.odo}</p>
            </div>
          </div>
          {event.note && (
            <p>
              {event.note}
            </p>
          )}
        </>
      )}
      {event.type === CarGetResponseEventType.Maintenance && (
        <>
          <div className={classes.head}>
            <h4>Maintenance</h4>
            <div className={classes.odoAndDateContainer}>
              <Date className={classes.odoAndDate} date={event.eventDate} />
              <p className={classes.odoAndDate}>{event.odo}</p>
            </div>
          </div>
          {event.note && (
            <p>
              {event.note}
            </p>
          )}
        </>
      )}
      {event.type === CarGetResponseEventType.Refuel && (
        <>
          <div className={classes.head}>
            <h4>Refuel</h4>
            <div className={classes.odoAndDateContainer}>
              <Date className={classes.odoAndDate} date={event.eventDate} />
              <p className={classes.odoAndDate}>{event.odo}</p>
            </div>
          </div>
          <p>
            {event.amount} - {event.price}
          </p>
          {event.note && (
            <p>
              {event.note}
            </p>
          )}
        </>
      )}
    </>
  )
}

function Date(props: { className: string, date: string }) {
  const maybeDate = DateTime.fromISO(props.date)
  if (maybeDate.isValid) {
    return (
      <p className={props.className}>
        {maybeDate.toFormat("yyyy-LL-dd HH:mm")}
      </p>
    )
  } else {
    console.error(`Invalid date format: ${props.date}`)
    return (
      <p className={props.className}>{props.date}</p>
    )
  }
}

export const CarRoute = {
  name: 'car',
  path: "/car/:id?refueled"
} as const satisfies Route
