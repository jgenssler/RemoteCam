package com.samsung.android.scan3d.http

import com.samsung.android.scan3d.serv.CamEngine
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import java.io.OutputStream

class HttpService {
    lateinit var engine: NettyApplicationEngine
    var channel = Channel<ByteArray>(2)
    var camengine: CamEngine? = null
    fun producer(): suspend OutputStream.() -> Unit = {
        val o = this
        channel = Channel()
        channel.consumeEach {
            o.write("--FRAME\r\nContent-Type: image/jpeg\r\n\r\n".toByteArray())
            o.write(it)
            o.flush()
        }
    }
    public fun main() {
        engine = embeddedServer(Netty, port = 8080) {
            routing {
                get("/cam") {
                    call.respondText("Ok")
                }
                get("/cam.jpg") {
                    val image  = camengine?.captureCameraImage()!!
                    call.respondBytes(image, ContentType.Image.JPEG)
                }
                get("/cam.mjpeg") {
                    call.respondOutputStream(
                        ContentType.parse("multipart/x-mixed-replace;boundary=FRAME"),
                        HttpStatusCode.OK, producer()
                    )
                }
            }
        }
        engine.start(wait = false)
    }

}