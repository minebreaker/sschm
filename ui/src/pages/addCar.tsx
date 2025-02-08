import { ChangeEvent, useCallback, useEffect, useState } from "react"
import { useRequest, UseRequestState } from "../lib/net"
import { useRouter } from "react-router5"
import { Route } from "router5"

export function AddCar() {

  const router = useRouter()

  const [name, setName] = useState("")
  const onChangeName = useCallback((e: ChangeEvent<HTMLInputElement>) => setName(e.target.value), [])
  const [odo, setOdo] = useState("")
  const onChangeOdo = useCallback((e: ChangeEvent<HTMLInputElement>) => setOdo(e.target.value), [])
  const [price, setPrice] = useState("")
  const onChangePrice = useCallback((e: ChangeEvent<HTMLInputElement>) => setPrice(e.target.value), [])
  const [eventDate, setEventDate] = useState("")
  const onChangeEventDate = useCallback((e: ChangeEvent<HTMLInputElement>) => setEventDate(e.target.value), [])
  const [note, setNote] = useState("")
  const onChangeNote = useCallback((e: ChangeEvent<HTMLTextAreaElement>) => setNote(e.target.value), [])

  const carPost = useRequest<CarPostResponse>("/api/car")
  const handleSubmit = useCallback(() => {
    const request: CarPostRequest = {
      name,
      odo: odo ? Number.parseInt(odo) : undefined,
      price: price ? Number.parseInt(price) : undefined,
      eventDate: eventDate, // FIXME
      note: note ? note : undefined
    }
    carPost.submit(request)
  }, [carPost, name, odo, price, eventDate, note])

  useEffect(() => {
    if (carPost.type === UseRequestState.Completed) {
      router.navigate("car", { id: carPost.data.id })
    }
  }, [carPost.type]);

  return (
    <div>
      <h1>Add a new car</h1>

      {carPost.type === UseRequestState.Error && (
        <div>
          <h2>Error</h2>
          <p>{carPost.error}</p>
        </div>
      )}

      <form>
        {/* FIXME: use useId */}
        <label htmlFor="name">Car name</label>
        <input type="text" id="name" value={name} onChange={onChangeName} placeholder="Your car name" />
        <label htmlFor="odo">Odo (optional)</label>
        <input type="number" id="odo" value={odo} onChange={onChangeOdo} placeholder="0" />
        <label htmlFor="price">Price (optional)</label>
        <input type="number" id="price" value={price} onChange={onChangePrice} placeholder="0" />
        <label htmlFor="event_date">Purchase date</label>
        <input type="datetime-local" id="event_date" value={eventDate} onChange={onChangeEventDate} />
        <label htmlFor="note">Note (optional)</label>
        <textarea id="note" value={note} onChange={onChangeNote}></textarea>

        <button type="submit"
                onClick={handleSubmit}
                disabled={carPost.type === UseRequestState.Loading}>
          Submit
        </button>
      </form>
    </div>
  )
}

export const AddCarRoute = {
  name: 'addCar',
  path: "/car/add"
} as const satisfies Route
