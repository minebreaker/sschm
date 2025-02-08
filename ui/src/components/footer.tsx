import { createUseStyles } from "react-jss"

const useStyles = createUseStyles({
  footer: {
    display: "flex",
    justifyContent: "end"
  }
})

export function Footer() {

  const classes = useStyles()

  return (
    <footer className={classes.footer}>
      <a href="https://github.com/minebreaker/sschm">GitHub</a>
    </footer>
  )
}
