package fi.mkouhia.solidabis.bullshit

import io.kotlintest.specs.AnnotationSpec
import kotlin.test.*

internal class CandidateTest: AnnotationSpec() {

    private val testSentence = "Te-ki-jät sai-vat uh-rin ker-to-maan pank-ki-kort-tin-sa tun-nus-lu-vun se-kä pank-ki-ti-lin-sä käyt-tä-jä-tun-nuk-sen ja sa-la-sa-nan."
    private val testCandidate = Candidate(testSentence.replace("-", ""))

    @Test
    fun getWords() {
        val testWords: List<Word> = testSentence.replace(Regex("[.-]"), "")
            .split(" ")
            .map { Word(it) }
            .toList()
        assertEquals(testWords, testCandidate.words)
    }

    @Test
    fun getMonogramPValue() {
        assertEquals(0.7939670678991018, testCandidate.monogramPValue)
    }

    @Test
    fun getSyllableTypePValue() {
        assertEquals(0.7475388132823538, testCandidate.syllableTypePValue)
    }

    @Test
    fun getAvgPValue() {
        assertEquals(0.7707529405907279, testCandidate.avgPValue)
    }

    @Test
    fun syllableTypes() {
        val syllableTypes: Map<String, Int> = testSentence.replace(".", "")
            .replace(Characters.consonant, "C")
            .replace(Characters.vowel, "V")
            .split(" ", "-")
            .groupingBy { it }
            .eachCount()
            .toMap()
        assertEquals(syllableTypes, testCandidate.syllableTypes())
    }

    @Test
    fun getContent() {
        val content = testSentence.replace("-", "")
        assertEquals(content, testCandidate.content)
    }
}
