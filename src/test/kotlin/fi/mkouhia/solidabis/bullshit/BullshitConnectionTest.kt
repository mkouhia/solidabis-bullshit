package fi.mkouhia.solidabis.bullshit

import io.kotlintest.specs.AnnotationSpec
import io.ktor.client.HttpClient
import io.ktor.client.engine.jetty.Jetty
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlin.test.*

internal class BullshitConnectionTest: AnnotationSpec() {

    val client = HttpClient(Jetty) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    val secretUrl = "https://koodihaaste-api.solidabis.com/secret"

    @Test
    suspend fun getBullshitsFromURL() {
        val b = BullshitConnection.fromSecretUrl(secretUrl, client)
        assertTrue(b.bullshits.isNotEmpty())
    }

    @Test
    suspend fun getBullshitsFromSecret() {
        val s = Secret.fromSecretUrl(secretUrl, client)
        val b = BullshitConnection.fromSecret(s, client)
        assertTrue(b.bullshits.isNotEmpty())
    }

    @Test
    suspend fun failIncorrectURL() {
        assertFails("Nonexistent URL should throw error") {
            BullshitConnection.fromSecretUrl(secretUrl + "foo", client)
        }
    }
}
