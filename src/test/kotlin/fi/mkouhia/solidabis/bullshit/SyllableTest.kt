package fi.mkouhia.solidabis.bullshit

import io.kotlintest.data.forall
import io.kotlintest.specs.StringSpec
import io.kotlintest.shouldBe
import io.kotlintest.tables.row

class SyllableTest : StringSpec() {

    init {
        "Content equals input in simple cases" {
            forall(
                row("hei"),
                row("äi"),
                row("huu")
            ) { Syllable(it).content shouldBe it }
        }

        "Non-letter content is trimmed away" {
            Syllable("-tuu.").content shouldBe "tuu"
        }

        "Syllable content is in lower case" {
            Syllable("Aa").content shouldBe "aa"
        }

        "Syllable type converts vowels to V" {
            Syllable("aeiouyåäö").type shouldBe "VVVVVVVVV"
        }

        "Syllable type converts consonants to C" {
            Syllable("bcdfghjklmnpqrstvwxz").type shouldBe "CCCCCCCCCCCCCCCCCCCC"
        }

        "Equal content syllables are equal" {
            Syllable("moi") shouldBe Syllable("moi")
        }
    }

}
