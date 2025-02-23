export type CarGetResponse = {
  id: string
  name: string,
  photoId?: string,
  note: string,
  odo: string,
  efficiency: string,
  events: CarGetResponseEvent[]
}

export const CarGetResponseEventType = {
  Event: "event",
  Maintenance: "maintenance",
  Refuel: "refuel"
}

export type CarGetResponseEventType = typeof CarGetResponseEventType[keyof typeof CarGetResponseEventType]

export type CarGetResponseEvent = CarGetResponseEventEvent
  & CarGetResponseEventMaintenance
  & CarGetResponseEventRefuel

type CarGetResponseEventCommon = {
  id: string
  type: typeof CarGetResponseEventType.Event,
  carId: string,
  odo: string,
  price: string,
  note: string,
  eventDate: string
}

export type CarGetResponseEventEvent = CarGetResponseEventCommon & {
  type: typeof CarGetResponseEventType.Event,
}

export type CarGetResponseEventMaintenance = CarGetResponseEventCommon

export type CarGetResponseEventRefuel = CarGetResponseEventCommon & {
  amount: string,
  noPreviousRefuel: boolean
}

export type CarListResponse = {
  items: {
    id: string
    name: string,
    photoId?: string,
    note?: string,
  }[]
}


export type CarPostRequest = {
  name: string,
  odo?: number,
  price?: number
  eventDate: string
  note?: string,
  photoId?: string
}

export type CarPostResponse = {
  id: string
  name: string,
  photoId?: string,
  note: string,
}
