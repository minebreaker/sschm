import { createUseStyles } from "react-jss"
import { Link } from "react-router5"
import { DashboardRoute } from "../pages/dashboard"
import { useCallback, useState } from "react"

const useStyles = createUseStyles({
  header: {
    position: "sticky",
    top: 0,
    display: "flex",
    justifyContent: "space-between",
    height: "4rem",
    backgroundColor: "#f4f5f6"
  },
  topLink: {
    display: "flex",
    alignItems: "center",
    marginLeft: "1rem",
    marginRight: "0"
  },
  topLinkLink: {
    color: "#5B5B66"
  },
  topLinkText: {
    margin: 0
  },
  menu: {
    display: "flex",
    alignItems: "center",
    marginRight: "1rem"
  },
  menuIcon: {
    fontSize: "3rem",
    margin: 0,
    cursor: "pointer"
  },
  menuItems: {
    top: "4rem",
    position: "absolute",  // Any better ways?
    //width: "100svw", // It's absolute, so using left and write instead
    left: "0",
    right: "0",
    display: "flex",
    flexDirection: "row",
    backgroundColor: "#f4f5f6",
    borderTop: "#5B5B66 solid 1px",
    borderBottom: "#5B5B66 solid 1px"
  },
  menuItem: {
    display: "flex",
    alignItems: "center",
    height: "4rem",
    marginLeft: "1rem"
  }
})

export function Header() {

  const classes = useStyles()

  const [open, setOpen] = useState(false)
  const handleToggleOpen = useCallback(() => {
    setOpen(open => !open)
  }, [])

  const handleClickOnMenu = useCallback(() => {
    // When something inside the menu is clicked, close the menu
    setOpen(false)
  }, [])

  return (
    <>
      <header className={classes.header}>
        <div className={classes.topLink}>
          <Link routeName={DashboardRoute.name} className={classes.topLinkLink}>
            <h3 className={classes.topLinkText}>SSCHM</h3>
          </Link>
        </div>
        <div className={classes.menu}>
          <p className={classes.menuIcon}
             onClick={handleToggleOpen}
             role="button">☰</p>
        </div>
      </header>
      {open && (
        <div className={classes.menuItems} onClick={handleClickOnMenu}>
          <div className={classes.menuItem}>
            <Link routeName={DashboardRoute.name}>
              <p>Top</p>
            </Link>
          </div>
        </div>
      )}
    </>
  )
}
