import { useFetch, UseFetchState } from "../lib/net"
import { LoadingPage } from "../components/loading"
import { Link } from "react-router5"
import { Route } from "router5"

export function Dashboard() {

  const cars = useFetch<CarListResponse>(`/api/cars`)

  if (cars.type === UseFetchState.Loading) {
    return <LoadingPage />
  }

  if (cars.type === UseFetchState.Error) {
    return (
      <div>
        <p>Failed to load data.</p>
      </div>
    )
  }

  return (
    <div>
      <h1>Dashboard</h1>
      <hr />

      {cars.data.items.length === 0 && (
        <div>
          <p>You haven't registered any cars yet.</p>
          <Link routeName="addCar">Add</Link>
        </div>
      )}


      {cars.data.items.length !== 0 && cars.data.items.map(car => (
        <div key={car.id}>
          <Link routeName="car" routeParams={{ id: car.id }}>
            <h2>{car.name}</h2>
          </Link>
        </div>
      ))}
    </div>
  )
}

export const DashboardRoute = {
  name: "dashboard",
  path: "/"
} as const satisfies Route
