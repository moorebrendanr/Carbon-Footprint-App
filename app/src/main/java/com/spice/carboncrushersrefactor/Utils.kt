@file:JvmName("Utils")

package com.spice.carboncrushersrefactor

import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

var client: OkHttpClient? = null

/**
 * Total daily pounds of CO2. All arguments are given in pounds of CO2
 */
fun getDailyTotal(diet: Int, travel: Int, electric: Int, gas: Int) =
        (2.20462 * (diet + travel + electric + gas)).roundToInt()

/**
 * Daily pounds of CO2 from diet.
 *
 * @param meat servings
 * @param nonLocalProduce servings
 * @param packagedMeals number of meals
 */
fun getDietCarbon(meat: Int = 0, nonLocalProduce: Int = 0, packagedMeals: Int = 0): Int {
    val meatCarbon = meat * 2.205 * 1.3
    val nonLocalProduceCarbon = nonLocalProduce * 0.0112
    return if (packagedMeals == 0) {
        (meatCarbon + nonLocalProduceCarbon).roundToInt()
    } else {
        (3 * packagedMeals * (meatCarbon + nonLocalProduceCarbon)).roundToInt()
    }
}

fun getTravelCarbon(carMiles: Int = 0, busMiles: Int = 0, trainMiles: Int = 0, planeMiles: Int = 0):
        Int {
    val carCarbon = 0.9685 * carMiles
    val busCarbon = 0.9685 * busMiles * 0.02
    val trainCarbon = 0.002 * trainMiles * (19.37/52.0/200.0)
    val planeCarbon = 0.483 * planeMiles
    return (carCarbon + busCarbon + trainCarbon + planeCarbon).roundToInt()
}

fun getElectricCarbonFromDollars(billCost: Double) = (billCost / 0.124 / 30).roundToInt()

fun getElectricCarbonFromkWh(kWh: Double) = (kWh * 0.99 / 30).roundToInt()

fun getGasCarbonFromDollars(billCost: Double) = (billCost / 1.86 * 15.5 / 30).roundToInt()

fun getGasCarbonFromTherms(thms: Double) = (thms * 15.5 / 30).roundToInt()

fun String.toJSONArray() = JSONArray(this)

fun String.toJSONObject() = JSONObject(this)