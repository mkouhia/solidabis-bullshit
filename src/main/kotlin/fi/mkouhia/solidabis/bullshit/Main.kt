package fi.mkouhia.solidabis.bullshit

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    val secretUrl = "https://koodihaaste-api.solidabis.com/secret"

    val bullshitConnection = runBlocking {
        HttpClient(Apache) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }.use {
            BullshitConnection.fromSecretUrl(secretUrl, it)
        }
    }

    println("# No bullshit\n")
    bullshitConnection.notBullshits
        .forEach {
            println("%5.1f %% : ${it.bestCandidate.content}".format(100 * it.bestCandidate.finnishProbability))
        }

    println("\n\n# Bullshit\n")
    bullshitConnection.actualBullshits
        .forEach {
            println("%5.1f %% : ${it.bestCandidate.content}".format(100 * it.bestCandidate.finnishProbability))
        }

}
