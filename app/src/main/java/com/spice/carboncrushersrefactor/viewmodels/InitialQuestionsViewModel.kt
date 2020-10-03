package com.spice.carboncrushersrefactor.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.SharedPreferencesHelper
import com.spice.carboncrushersrefactor.client
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class InitialQuestionsViewModel : ViewModel() {
    companion object {
        private const val MILES = 0.9685
        private const val THERMS = 15.5
        private const val FLY_MILES = 0.483
        private const val POWER = 0.99
    }

    lateinit var pHelper: SharedPreferencesHelper

    var followsDiet: Boolean? = null
    private val _diets = mutableListOf<String>()
    val diets: MutableList<String>
        get() = if (followsDiet == true) {
                _diets
            } else {
                mutableListOf()
            }

    var ownsCar: Boolean? = null
    private var _carMiles: Int? = null
    var carMiles: Int?
        get() = if (ownsCar == true) {
            _carMiles
        } else {
            null
        }
        set(value) {
            _carMiles = value
        }

    var electricBill: Int? = null

    var hasNaturalGas: Boolean? = null
    private var _gasBill: Int? = null
    var gasBill: Int?
        get() = if (hasNaturalGas == true) {
            _gasBill
        } else {
            null
        }
        set(value) {
            _gasBill = value
        }

    var hasFlown: Boolean? = null
    private var _flightMiles: Int? = null
    var flightMiles: Int?
        get() = if (hasFlown == true) {
            _flightMiles
        } else {
            null
        }
        set(value) {
            _flightMiles = value
        }

    var homeSize: Int? = null
    var people: Int? = null

    private val avgCarbon: Int?
        get() = if (areQuestionsAnswered) {
            var total = 0.0
            total += (carMiles ?: 0) * MILES
            total += POWER * electricBill!! * 12.0 / 1.07
            total += (gasBill ?: 0) * THERMS
            total += (flightMiles ?: 0) * FLY_MILES
            total *= 2.20462
            total.roundToInt()
        } else {
            null
        }

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val _dietNeedsFocus = MutableLiveData<Boolean>()
    val dietNeedsFocus: LiveData<Boolean>
        get() = _dietNeedsFocus

    private val _carNeedsFocus = MutableLiveData<Boolean>()
    val carNeedsFocus: LiveData<Boolean>
        get() = _carNeedsFocus

    private val _electricNeedsFocus = MutableLiveData<Boolean>()
    val electricNeedsFocus: LiveData<Boolean>
        get() = _electricNeedsFocus

    private val _gasNeedsFocus = MutableLiveData<Boolean>()
    val gasNeedsFocus: LiveData<Boolean>
        get() = _gasNeedsFocus

    private val _flightNeedsFocus = MutableLiveData<Boolean>()
    val flightNeedsFocus: LiveData<Boolean>
        get() = _flightNeedsFocus

    private val _home1needsFocus = MutableLiveData<Boolean>()
    val home1needsFocus: LiveData<Boolean>
        get() = _home1needsFocus

    private val _home2needsFocus = MutableLiveData<Boolean>()
    val home2needsFocus: LiveData<Boolean>
        get() = _home2needsFocus

    val areQuestionsAnswered: Boolean
        get() {
            if (followsDiet == null || followsDiet == true && diets.isEmpty()) {
                _dietNeedsFocus.postValue(true)
                return false
            } else {
                _dietNeedsFocus.postValue(false)
            }

            if (ownsCar == null || ownsCar == true && carMiles == null) {
                _carNeedsFocus.postValue(true)
                return false
            } else {
                _carNeedsFocus.postValue(false)
            }

            if (electricBill == null) {
                _electricNeedsFocus.postValue(true)
                return false
            } else {
                _electricNeedsFocus.postValue(false)
            }

            if (hasNaturalGas == null || hasNaturalGas == true && gasBill == null) {
                _gasNeedsFocus.postValue(true)
                return false
            } else {
                _gasNeedsFocus.postValue(false)
            }

            if (hasFlown == null || hasFlown == true && flightMiles == null) {
                _flightNeedsFocus.postValue(true)
                return false
            } else {
                _flightNeedsFocus.postValue(false)
            }

            if (homeSize == null) {
                _home1needsFocus.postValue(true)
                return false
            } else {
                _home1needsFocus.postValue(false)
            }

            if (people == null) {
                _home2needsFocus.postValue(true)
                return false
            } else {
                _home2needsFocus.postValue(false)
            }

            return true
        }

    /**
     * Save data. Should only be called if initial questions are answered. (Will cause null pointer
     * exceptions otherwise.
     */
    fun saveData() {
        pHelper.areInitialQuestionsAnswered = true
        pHelper.noMeat = diets.contains(Constants.VEGETARIAN) || diets.contains(Constants.VEGAN)
        pHelper.ownsCar = ownsCar!!
        pHelper.carMiles = carMiles
        pHelper.avgElectricBill = electricBill!!
        pHelper.hasNaturalGas = hasNaturalGas!!
        pHelper.avgGasBill = gasBill
        pHelper.hasFlownInLastYear = hasFlown!!
        pHelper.flightMiles = flightMiles
        pHelper.homeSqFt = homeSize!!
        pHelper.homeNumberOfPeople = people!!
        pHelper.avgCarbon = avgCarbon!!
    }

    fun uploadData() {
        val jsonObject = JSONObject()
        jsonObject.put("diets", JSONArray(diets))
        jsonObject.put("car", ownsCar)
        jsonObject.put("miles", carMiles ?: JSONObject.NULL)
        jsonObject.put("gas", hasNaturalGas)
        jsonObject.put("therms", gasBill ?: JSONObject.NULL)
        jsonObject.put("power", electricBill)
        jsonObject.put("flown", hasFlown)
        jsonObject.put("flymiles", flightMiles ?: JSONObject.NULL)
        jsonObject.put("housearea", homeSize)
        jsonObject.put("people", people)
        jsonObject.put("score", avgCarbon)
        Log.d("connection", jsonObject.toString(4))
        val body = jsonObject.toString()
        val request = Request.Builder()
                .url(Constants.SERVER_URL+"/question")
                .post(body.toRequestBody("application/json".toMediaType()))
                .build()
        client!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _toastMessage.postValue("Error communicating with database.")
                Log.d("connection", e.toString())
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    Log.d("connection", it.body?.string() ?: "No response body.")
                }
            }

        })
    }

}