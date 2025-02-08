import { createRoot } from "react-dom/client"
import { createRouter } from "router5"
import { RouterProvider } from "react-router5"
import browserPlugin from 'router5-plugin-browser'
import { Main } from "./components/main"
import { Header } from "./components/header"
import { createUseStyles } from "react-jss"
import { Footer } from "./components/footer"
import { Routes } from "./lib/routing"

document.addEventListener('DOMContentLoaded', () => {

  const router = createRouter(Routes)
  router.usePlugin(browserPlugin())
  const url = new URL(location.href)
  router.start(url.pathname)

  const root = createRoot(document.getElementById("app")!!)
  root.render(
    //@ts-ignore
    <RouterProvider router={router}>
      <App />
    </RouterProvider>
  )
})

const useStyles = createUseStyles({
  main: {
    display: "grid",
    gridTemplateColumns: "auto 1fr auto",
    height: "100svh"
  }
})

function App() {

  const classes = useStyles()

  return (
    <div className={classes.main}>
      <Header />
      <Main />
      <Footer />
    </div>
  )
}
