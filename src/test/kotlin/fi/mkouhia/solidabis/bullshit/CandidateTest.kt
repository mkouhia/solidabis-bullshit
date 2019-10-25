package fi.mkouhia.solidabis.bullshit

import io.kotlintest.shouldBe
import io.kotlintest.specs.AnnotationSpec
import io.kotlintest.specs.StringSpec
import kotlin.test.*

internal class CandidateTest: StringSpec() {

    private val testSentence = "Te-ki-jät sai-vat uh-rin ker-to-maan pank-ki-kort-tin-sa tun-nus-lu-vun se-kä pank-ki-ti-lin-sä käyt-tä-jä-tun-nuk-sen ja sa-la-sa-nan."
    private val testCandidate = Candidate(testSentence.replace("-", ""))

    init {
        "Word extraction from test sentence" {
            val testWords: List<Word> = testSentence.replace(Regex("[.-]"), "")
                .split(" ")
                .map { Word(it) }
                .toList()
            testCandidate.words shouldBe testWords
        }

        "Monogram p-value" {
            testCandidate.monogramPValue shouldBe 0.7187782866043061
        }

        "Syllable type p-value" {
            testCandidate.syllableTypePValue shouldBe 1.4657199898238105E-9
        }


        "Extraction of syllable types" {
            val syllableTypes: Map<String, Int> = testSentence.replace(".", "")
                .replace(Characters.consonant, "C")
                .replace(Characters.vowel, "V")
                .split(" ", "-")
                .groupingBy { it }
                .eachCount()
                .toMap()
            testCandidate.syllableTypes() shouldBe syllableTypes
        }

        "Content should be unchanged" {
            testCandidate.content shouldBe testSentence.replace("-", "")
        }

        "Candidate equality" {
            testCandidate shouldBe Candidate(testCandidate.content)
        }

        "Finnish candidate is likely Finnish" {
            testCandidate.isLikelyFinnish shouldBe true
        }

        "Non-Finnish candidate is not likely Finnish" {
            Candidate("tajs audrc ao saoeuh asuet akeonhdllr smoakklrcoe").isLikelyFinnish shouldBe false
        }

    }
}
