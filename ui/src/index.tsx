import React from "react"
import { createRoot } from "react-dom/client"

document.addEventListener('DOMContentLoaded', () => {
  const root = createRoot(document.getElementById("app")!!)
  root.render(<App />)
})

function App() {
  return (
    <div>
      <h1>hello world</h1>
    </div>
  )
}
