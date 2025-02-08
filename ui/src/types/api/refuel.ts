type RefuelPostRequest = {
  odo?: number,
  price?: number,
  note?: string,
  amount: number,
  noPreviousRefuel: boolean,
  eventDate: string
}

type RefuelPostResponse = {}
