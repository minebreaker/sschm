import { PropsWithChildren } from "react"
import { createUseStyles } from "react-jss"

const useStyles = createUseStyles({
  panel: {
    backgroundColor: "#f4f5f6"
  }
})

export function InfoPanel(props: PropsWithChildren) {

  const classes = useStyles()

  return (
    <div className={classes.panel}>
      {props.children}
    </div>
  )
}
