package fi.mkouhia.solidabis.bullshit

private const val v = "[aeiouyåäö]" //Wovel
private const val c = "[bcdfghjklmnpqrstvwxz]" // Consonant

private const val vv = "(?:aa|ee|ii|oo|uu|yy|åå|ää|öö)" // Long wovel
private const val dd = "(?:ai|ei|oi|äi|öi|ey|äy|öy|au|eu|ou|ui|yi|iu|iy|ie|uo|yö)" // diphthong

private val consonantRule = Regex("($v$c*)($c$v)", RegexOption.IGNORE_CASE)
private val diphthongRule = Regex("($c*(?:$dd|$vv))($v)", RegexOption.IGNORE_CASE)

private val doubleVowel = Regex("$v{2}", RegexOption.IGNORE_CASE)
private val diphthong = Regex(dd, RegexOption.IGNORE_CASE)

private val firstSyllableDiphthongs: List<String> = listOf("ie", "uo", "yö")

private val trimNonLetter = Regex("(^[^a-zåäö]*|[^a-zåäö]*$)", RegexOption.IGNORE_CASE)

private val colonEndings = Regex(":(n|an|han|in|ssa|sta|stä|ssä|oon|lla|lta|lle|ta|seen|aa|aan|ään|ien|ina|eina|hin|kin|llä|ltä|iin|tä|ksi|tta|en|t|den|it|ia|hän|hyn|hun|na|een)$")

/**
 * A word in natural language
 *
 * @param rawContent string representation
 */
class Word(rawContent: String) {

    init {
        if (!rawContent.contains(colonEndings) && rawContent.contains(":")) {
            println(rawContent)
        }
    }

    val content = rawContent
        .replace(trimNonLetter, "")
        .replace(colonEndings, "")
        .toLowerCase()

    val syllables: List<Syllable> by lazy { breakSyllables(content) }


    override fun toString(): String {
        return "Word(string='$content')"
    }

    /**
     * Break word to syllables
     *
     * Following guidelines have been applied in this method
     * - http://www.kielitoimistonohjepankki.fi/haku/tavutus/ohje/153
     * - http://teppo.tv/haikueditori/tavutus.html
     *
     * @param thisString string to be broken into syllables
     * @return list of syllables in their appearing order
     */
    private fun breakSyllables(thisString: String): List<Syllable> {
        if (thisString.contains(Characters.notLetter)) {
            return thisString.split(Characters.notLetter).map { breakSyllables(it) }.flatten()
        }

        if (thisString.contains(consonantRule)) {
            return breakSyllables(thisString.replace(consonantRule) { m -> m.groups[1]?.value + "-" + m.groups[2]?.value })
        }

        if (thisString.contains(doubleVowel)) {
            val dashedWord: String = thisString.replace(doubleVowel) { m ->
                when {
                    // Long wovel - no break
                    m.value[0].toLowerCase() == m.value[1].toLowerCase() -> m.value
                    // Latter is 'i' - no break
                    m.value[1].toLowerCase() == 'i' -> m.value
                    // Diphthongs do not generally break
                    m.value.matches(diphthong) -> when {
                        // Speciol case: considered as diphthongs only in first syllable
                        !content.startsWith(thisString) && firstSyllableDiphthongs.contains(m.value) ->
                            m.value[0] + "-" + m.value[1]
                        else -> m.value
                    }
                    // Otherwise: break between first and second woves of the syllable
                    else -> m.value[0] + "-" + m.value[1]
                }
            }
            if (dashedWord.contains('-')) {
                return breakSyllables(dashedWord)
            }

        }

        if (thisString.contains(diphthongRule)) {
            return breakSyllables(thisString.replace(diphthongRule) { m -> m.groups[1]?.value + "-" + m.groups[2]?.value })
        }

        if (thisString.isEmpty()) {
            return emptyList()
        }
        return listOf(Syllable(thisString))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Word

        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }

}

object Characters {
    val consonant = Regex(c, RegexOption.IGNORE_CASE)
    val vowel = Regex(v, RegexOption.IGNORE_CASE)
    val notLetter = Regex("[^a-zåäöÅÄÖ]", RegexOption.IGNORE_CASE)
}


/**
 * A natural language syllable
 *
 * @param rawContent string representation
 */
class Syllable(rawContent: String) {

    val content: String = rawContent
        .replace(trimNonLetter, "")
        .toLowerCase()


    /** Syllable type string: wovels are replaced by 'V' and consonants by 'C' */
    val type: String by lazy {
        content.replace(Characters.consonant, "C")
            .replace(Characters.vowel, "V")
    }

    override fun toString(): String {
        return "Syllable(content='$content')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Syllable

        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }
}
