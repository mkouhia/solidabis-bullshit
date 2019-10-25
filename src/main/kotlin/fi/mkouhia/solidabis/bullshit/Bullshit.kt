package fi.mkouhia.solidabis.bullshit

import fi.mkouhia.solidabis.bullshit.FrequencyDistribution.Companion.FinnishCharacters
import fi.mkouhia.solidabis.bullshit.FrequencyDistribution.Companion.FinnishSyllables
import kotlinx.serialization.Serializable
import java.util.stream.Collectors
import java.util.stream.IntStream


/**
 * Container for string encrypted with Caesarean cipher
 *
 * @param message ciphered content
 */
@Serializable
class Bullshit(val message: String) {

    private val nLetters = 29

    /** Candidates for decrypted content */
    val candidates: List<Candidate> by lazy {
        IntStream.range(0, nLetters)
            .mapToObj { Candidate(rotateMessage(it)) }
            .collect(Collectors.toList())
    }

    override fun toString(): String {
        return "Bullshit(message='$message')"
    }

    /** Best candidate, by highest probability of being Finnish */
    val bestCandidate: Candidate by lazy {
        candidates
            .map {
                it to it.finnishProbability
            }
            .sortedBy { (_, value) -> -value }[0]
            .first
    }

    /** Best candidate is likely to be Finnish */
    val isLikelyFinnish: Boolean by lazy { bestCandidate.isLikelyFinnish }
    /** Probability of best candidate of being Finnish */
    val finnishProbability: Double by lazy { bestCandidate.finnishProbability }


    /**
     * Rotate content by N characters
     *
     * @param nChars amount of rotation
     * @return rotated string
     */
    private fun rotateMessage(nChars: Int): String {
        return message
            .toCharArray()
            .map {
                when {
                    !it.isLetter() -> it
                    else -> rotateChar(it, nChars)
                }
            }
            .joinToString("")
    }

    /**
     * Rotate character forward by N characters
     *
     * Implement Caesarean cipher, on alphabets A-Z + Å, Ä, Ö. Rotate character forward,
     * while keeping its case intact. Keep characters not included in the set as they are.
     *
     * @param c character to be rotated
     * @param nChars number of characters forward
     * @return resulting character
     */
    private fun rotateChar(c: Char, nChars: Int): Char {
        val charInd = when (c.toLowerCase()) {
            in 'a'..'z' -> c.toLowerCase() - 'a'
            'å' -> 26
            'ä' -> 27
            'ö' -> 28
            else -> -1
        }
        val newCharInd = when (charInd) {
            -1 -> -1
            else -> (charInd + nChars) % nLetters
        }
        val newChar: Char = when (newCharInd) {
            in 0..25 -> 'a' + newCharInd
            26 -> 'å'
            27 -> 'ä'
            28 -> 'ö'
            -1 -> c
            else -> throw AssertionError("Could not count character modulos: $c -> $newCharInd")
        }
        return if (c.isUpperCase()) newChar.toUpperCase() else newChar
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bullshit

        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }
}

/**
 * Candidate for decrypted content
 */
class Candidate(val content: String) {

    override fun toString(): String {
        return "Candidate(content='$content')"
    }

    /** List of words in the candidate string */
    val words: List<Word> by lazy {
        content
            .split(" ")
            .filter { it.isNotEmpty() }
            .map { Word(it) }
    }

    /** P-value that character distribution corresponds to Finnish */
    val monogramPValue: Double by lazy {
        ChiSquared(FinnishCharacters, thisLetterCounts(), 0.1).pValue()
    }

    /** P-value that syllable type distribution corresponds to Finnish */
    val syllableTypePValue: Double by lazy {
        ChiSquared(FinnishSyllables, syllableTypes(), 0.1).pValue()
    }

    /**
     * Declare if candidate is likely Finnish language
     *
     * TODO create better statistics
     */
    val isLikelyFinnish: Boolean by lazy { monogramPValue > 0.05 }

    /**
     * Probability of being Finnish language
     *
     * TODO create better statistics
     */
    val finnishProbability: Double by lazy { monogramPValue }


    /** Count letters in content */
    private fun thisLetterCounts(): Map<Char, Int> = content.toLowerCase().replace(Characters.notLetter, "")
        .groupingBy { it }.eachCount()

    /**
     * Count syllable types in a candidate string
     *
     * Syllable types are represented by strings having 'V' in place of vowels and 'C' in plase of consonants
     *
     * @return mapping, key: syllable type (e.g. CVC) to count of types in candidate
     */
    fun syllableTypes(): Map<String, Int> {
        return words
            .flatMap { word ->
                word.syllables.map {
                    it.type
                }
            }
            .groupingBy { it }.eachCount()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Candidate

        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }


}
