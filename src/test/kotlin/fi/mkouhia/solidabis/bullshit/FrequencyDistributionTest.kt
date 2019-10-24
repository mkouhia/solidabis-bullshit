package fi.mkouhia.solidabis.bullshit

import fi.mkouhia.solidabis.bullshit.FrequencyDistribution.Companion.FinnishCharacters
import io.kotlintest.specs.AnnotationSpec
import kotlin.test.*

internal class FrequencyDistributionTest: AnnotationSpec() {

    @Test
    fun `Chi2, list of Finnish characters vs itself, 100 character length`() {
        val testString = FinnishCharacters.frequency.map {
            it.key.toString().repeat((it.value * 100).toInt())
        }.joinToString("")

        assertEquals(2.7179384213018314, chiSquared(testString))
    }

    @Test
    fun `Chi2, list of Finnish characters vs itself, 10 000 character length`() {
        val testString = FinnishCharacters.frequency.map {
            it.key.toString().repeat((it.value * 1e4).toInt())
        }.joinToString("")
        assertEquals(0.36977147939551747, chiSquared(testString))
    }

    @Test
    fun `Chi2, list of Finnish characters vs uniform distribution, 290 characters`() {
        val testString = (('a'..'z').joinToString("") + "åäö").repeat(100)

        assertEquals(184611.6168710996, chiSquared(testString))
    }

    private fun chiSquared(testString: String): Double {
        return ChiSquared(FinnishCharacters, letterCounts(testString)).testValue
    }

    private fun letterCounts(content: String): Map<Char, Int> = content.toLowerCase().replace(Characters.notLetter, "")
        .groupingBy { it }.eachCount()

}
