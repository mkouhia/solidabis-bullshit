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

    /**
     * TODO get best performing candidate
     *
     * @return best candidate, by lowest monogram P value
     */
    fun bestCandidate(): Candidate {
        return candidates
            .map {
                it to it.monogramPValue
            }
            .toList()
            .sortedBy { (_, value) -> -value }[0]
            .first
    }


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
    fun rotateChar(c: Char, nChars: Int): Char {
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
}

/**
 * Candidate for decrypted content
 */
class Candidate(val content: String) {

    override fun toString(): String {
        return "Candidate(message='$content')"
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
        ChiSquared(FinnishCharacters, thisLetterCounts(), 1.0).pValue()
    }

    /** P-value that syllable type distribution corresponds to Finnish */
    val syllableTypePValue: Double by lazy {
        ChiSquared(FinnishSyllables, syllableTypes(), 1.0).pValue()
    }

    val avgPValue: Double by lazy {
        (monogramPValue + syllableTypePValue) / 2
    }

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
}
