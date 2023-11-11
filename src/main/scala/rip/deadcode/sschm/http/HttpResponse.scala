package rip.deadcode.sschm.http

import com.google.common.net.MediaType

sealed trait HttpResponse:
  val status: Int
  val header: Map[String, String]

object HttpResponse:
  case class StringHttpResponse(
      status: Int,
      contentType: MediaType,
      body: String,
      header: Map[String, String] = Map.empty
  ) extends HttpResponse

  case class BinaryHttpResponse(
      status: Int,
      contentType: MediaType,
      body: Array[Byte],
      header: Map[String, String] = Map.empty
  ) extends HttpResponse

  case class EmptyHttpResponse(
      status: Int,
      header: Map[String, String] = Map.empty
  ) extends HttpResponse
