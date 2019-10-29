package fi.mkouhia.solidabis.bullshit

import io.kotlintest.matchers.string.shouldHaveMinLength
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.headersOf
import io.ktor.http.withCharset
import kotlin.test.assertFails

internal class SecretTest : StringSpec() {

    private val secretUrl = "https://koodihaaste-api.solidabis.com/secret"

    private val client = autoClose(
        HttpClient(Apache) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    )

    private val mockToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJidWxsc2hpdCI6ImJ1bGxzaGl0IiwiaWF0IjoxNTcxODM3MDg0fQ.rYvpIps3Eg7uPh7RHoB0Ffcc3a8BzfU1ibsQKbzcHIg"
    private val mockBullshitUrl = "https://koodihaaste-api.solidabis.com/bullshit"
    private val mockContent = """{"jwtToken":"$mockToken","bullshitUrl":"$mockBullshitUrl"}"""
    private val mockClient = autoClose(
        HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        mockContent,
                        headers = headersOf(
                            "content-type" to listOf(ContentType.Application.Json.withCharset(Charsets.UTF_8).toString()),
                            "content-length" to listOf("${mockContent.length}")
                        )
                    )
                }
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    )


    init {

        "Parameter jwtToken" {
            Secret("testToken", "").jwtToken shouldBe "testToken"
        }

        "Parameter bullshitUrl" {
            Secret("", "testURL").bullshitUrl shouldBe "testURL"
        }

        "Secret from live connection should have token and url" {
            val secret = Secret.fromSecretUrl(secretUrl, client)
            secret.jwtToken shouldHaveMinLength 1
            secret.bullshitUrl shouldHaveMinLength 1
        }

        "Secret from live connection should direct somewhere" {
            val secret = Secret.fromSecretUrl(secretUrl, client)
            val response = client.get<String>(secretUrl) {
                header("Authorization", "Bearer: " + secret.jwtToken)
            }
            response shouldHaveMinLength 1
        }

        "Nonexistent URL should throw error" {
            assertFails("Nonexistent URL should throw error") {
                Secret.fromSecretUrl("https://koodihaaste-api.solidabis.com/secre", client)
            }
        }

        "Secret from mocked content has same parameters" {
            val secret = Secret.fromSecretUrl(secretUrl, mockClient)
            secret.jwtToken shouldBe mockToken
            secret.bullshitUrl shouldBe mockBullshitUrl
        }

    }
}
