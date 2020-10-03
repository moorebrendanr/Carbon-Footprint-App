package com.spice.carboncrushersrefactor.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.client
import com.spice.carboncrushersrefactor.toJSONObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SummaryViewModel : ViewModel() {
    companion object {
        private const val URL = Constants.SERVER_URL+"/dailylog"
    }

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    var yesterdayTotal = 0.0
    var pastWeekCarbonTotal = 0.0
    var pastMonthCarbonTotal = 0.0
    var pastYearCarbonTotal = 0.0
    private val _finishedFetchingData = MutableLiveData<Boolean>()
    val finishedFetchingData: LiveData<Boolean>
        get() = _finishedFetchingData

    var checkedChip = 0

    @SuppressLint("SimpleDateFormat")
    fun fetchDailyLogsAndUpdate() {
        val sdf = SimpleDateFormat("yyyyMMdd")
        val yesterday = sdf.format(Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1) }.time)
        val lastWeek = sdf.format(Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, -1) }.time)
        val lastMonth = sdf.format(Calendar.getInstance().apply {
            add(Calendar.MONTH, -1) }.time)
        val lastYear = sdf.format(Calendar.getInstance().apply {
            add(Calendar.YEAR, -1) }.time)

        val request = Request.Builder()
                .url("$URL?number=365")
                .build()
        client!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _toastMessage.postValue("Could not fetch data.")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseObject = it.body?.string()?.toJSONObject()
                    if (responseObject != null) {
                        val logs = responseObject.getJSONArray("documents")
                        val length = logs.length()
                        for (i in 0 until length) {
                            val log = logs.getJSONObject(i)
                            val date = log.getString("date")
                            val dailyTotal = log.getDouble("dailyCarbonTotal")
                            if (date >= yesterday) {
                                yesterdayTotal = dailyTotal
                            }
                            if (date >= lastWeek) {
                                pastWeekCarbonTotal += dailyTotal
                            }
                            if (date >= lastMonth) {
                                pastMonthCarbonTotal += dailyTotal
                            }
                            if (date >= lastYear) {
                                pastYearCarbonTotal += dailyTotal
                            }
                        }
                        _finishedFetchingData.postValue(true)
                    } else {
                        _toastMessage.postValue("Could not fetch data.")
                    }
                }
            }

        })
    }
}