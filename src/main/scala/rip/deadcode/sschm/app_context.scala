package rip.deadcode.sschm

import com.github.jknack.handlebars.Handlebars
import org.jdbi.v3.core.Jdbi

trait AppContext {
  val config: Config
  val jdbi: Jdbi
  val handlebars: Handlebars
}

case class AppContextImpl(
    config: Config,
    jdbi: Jdbi,
    handlebars: Handlebars
) extends AppContext
