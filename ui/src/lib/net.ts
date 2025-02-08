import { useCallback, useEffect, useReducer, useRef } from "react"

export type UseFetchResult<T> = {
  type: "loading"
  isLoading: boolean,
} | {
  type: "error"
  error: string,
} | {
  type: "completed"
  data: T,
}

export const UseFetchState = {
  Loading: "loading",
  Error: "error",
  Completed: "completed"
} as const

type UseFetchState<T> = {
  type: "loading"
} | {
  type: "error"
  error: string
} | {
  type: "completed",
  data: T
}

const UseFetchAction = {
  Error: "error",
  Completed: "complete"
} as const

type UseFetchAction<T> = {
  type: "error"
  error: string
} | {
  type: "complete"
  data: T
}


function useFetchReducer<T>(state: UseFetchState<T>, action: UseFetchAction<T>): UseFetchState<T> {
  switch (action.type) {
    case  UseFetchAction.Error:
      return { type: "error", error: action.error }
    case UseFetchAction.Completed:
      return { type: "completed", data: action.data }
  }
}

export function useFetch<T>(url: string): UseFetchResult<T> {

  const isMounted = useRef(false)

  useEffect(() => {
    isMounted.current = true
    return () => {isMounted.current = false}
  }, []);

  const [s, dispatch] = useReducer(useFetchReducer<T>, { type: "loading" })

  useEffect(() => {
    (async () => {
      try {
        const result = await fetch(url)
        if (result.ok) {
          const body = await result.json()
          if (isMounted.current) {
            dispatch({ type: "complete", data: body })
          }
        } else {
          if (isMounted.current) {
            const body = await result.json()
            console.warn(body)
            dispatch({ type: "error", error: "Failed to fetch data." })
          }
        }
      } catch (e) {
        if (isMounted.current) {
          console.warn(e)
          dispatch({ type: "error", error: "Failed to fetch data." })
        }
      }
    })().then()
  }, [url]);

  switch (s.type) {
    case UseFetchState.Loading:
      return { type: "loading", isLoading: true }
    case UseFetchState.Error:
      return { type: "error", error: s.error }
    case UseFetchState.Completed:
      return { type: "completed", data: s.data }
  }
}


export type UseRequestResult<T> = {
  submit: (request: any) => void
} & ({
  type: "init"
} | {
  type: "loading"
  isLoading: boolean,
} | {
  type: "error"
  error: string,
} | {
  type: "completed"
  data: T,
})

export const UseRequestState = {
  Init: "init",
  Loading: "loading",
  Error: "error",
  Completed: "completed"
} as const

type UseRequestState<T> = {
  type: "init",
} | {
  type: "loading"
} | {
  type: "error"
  error: string
} | {
  type: "completed",
  data: T
}

const UseRequestAction = {
  Loading: "loading",
  Error: "error",
  Completed: "complete"
} as const

type UseRequestAction<T> = {
  type: "loading"
} | {
  type: "error"
  error: string
} | {
  type: "complete"
  data: T
}


function reducer<T>(state: UseRequestState<T>, action: UseRequestAction<T>): UseRequestState<T> {
  switch (action.type) {
    case UseRequestAction.Loading:
      return { type: "loading" }
    case  UseFetchAction.Error:
      return { type: "error", error: action.error }
    case UseFetchAction.Completed:
      return { type: "completed", data: action.data }
  }
}

export function useRequest<T>(url: string): UseRequestResult<T> {

  const isMounted = useRef(false)

  useEffect(() => {
    isMounted.current = true
    return () => {isMounted.current = false}
  }, []);

  const [s, dispatch] = useReducer(reducer<T>, { type: "init" })

  const submit = useCallback((request: any) => {
    (async () => {
      if (!isMounted.current) {
        console.warn("Submit from an unmounted component")
        return
      }
      dispatch({ type: "loading" })

      try {
        const result = await fetch(url, {
          body: JSON.stringify(request),
          method: "POST",
          headers: { "Content-Type": "application/json" }
        })
        if (result.ok) {
          const body = await result.json()
          if (isMounted.current) {
            dispatch({ type: "complete", data: body })
          }
        } else {
          if (isMounted.current) {
            const body = await result.json()
            console.warn(body)
            dispatch({ type: "error", error: "Erroneous response." })
          }
        }
      } catch (e) {
        if (isMounted.current) {
          console.warn(e)
          dispatch({ type: "error", error: "Failed to fetch data." })
        }
      }


    })().then()
  }, [url])

  switch (s.type) {
    case UseRequestState.Init:
      return { submit, type: "init" }
    case UseRequestState.Loading:
      return { submit, type: "loading", isLoading: true }
    case UseRequestState.Error:
      return { submit, type: "error", error: s.error }
    case UseRequestState.Completed:
      return { submit, type: "completed", data: s.data }
  }
}
