package rip.deadcode.sschm.http

import com.google.common.net.MediaType

sealed trait HttpResponse:
  val status: Int
  val contentType: MediaType

object HttpResponse:
  case class StringHttpResponse(
      status: Int,
      contentType: MediaType,
      body: String
  ) extends HttpResponse

  case class BinaryHttpResponse(
      status: Int,
      contentType: MediaType,
      body: Array[Byte]
  ) extends HttpResponse
