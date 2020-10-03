package com.spice.carboncrushersrefactor

import android.content.SharedPreferences

class SharedPreferencesHelper(private val prefs: SharedPreferences) {
    companion object {
        private const val KEY_LOGGED_IN_USER = "LOGGED_IN_USER"
        private const val KEY_INITIAL_QUESTIONS = "INITIAL_QUESTIONS"
        private const val KEY_DIET = "DIET"
        private const val KEY_OWNS_CAR = "OWNS_CAR"
        private const val KEY_CAR_MILES = "CAR_MILES"
        private const val KEY_ELECTRIC_BILL = "ELECTRIC_BILL"
        private const val KEY_HAS_NATURAL_GAS = "HAS_NATURAL_GAS"
        private const val KEY_GAS_BILL = "GAS_BILL"
        private const val KEY_HAS_FLOWN = "HAS_FLOWN"
        private const val KEY_FLIGHT_MILES = "FLIGHT_MILES"
        private const val KEY_HOME_SQ_FT = "HOME_SQ_FT"
        private const val KEY_HOME_PEOPLE = "HOME_PEOPLE"
        private const val KEY_AVG_CARBON = "AVG_CARBON"
        private const val KEY_TOKEN = "TOKEN"
        private const val KEY_REFRESH_TOKEN = "REFRESH_TOKEN"
        private const val KEY_REMINDER_TIME = "REMINDER_TIME"
    }

    val rememberedUser: String?
        get() = prefs.getString(KEY_LOGGED_IN_USER, null)

    var areInitialQuestionsAnswered: Boolean
        set(value) {
            val editor = prefs.edit()
            editor.putBoolean(KEY_INITIAL_QUESTIONS, value)
            editor.apply()
        }
        @JvmName("areInitialQuestionsAnswered")
        get() = prefs.getBoolean(KEY_INITIAL_QUESTIONS, false)

    var noMeat: Boolean
        set(value) {
            val editor = prefs.edit()
            editor.putBoolean(KEY_DIET, value)
            editor.apply()
        }
        get() = prefs.getBoolean(KEY_DIET, false)

    var ownsCar: Boolean
        set(value) {
            val editor = prefs.edit()
            editor.putBoolean(KEY_OWNS_CAR, value)
            editor.apply()
        }
        @JvmName("ownsCar")
        get() = prefs.getBoolean(KEY_OWNS_CAR, false)

    var carMiles: Int?
        set(value) {
            val editor = prefs.edit()
            editor.putInt(KEY_CAR_MILES, value ?: 0)
            editor.apply()
        }
        get() = prefs.getInt(KEY_CAR_MILES, 0)

    var avgElectricBill: Int
        set(value) {
            val editor = prefs.edit()
            editor.putInt(KEY_ELECTRIC_BILL, value)
            editor.apply()
        }
        get() = prefs.getInt(KEY_ELECTRIC_BILL, 0)

    var hasNaturalGas: Boolean
        set(value) {
            val editor = prefs.edit()
            editor.putBoolean(KEY_HAS_NATURAL_GAS, value)
            editor.apply()
        }
        @JvmName("hasNaturalGas")
        get() = prefs.getBoolean(KEY_HAS_NATURAL_GAS, false)

    var avgGasBill: Int?
        set(value) {
            val editor = prefs.edit()
            editor.putInt(KEY_GAS_BILL, value ?: 0)
            editor.apply()
        }
        get() = prefs.getInt(KEY_GAS_BILL, 0)

    var hasFlownInLastYear: Boolean
        set(value) {
            val editor = prefs.edit()
            editor.putBoolean(KEY_HAS_FLOWN, value)
            editor.apply()
        }
        get() = prefs.getBoolean(KEY_HAS_FLOWN, false)

    var flightMiles: Int?
        set(value) {
            val editor = prefs.edit()
            editor.putInt(KEY_FLIGHT_MILES, value ?: 0)
            editor.apply()
        }
        get() = prefs.getInt(KEY_FLIGHT_MILES, 0)

    var homeSqFt: Int
        set(value) {
            val editor = prefs.edit()
            editor.putInt(KEY_HOME_SQ_FT, value)
            editor.apply()
        }
        get() = prefs.getInt(KEY_HOME_SQ_FT, 0)

    var homeNumberOfPeople: Int
        set(value) {
            val editor = prefs.edit()
            editor.putInt(KEY_HOME_PEOPLE, value)
            editor.apply()
        }
        get() = prefs.getInt(KEY_HOME_PEOPLE, 0)

    var avgCarbon: Int
        set(value) {
            val editor = prefs.edit()
            editor.putInt(KEY_AVG_CARBON, value)
            editor.apply()
        }
        get() = prefs.getInt(KEY_AVG_CARBON, 0)

    var token: String?
        set(value) {
            val editor = prefs.edit()
            editor.putString(KEY_TOKEN, value)
            editor.apply()
        }
        get() = prefs.getString(KEY_TOKEN, null)

    var refreshToken: String?
        set(value) {
            val editor = prefs.edit()
            editor.putString(KEY_REFRESH_TOKEN, value)
            editor.apply()
        }
        get() = prefs.getString(KEY_REFRESH_TOKEN, null)

    var reminderTime: Long?
        set(value) {
            val editor = prefs.edit()
            editor.putLong(KEY_REMINDER_TIME, value ?: -1L)
            editor.apply()
        }
        get() {
            val result = prefs.getLong(KEY_REMINDER_TIME, -1)
            return if (result == -1L) null else result
        }

    fun loginUser(username: String?) {
        val editor = prefs.edit()
        editor.putString(KEY_LOGGED_IN_USER, username)
        editor.apply()
    }

    fun logoutUser() {
        val editor = prefs.edit()
        editor.putString(KEY_LOGGED_IN_USER, null)
        editor.apply()
    }
}