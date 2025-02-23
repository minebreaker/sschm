import { ChangeEvent, useCallback, useEffect, useId, useState } from "react"
import { useRequest, UseRequestState } from "../lib/net"
import { DateTime } from "luxon"

export function Refuel(props: {
  carId: string,
  onFinished: () => void
}) {

  const odoId = useId()
  const priceId = useId()
  const amountId = useId()
  const noPreviousRefuelId = useId()
  const eventDateId = useId()
  const noteId = useId()

  const [odo, setOdd] = useState("")
  const onChangeOdo = useCallback((e: ChangeEvent<HTMLInputElement>) => setOdd(e.target.value), [])
  const [price, setPrice] = useState("")
  const onChangePrice = useCallback((e: ChangeEvent<HTMLInputElement>) => setPrice(e.target.value), [])
  const [amount, setAmount] = useState("")
  const onChangeAmount = useCallback((e: ChangeEvent<HTMLInputElement>) => setAmount(e.target.value), [])
  const [noPreviousRefuel, setNoPreviousRefuel] = useState(false)
  const onChangeNoPreviousRefuel = useCallback((e: ChangeEvent<HTMLInputElement>) => setNoPreviousRefuel(e.target.checked), [])
  const [eventDate, setEventDate] = useState("")
  const onChangeEventDate = useCallback((e: ChangeEvent<HTMLInputElement>) => setEventDate(e.target.value), [])
  const [note, setNote] = useState("")
  const onChangeNote = useCallback((e: ChangeEvent<HTMLTextAreaElement>) => setNote(e.target.value), [])

  const refuelPost = useRequest<RefuelPostResponse>(`/api/car/${props.carId}/refuel`)
  const handleSubmit = useCallback(() => {
    console.log(odo)
    const request: RefuelPostRequest = {
      odo: odo ? Number.parseInt(odo) : undefined,
      price: price ? Number.parseInt(price) : undefined,
      note: note ? note : undefined,
      amount: Math.floor(Number.parseFloat(amount) * 100),
      eventDate, // FIXME
      noPreviousRefuel: noPreviousRefuel
    }
    console.log(request)
    refuelPost.submit(request)
  }, [refuelPost, odo, price, note, eventDate, noPreviousRefuel])

  useEffect(() => {
    if (refuelPost.type === UseRequestState.Completed) {
      props.onFinished()
    }
  }, [refuelPost.type, props.onFinished]);

  useEffect(() => {
    const now = DateTime.now()
    setEventDate(now.toISO())
  }, [])

  return (
    <>
      {refuelPost.type === UseRequestState.Error && (
        <div>
          <h2>Error</h2>
          <p>{refuelPost.error}</p>
        </div>
      )}

      <form>
        <label htmlFor={odoId}>Odo (optional)</label>
        <input type="number" id={odoId} value={odo} onChange={onChangeOdo} placeholder="0" />
        <label htmlFor={priceId}>Price (optional)</label>
        <input type="number" id={priceId} value={price} onChange={onChangePrice} placeholder="0" />
        <label htmlFor={eventDateId}>Date</label>
        <input type="datetime-local" id={eventDateId} value={eventDate} onChange={onChangeEventDate} />
        <label htmlFor={amountId}>Amount</label>
        <input type="number" id={amountId} value={amount} onChange={onChangeAmount} placeholder="0.0" />
        <div>
          <input type="checkbox" id={noPreviousRefuelId} checked={noPreviousRefuel} onChange={onChangeNoPreviousRefuel} />
          <label htmlFor={noPreviousRefuelId} className="label-inline">Forgot to enter previous refueling</label>
        </div>
        <label htmlFor={noteId}>Note (optional)</label>
        <textarea id={noteId} value={note} onChange={onChangeNote}></textarea>

        <button type="submit"
                onClick={handleSubmit}
                disabled={refuelPost.type === UseRequestState.Loading}>
          Submit
        </button>
      </form>
    </>
  )
}
