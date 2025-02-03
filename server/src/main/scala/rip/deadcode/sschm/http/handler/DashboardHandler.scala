package rip.deadcode.sschm.http.handler

import cats.effect.IO
import org.eclipse.jetty.server.Request
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}

import scala.util.matching.compat.Regex

object DashboardHandler extends HttpHandler:

  override def url: Regex = "^/$".r
  override def method: String = "GET"
  override def handle(request: Request, ctx: AppContext): IO[HttpResponse] = ???
