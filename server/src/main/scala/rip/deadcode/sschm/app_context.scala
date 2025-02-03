package rip.deadcode.sschm

import org.jdbi.v3.core.Jdbi

trait AppContext {
  val config: Config
  val jdbi: Jdbi
}

case class AppContextImpl(
    config: Config,
    jdbi: Jdbi
) extends AppContext
