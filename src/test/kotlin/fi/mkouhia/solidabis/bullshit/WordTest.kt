package fi.mkouhia.solidabis.bullshit

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.row


class WordTest : StringSpec({

    "Clear non-letter characters from Word start and end" {
        forall(
            row("loppu.", "loppu"),
            row("suur-Suomi", "suur-Suomi"),
            row("Hei!", "Hei")
        ) { rawContent, content ->
            Word(rawContent).content shouldBe content.toLowerCase()
        }
    }

    "Syllables list of empty string should be empty" {
        Word("").syllables.shouldBeEmpty()
    }

    /**
     * Check if word hyphenation returns expected results
     */
    fun beHyphenatedLikeThis() = object : Matcher<String> {
        override fun test(value: String): Result {
            val word = value.replace("-", "")
            val syllables = value.split("-").map { Syllable(it) }

            return Result(
                Word(word).syllables == syllables,
                "String $word should hyphenate as $value",
                "String $word should not hyphenate as $value"
            )
        }
    }

    "Consonant rule: hyphenate before last consonant: VC*CV -> VC*-CV" {
        forall(
            row("ka-la"),
            row("kui-ten-kin"),
            row("kurs-si"),
            row("ken-gät"),
            row("lef-fas-sa"),
            row("ki-vaa"),
            row("kah-del-le"),
            row("tra-giik-kaa"),
            row("se-kä"),
            row("hork-ka-ti-lo-ja"),
            row("greip-pi"),
            row("al-la")
        ) { it should beHyphenatedLikeThis() }
    }

    "Vowel rule: hyphenate between first and second vowel, unless the pair is long wovels, or diphthong" {
        forall(
            row("ai-no-a"),
            row("hert-tu-aa"),
            row("köy-hi-en"),
            row("puo-lu-ei-ta"),
            row("pie-ni"),
            row("tuo-li"),
            row("pa-pe-ri-en"),
            row("hy-gi-e-ni-a"),
            row("vi-an"),
            row("se-as-sa"),
            row("lo-as-sa"),
            row("mu-as-sa"),
            row("di-a"),
            row("kor-ke-a"),
            row("myl-ly-ä"),
            row("lu-en-to"),
            row("Aa-si-an"),
            row("kää-pi-öis-tä"),
            row("puo-lu-eis-ta")
        ) { it should beHyphenatedLikeThis() }
    }

    "Diphthong rule: when a diphthong or long vowel is followed by a vowel, break before that vowel" {
        forall(
            row("raa-is-tu-nut"),
            row("maa-il-ma"),
            row("liu-ot-ti-met"),
            row("lau-an-tai-na"),
            row("tau-ot-ta"),
            row("leu-an")
        ) { it should beHyphenatedLikeThis() }
    }

    "Keep small words as they are" {
        forall (
            row("on"),
            row("ja"),
            row("ei")
        ) { it should beHyphenatedLikeThis() }
    }

    "Equal content words are equal" {
        Word("testisana") shouldBe Word("testisana")
    }

    "Word equality is not determined by case" {
        Word("Moi") shouldBe Word("moi")
    }
})
