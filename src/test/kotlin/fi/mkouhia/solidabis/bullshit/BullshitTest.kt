package fi.mkouhia.solidabis.bullshit

import io.kotlintest.data.forall
import io.kotlintest.inspectors.forAll
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

    "Same content bullshits are equal" {
        Bullshit("foo") shouldBe Bullshit("foo")
    }

    "Finnish sentences are recognized as likely Finnish" {
        listOf(
            Bullshit("Olen omena, olen pyöreä omena"),
            Bullshit("Hopeinen kuu luo merelle siltaa, ei tulla koskaan voisi kai tällaista iltaa")
        ).forAll {
            it.isLikelyFinnish shouldBe true
        }
    }

    "Non-Finnish sentences are recognized as likely not Finnish" {
        listOf(
            Bullshit("foo santeohusteoa aosetua oaseta asote ua oaestueoa aoeurga ej."),
            Bullshit("asd saoe lkar crdcgbok saeu xntbag sacuoa asoueh asoeucbka aoexb")
        ).forAll {
            it.isLikelyFinnish shouldBe false
        }
    }

    "ROT-13 Finnish sentences are recognized as likely Finnish" {
        listOf(
            Bullshit("Olen omena, olen pyöreä omena"),
            Bullshit("Hopeinen kuu luo merelle siltaa, ei tulla koskaan voisi kai tällaista iltaa")
        ).map {
            val msg: String = it.candidates[13].content
            Bullshit(msg)
        }.forAll {
            it.isLikelyFinnish shouldBe true
        }
    }

})
