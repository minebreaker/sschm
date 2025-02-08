type CarGetResponse = {
  id: string
  name: string,
  photoId?: string,
  note?: string,
  odo?: number,
  efficiency?: number,
  events: any[] // FIXME
}

type CarListResponse = {
  items: {
    id: string
    name: string,
    photoId?: string,
    note?: string,
  }[]
}


type CarPostRequest = {
  name: string,
  odo?: number,
  price?: number
  eventDate: string
  note?: string,
}

type CarPostResponse = {
  id: string
  name: string,
  photoId?: string,
  note: string,
}
