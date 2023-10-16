package rip.deadcode.sschm

import cats.effect.unsafe.IORuntime
import com.google.common.net.MediaType
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.{Request, Server, ServerConnector}
import org.eclipse.jetty.util.thread.QueuedThreadPool
import rip.deadcode.sschm.db.{createDataSource, setupFlyway}
import rip.deadcode.sschm.http.{HelloWorldHandler, HttpResponse, NotFoundHandler}

import scala.util.chaining.scalaUtilChainingOps

@main
def main(): Unit =
  runServer()

def runServer(): Unit =

  val config = readConfig()
  val dataSource = createDataSource(config)
  setupFlyway(dataSource)

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

        val targetHandler = handlers.find(_.url().matches(target)).getOrElse(NotFoundHandler)
        val result = targetHandler.handle().handleError(handlerUnexpected).unsafeRunSync()

        response.setStatus(result.status)
        response.setContentType(result.contentType.toString)
        response.getWriter.print(result.body)
        baseRequest.setHandled(true)
  )
  server.start()

private val handlers = Seq(
  HelloWorldHandler
)

private def handlerUnexpected(e: Throwable) = HttpResponse(
  status = 500,
  contentType = MediaType.HTML_UTF_8,
  "<h1>500 Internal Server Error</h1>"
)
