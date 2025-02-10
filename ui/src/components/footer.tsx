import { createUseStyles } from "react-jss"

const useStyles = createUseStyles({
  footer: {
    display: "flex",
    justifyContent: "space-between",
    height: "4rem",
    backgroundColor: "#f4f5f6"
  },
  leftSpacer: {
    display: "flex",
    alignItems: "center",
    marginLeft: "1rem",
    marginRight: "0"
  },
  link: {
    display: "flex",
    alignItems: "center",
    marginRight: "1rem"
  }
})

export function Footer() {

  const classes = useStyles()

  return (
    <footer className={classes.footer}>
      <div className={classes.leftSpacer} />
      <div className={classes.link}>
        <a href="https://github.com/minebreaker/sschm">GitHub</a>
      </div>
    </footer>
  )
}
