package rip.deadcode.sschm.db

import cats.effect.{Concurrent, IO, Ref}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.flywaydb.core.Flyway
import org.postgresql.ds.{PGConnectionPoolDataSource, PGSimpleDataSource}
import rip.deadcode.sschm.Config

import java.net.URI
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

def setupFlyway(dataSource: DataSource): Unit =
  Flyway.configure().dataSource(dataSource)
    .load()
    .migrate()
