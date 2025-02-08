import { createUseStyles } from "react-jss"

const useStyles = createUseStyles({
  header: {
    display: "flex",
    justifyContent: "end"
  }
})

export function Header() {

  const classes = useStyles()

  return (
    <header className={classes.header}></header>
  )
}
