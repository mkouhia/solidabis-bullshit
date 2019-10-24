package fi.mkouhia.solidabis.bullshit

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
class Secret (val jwtToken: String, val bullshitUrl: String){

    companion object {

        /**
         * Get secret from URL
         *
         * @param secretUrl secret source
         * @param client HTTP client
         */
        suspend fun fromSecretUrl(secretUrl: String, client: HttpClient) : Secret {
            return client.get(secretUrl)
        }
    }

    override fun toString(): String {
        return "Secret(jwtToken='$jwtToken', bullshitUrl='$bullshitUrl')"
    }
}
