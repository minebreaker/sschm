import { Route } from "router5"
import { DashboardRoute } from "../pages/dashboard"
import { CarRoute } from "../pages/car"
import { AddCarRoute } from "../pages/addCar"
import { RefuelRoute } from "../pages/refuel"


const ConstantRoutes = [
  DashboardRoute,
  CarRoute,
  AddCarRoute,
  RefuelRoute
] as const

export type RouteNames =  typeof ConstantRoutes[number]["name"]

// Readonly problem. Just casts.
export const Routes: Route[] = ConstantRoutes as unknown as Route[]
