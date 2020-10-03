package com.spice.carboncrushersrefactor.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spice.carboncrushersrefactor.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DailyLogViewModel : ViewModel() {
    companion object {
        private const val URL = "https://carbon-crushers.herokuapp.com/dailylog"
    }

    lateinit var pHelper: SharedPreferencesHelper

    var meatServings = 0
    var packagedMeals = 0
    var nonLocalProduce = 0
    var travelCar = 0
    var travelBus = 0
    var travelBicycle = 0
    var travelTrain = 0
    var travelPlane = 0
    var travelWalking = 0

    var receivedElectricBill = false

    private var _electricBillAmount: Double? = null
    var electricBillAmount: Double?
        get() = if (receivedElectricBill) {
            _electricBillAmount
        } else {
            null
        }
        set(value) {
            _electricBillAmount = value
        }

    private var _electricBillUnit: String? = Constants.DOLLARS
    var electricBillUnit: String?
        get() = if (receivedElectricBill) {
            _electricBillUnit
        } else {
            null
        }
        set(value) {
            _electricBillUnit = value
        }

    var receivedGasBill = false

    private var _gasBillAmount: Double? = null
    var gasBillAmount: Double?
        get() = if (receivedGasBill) {
            _gasBillAmount
        } else {
            null
        }
        set(value) {
            _gasBillAmount = value
        }

    private var _gasBillUnit: String? = Constants.DOLLARS
    var gasBillUnit: String?
        get() = if (receivedGasBill) {
            _gasBillUnit
        } else {
            null
        }
        set(value) {
            _gasBillUnit = value
        }

    val areQuestionsAnswered: Boolean
        get() {
            if (receivedElectricBill && electricBillAmount == null) {
                return false
            }

            if (receivedGasBill && gasBillAmount == null) {
                return false
            }

            return true
        }

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val _dataSubmitted = MutableLiveData<Boolean>()
    val dataSubmitted: LiveData<Boolean>
        get() = _dataSubmitted

    private val _alreadyLoggedToday = MutableLiveData<Boolean>()
    val alreadyLoggedToday: LiveData<Boolean>
        get() = _alreadyLoggedToday

    @SuppressLint("SimpleDateFormat")
    fun uploadData() {
        val date = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, -5) }.time
        val sdf = SimpleDateFormat("yyyyMMdd")
        val dateStr = sdf.format(date)

        val jsonObject = JSONObject()
        jsonObject.put("username", pHelper.rememberedUser)
        jsonObject.put("date", dateStr)
        jsonObject.put("receivedElectricBill", receivedElectricBill)
        jsonObject.put("electricBillAmount", electricBillAmount)
        jsonObject.put("electricBillUnit", electricBillUnit)
        jsonObject.put("receivedGasBill", receivedGasBill)
        jsonObject.put("gasBillAmount", gasBillAmount)
        jsonObject.put("gasBillUnit", gasBillUnit)
        jsonObject.put("meatServings", meatServings)
        jsonObject.put("packagedMeals", packagedMeals)
        jsonObject.put("nonLocalProduceServings", nonLocalProduce)
        jsonObject.put("travelCar", travelCar)
        jsonObject.put("travelBus", travelBus)
        jsonObject.put("travelBicycle", travelBicycle)
        jsonObject.put("travelTrain", travelTrain)
        jsonObject.put("travelPlane", travelPlane)
        jsonObject.put("travelWalking", travelWalking)
        val body = jsonObject.toString()
        val request = Request.Builder()
                .url(URL)
                .post(body.toRequestBody("application/json".toMediaType()))
                .build()
        client!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _toastMessage.postValue("Failed to submit data. Please try again.")
                Log.e("connection", e.toString())
                e.printStackTrace()
                _dataSubmitted.postValue(false)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    Log.d("connection", it.body?.string() ?: "No response body")
                    _toastMessage.postValue("Successfully submitted data.")
                    _dataSubmitted.postValue(true)
                }
            }
        })
    }

    fun fetchMostRecentDailyLogAndUpdate() {
        val request = Request.Builder()
                .url("$URL?number=1")
                .build()
        client!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _toastMessage.postValue("Could not fetch data.")
            }

            @SuppressLint("SimpleDateFormat")
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val jsonObject = it.body?.string()?.toJSONObject()
                    Log.d("json", jsonObject?.toString(2) ?: "null")
                    val jsonArray = jsonObject?.getJSONArray("documents")
                    if (jsonArray != null) {
                        val log = jsonArray.optJSONObject(0)
                        if (log == null) {
                            _alreadyLoggedToday.postValue(false)
                        } else {
                            val sdf = SimpleDateFormat("yyyyMMdd")
                            val logDate = log.getString("date")
                            val currentDate = Calendar.getInstance().apply {
                                add(Calendar.HOUR_OF_DAY, -5) // Consider everything before 5AM to be the previous day
                            }.time
                            val currentDateFormatted = sdf.format(currentDate)
                            Log.d("Debugging", "currentDate: $currentDateFormatted\nlogDate: $logDate")
                            if (currentDateFormatted == logDate) {
                                _alreadyLoggedToday.postValue(true)
                            } else {
                                _alreadyLoggedToday.postValue(false)
                            }
                        }
                    }
                }
            }
        })
    }
}