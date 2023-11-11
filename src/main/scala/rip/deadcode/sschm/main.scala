package rip.deadcode.sschm

import cats.effect.unsafe.IORuntime
import com.google.common.net.MediaType
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.{Request, Server, ServerConnector}
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.jdbi.v3.core.Jdbi
import org.slf4j.LoggerFactory
import rip.deadcode.sschm.db.{createDataSource, setupFlyway}
import rip.deadcode.sschm.http.HttpResponse.{BinaryHttpResponse, StringHttpResponse}
import rip.deadcode.sschm.http.{HelloWorldHandler, HttpResponse, NotFoundHandler}

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
    Jdbi.create(dataSource)
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
        val targetHandler = handlers.find(_.url().matches(target)).getOrElse(NotFoundHandler)
        val result = targetHandler.handle(target, appCtx).handleError(handlerUnexpected).unsafeRunSync()

        response.setStatus(result.status)
        response.setContentType(result.contentType.toString)
        result match {
          case StringHttpResponse(_, _, body) =>
            logger.debug(s"Response: {}", body)
            response.getWriter.print(body)
          case BinaryHttpResponse(_, _, body) =>
            logger.debug("Response: binary")
            response.getOutputStream.write(body)
        }
        baseRequest.setHandled(true)
  )
  server.start()

private val handlers = Seq(
  HelloWorldHandler
)

private def handlerUnexpected(e: Throwable) = StringHttpResponse(
  status = 500,
  contentType = MediaType.HTML_UTF_8,
  "<h1>500 Internal Server Error</h1>"
)
