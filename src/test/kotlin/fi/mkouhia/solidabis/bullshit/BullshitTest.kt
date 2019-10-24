package fi.mkouhia.solidabis.bullshit

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.row

internal class BullshitTest: StringSpec({

    "Simple rotation shifts characters" {
        forall(
            row("abc", "bcd", 1),
            row("xyzåäöabc", "yzåäöabcd", 1),
            row("xyzåäöabc", "zåäöabcde", 2),
            row("AaBbCc", "BbCcDd", 1),
            row("ZzÅåÄäÖöAa", "ÅåÄäÖöAaBb", 1)
        ) { original, rotated, rotN ->
            Bullshit(original).candidates[rotN].content shouldBe rotated
        }
    }

    "Special characters are ignored in rotation" {
        forall(
            row("abc def", "bcd efg", 1),
            row("a!@#$", "b!@#$", 1)
        ) { original, rotated, rotN ->
            Bullshit(original).candidates[rotN].content shouldBe rotated
        }
    }

    "Candidate array extremes are as expected" {
        forall(
            row("abc", 0),
            row("bcd", 1),
            row("zåä", 25),
            row("åäö", 26),
            row("äöa", 27),
            row("öab", 28)
        ) { rotated, rotN ->
            Bullshit("abc").candidates[rotN].content shouldBe rotated
        }
    }

    "Bullshit candidate array size" {
        Bullshit("aa").candidates.size shouldBe 29
    }

})
