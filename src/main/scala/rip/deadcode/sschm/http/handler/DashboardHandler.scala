package rip.deadcode.sschm.http.handler

import cats.effect.IO
import rip.deadcode.sschm.AppContext
import rip.deadcode.sschm.http.{HttpHandler, HttpResponse}

import scala.util.matching.compat.Regex

object DashboardHandler extends HttpHandler:

  override def url(): Regex = "^/$".r

  override def handle(url: String, ctx: AppContext): IO[HttpResponse] = ???
