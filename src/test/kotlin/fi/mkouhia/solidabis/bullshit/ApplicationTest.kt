package fi.mkouhia.solidabis.bullshit

import io.kotlintest.matchers.string.shouldHaveMinLength
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication

class ApplicationTest: StringSpec({

    "Request at / should return something" {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                response.status() shouldBe HttpStatusCode.OK
                response.content shouldHaveMinLength 1
            }
        }
    }
})
