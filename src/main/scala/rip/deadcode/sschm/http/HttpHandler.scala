package rip.deadcode.sschm.http

import cats.effect.IO
import rip.deadcode.sschm.AppContext

import scala.util.matching.compat.Regex

trait HttpHandler:

  def url(): Regex
  def handle(url: String, ctx: AppContext): IO[HttpResponse]
