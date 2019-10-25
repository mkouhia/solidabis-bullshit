package fi.mkouhia.solidabis.bullshit

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import kotlinx.serialization.Serializable

@Serializable
class BullshitConnection(val bullshits: List<Bullshit>) {

    override fun toString(): String {
        return "BullshitConnection(bullshits=$bullshits)"
    }

    val notBullshits: List<Bullshit> by lazy {
        bullshits
            .filter { it.isLikelyFinnish }
            .toList()
            .sortedBy { -it.finnishProbability }
    }

    val actualBullshits: List<Bullshit> by lazy {
        bullshits
            .filter { !it.isLikelyFinnish }
            .toList()
            .sortedBy { -it.finnishProbability }
    }

    companion object {

        suspend fun fromSecret(secret: Secret, client: HttpClient): BullshitConnection {
            return client.get {
                url(secret.bullshitUrl)
                header("Authorization", "Bearer " + secret.jwtToken)
            }
        }

        suspend fun fromSecretUrl(secretUrl: String, client: HttpClient): BullshitConnection {
            return fromSecret(Secret.fromSecretUrl(secretUrl, client), client)
        }

    }
}
