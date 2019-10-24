package fi.mkouhia.solidabis.bullshit

import fi.mkouhia.solidabis.bullshit.FrequencyDistribution.Companion.FinnishCharacters
import io.ktor.client.HttpClient
import io.ktor.client.engine.jetty.Jetty
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.coroutines.runBlocking
import java.util.stream.Collectors
import java.util.stream.IntStream

fun main(args: Array<String>) {
    val secretUrl = "https://koodihaaste-api.solidabis.com/secret"

    val bullshitConnection = runBlocking {
        HttpClient(Jetty) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }.use {
            BullshitConnection.fromSecretUrl(secretUrl, it)
        }
    }


    println(bullshitConnection.bullshits[0])
    val bs = bullshitConnection.bullshits[0]
    val msg = bs.message
    val bs2 = Bullshit(msg)


    println(bs2.bestCandidate())

    bullshitConnection.bullshits
        .map { it.bestCandidate() }
        .toList()
        .sortedBy { -it.monogramPValue }
        .forEach {
            println("${it.avgPValue} ${it.monogramPValue} ${it.syllableTypePValue} ${it.content}")
        }

//    println(bestCandidate(bullshitConnection.bullshits[0].candidates).avgPValue)

    println(FinnishCharacters.frequency)

//    val s = bullshitConnection.bullshits
//        .map {
//            bestCandidate(it.candidates)
//        }
//        .map { it.syllableTypes() }
//    println(s)
}
