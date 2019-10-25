package fi.mkouhia.solidabis.bullshit

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.client.*
import io.ktor.client.engine.jetty.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.coroutines.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.thymeleaf.Thymeleaf
import io.ktor.thymeleaf.ThymeleafContent
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

object Settings {
    val secretUrl = "https://koodihaaste-api.solidabis.com/secret"

}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val client = HttpClient(Jetty) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }
    val bullshitConnection = runBlocking {
        BullshitConnection.fromSecretUrl(Settings.secretUrl, client)
    }


    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/thymeleaf/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }

    routing {
        get("/") {
            call.respond(ThymeleafContent("index", mapOf("bullshitConnection" to bullshitConnection)))
        }

    }
}

data class JsonSampleClass(val hello: String)

data class ThymeleafUser(val id: Int, val name: String)

