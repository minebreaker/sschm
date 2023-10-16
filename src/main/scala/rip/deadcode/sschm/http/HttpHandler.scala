package rip.deadcode.sschm.http

import cats.effect.IO

import scala.util.matching.compat.Regex

trait HttpHandler:

  def url(): Regex
  def handle(): IO[HttpResponse]
