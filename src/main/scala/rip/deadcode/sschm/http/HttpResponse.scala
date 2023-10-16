package rip.deadcode.sschm.http

import com.google.common.net.MediaType

case class HttpResponse(
    status: Int,
    contentType: MediaType,
    body: String
)
