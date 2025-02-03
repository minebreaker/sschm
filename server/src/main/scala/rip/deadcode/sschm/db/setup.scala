package rip.deadcode.sschm.db

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.flywaydb.core.Flyway
import rip.deadcode.sschm.{Config, DatabaseConfig}

import javax.sql.DataSource
import scala.util.chaining.scalaUtilChainingOps

def createDataSource(config: Config): DataSource =
  val dbConf = config.database
  // Should use sttp uri builder?
  val jdbcUrl = s"jdbc:postgresql://${dbConf.host}:${dbConf.port}/${dbConf.database}"

  val hikariConfig = HikariConfig()
    .tap(_.setJdbcUrl(jdbcUrl))
    .tap(_.setUsername(dbConf.username))
    .tap(_.setPassword(dbConf.password))
  HikariDataSource(hikariConfig)

def setupFlyway(dataSource: DataSource, config: DatabaseConfig): Unit =
  val flyway = Flyway
    .configure()
    .cleanDisabled(!config.cleanDatabaseOnStartup)
    .dataSource(dataSource)
    .load()
  if (config.cleanDatabaseOnStartup) {
    flyway.clean()
  }
  flyway.migrate()
