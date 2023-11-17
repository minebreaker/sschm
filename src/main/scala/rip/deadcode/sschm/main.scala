package rip.deadcode.sschm

import cats.effect.unsafe.IORuntime
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.helper.ConditionalHelpers
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.google.common.net.MediaType
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.{Request, Server, ServerConnector}
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.jdbi.v3.core.Jdbi
import org.slf4j.LoggerFactory
import rip.deadcode.sschm.db.{createDataSource, setupFlyway}
import rip.deadcode.sschm.http.HttpResponse.{BinaryHttpResponse, EmptyHttpResponse, StringHttpResponse}
import rip.deadcode.sschm.http.handler.{
  CarGetHandler,
  CarPostFormHandler,
  CarPostHandler,
  PhotoGetHandler,
  PhotoPostHandler,
  ResourceHandler
}
import rip.deadcode.sschm.http.{HelloWorldHandler, HttpResponse, NotFoundHandler}
import rip.deadcode.sschm.lib.jdbi.{ConstructorRowMapperFactoryDelegator, OptionColumnMapperFactory}

import javax.sql.DataSource
import scala.util.chaining.scalaUtilChainingOps

@main
def main(): Unit =
  runServer()

private val logger = LoggerFactory.getLogger("rip.deadcode.sschm")

def runServer(): Unit =

  logger.info("Sschm")

  val config = readConfig()
  val dataSource = createDataSource(config)
  setupFlyway(dataSource, config.database)

  logger.debug("Config: {}", config)

  val appCtx = AppContextImpl(
    config,
    createJdbi(dataSource),
    createHandlebars()
  )

  val threadPool = QueuedThreadPool()
    .tap(_.setName("server"))
  val server = Server(threadPool)
  val connector = ServerConnector(server)
    .tap(_.setPort(config.port))
  server.addConnector(connector)

  server.setHandler(
    new AbstractHandler:
      override def handle(
          target: String,
          baseRequest: Request,
          request: HttpServletRequest,
          response: HttpServletResponse
      ): Unit =

        implicit val catsRuntime: IORuntime = IORuntime.global

        logger.debug(s"Target url: $target")
        val targetHandler = handlers
          .find(h => baseRequest.getMethod == h.method && h.url.matches(target))
          .getOrElse(NotFoundHandler)
        val result =
          try targetHandler.handle(baseRequest, appCtx).handleError(handlerUnexpected).unsafeRunSync()
          catch
            case e: Exception =>
              logger.info("Error outside the IO")
              handlerUnexpected(e)

        response.setStatus(result.status)
        result.header.foreach { case (name, value) =>
          response.setHeader(name, value)
        }
        result match {
          case StringHttpResponse(_, contentType, body, _) =>
            logger.debug(s"Response: string")
            response.setContentType(contentType.toString)
            response.getWriter.print(body)
          case BinaryHttpResponse(_, contentType, body, _) =>
            logger.debug("Response: binary")
            response.setContentType(contentType.toString)
            response.getOutputStream.write(body)
          case EmptyHttpResponse(_, _) =>
            logger.debug("Response: empty body")
        }
        baseRequest.setHandled(true)
  )
  server.start()

private val handlers = Seq(
  HelloWorldHandler,
  ResourceHandler,
  CarPostFormHandler,
  CarPostHandler,
  CarGetHandler,
  PhotoGetHandler,
  PhotoPostHandler
)

private def handlerUnexpected(e: Throwable) =
  logger.warn("Unhandled exception", e)
  StringHttpResponse(
    status = 500,
    contentType = MediaType.HTML_UTF_8,
    "<h1>500 Internal Server Error</h1>"
  )

private def createJdbi(dataSource: DataSource): Jdbi =
  Jdbi
    .create(dataSource)
    .registerRowMapper(new ConstructorRowMapperFactoryDelegator())
    .registerColumnMapper(new OptionColumnMapperFactory())

private def createHandlebars(): Handlebars =
  val loader = ClassPathTemplateLoader("/template", ".hbs")
  Handlebars(loader).registerHelper("eq", ConditionalHelpers.eq)
