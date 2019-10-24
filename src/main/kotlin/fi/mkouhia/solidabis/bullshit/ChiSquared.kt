package fi.mkouhia.solidabis.bullshit

import kotlin.math.pow

/**
 * Pearson's Chi-squared test
 *
 * @param expectedFrequencyDistribution map of expected frequencies, [0..1]
 * @param observedCount map of observed counts
 * @param ignoreExpectedCountsLessThan if expected count is less than value, exclude category from Chi2 calculaion
 */
class ChiSquared<T>(val expectedFrequencyDistribution: FrequencyDistribution<T>, val observedCount: Map<T, Int>, val ignoreExpectedCountsLessThan: Double = 0.0) {

    private val observations = observedCount.values.sum()

    val testValue: Double by lazy { testValue() }
    val pValue: Double by lazy { pValue() }

    /**
     * Chi-squared test value
     *
     * @return test value
     */
    private fun testValue(): Double {
        val lowerFrequencyLimit = ignoreExpectedCountsLessThan / observations.toDouble()
        return expectedFrequencyDistribution.frequency
            .filter { it.value > lowerFrequencyLimit }
            .map {
                val thisCount = observedCount.get(it.key) ?: 0
                val expectedCount = it.value * observations
                (thisCount - expectedCount).pow(2) / expectedCount
            }
            .sum()
    }

    /** P-value for the Pearson's Chi-squared test */
    fun pValue(degreesOfFreedom: Int = expectedFrequencyDistribution.frequency.size - 1): Double {
        return ChiSquaredMath.chi2Probability(degreesOfFreedom, testValue)
    }

    fun distributionsAreSame(significance: Double): Boolean {
        return pValue > significance
    }

    /**
     * Chi-squared metrics calculations from Rosetta Code
     *
     * Source: [https://rosettacode.org/wiki/Verify_distribution_uniformity/Chi-squared_test#Kotlin] - version 1.1.51
     */
    object ChiSquaredMath {
        //

        /**
         * Gamma function - Lanczos approximation
         *
         * Source: [https://rosettacode.org/wiki/Gamma_function#Kotlin] - version 1.0.6
         * Reformatted for better readability
         */
        private fun gammaLanczos(x: Double): Double {
            val p = doubleArrayOf(
                0.99999999999980993,
                676.5203681218851,
                -1259.1392167224028,
                771.32342877765313,
                -176.61502916214059,
                12.507343278686905,
                -0.13857109526572012,
                9.9843695780195716e-6,
                1.5056327351493116e-7
            )
            val g = 7
            if (x < 0.5) {
                return Math.PI / (Math.sin(Math.PI * x) * gammaLanczos(1.0 - x))
            }
            val xx = x - 1.0
            var a = p[0]
            val t = xx + g + 0.5
            for (i in 1 until p.size) {
                a += p[i] / (xx + i)
            }
            return Math.sqrt(2.0 * Math.PI) * Math.pow(t, xx + 0.5) * Math.exp(-t) * a
        }


        /**
         * Numerical integration, Simpson's composite method
         *
         * Sources:
         * - [https://rosettacode.org/wiki/Verify_distribution_uniformity/Chi-squared_test#Kotlin] - version 1.1.51
         * - [https://rosettacode.org/wiki/Numerical_integration#Kotlin] - version 1.1.2
         *
         * @param a lower bound
         * @param b upper bound
         * @param n number of approximations to make in the range [a, b]
         * @param f function to be integrated
         */
        private fun integrate(a: Double, b: Double, n: Int, f: (Double) -> Double): Double {
            val h = (b - a) / n
            var sum = 0.0
            for (i in 0 until n) {
                val x = a + i * h
                sum += (f(x) + 4.0 * f(x + h / 2.0) + f(x + h)) / 6.0
            }
            return sum * h
        }

        /**
         * Regularized gamma function approximation
         *
         * Sources:
         * - [https://rosettacode.org/wiki/Verify_distribution_uniformity/Chi-squared_test#Kotlin] - version 1.1.51
         * - [https://rosettacode.org/wiki/Verify_distribution_uniformity/Chi-squared_test#C]
         * - [https://en.wikipedia.org/wiki/Incomplete_gamma_function]
         *
         * Reformatted for better readability
         */
        private fun gammaIncompleteQ(a: Double, x: Double): Double {
            val aa1 = a - 1.0
            fun f0(t: Double) = Math.pow(t, aa1) * Math.exp(-t)

            // approximate integration step size
            val h = 1.5e-2
            var y = aa1

            // this cuts off the tail of the integration to speed things up
            while ((f0(y) * (x - y) > 2.0e-8) && (y < x)) {
                y += 0.4
            }
            if (y > x) {
                y = x
            }

            return 1.0 - integrate(0.0, y, (y / h).toInt(), ::f0) / gammaLanczos(a)
        }

        /**
         * Cumulative distribution function
         *
         * Sources:
         * - [https://rosettacode.org/wiki/Verify_distribution_uniformity/Chi-squared_test#Kotlin] - version 1.1.51
         * - [https://en.wikipedia.org/wiki/Chi-squared_distribution#Cumulative_distribution_function]
         */
        fun chi2Probability(dof: Int, distance: Double) =
            gammaIncompleteQ(0.5 * dof, 0.5 * distance)

    }


}
