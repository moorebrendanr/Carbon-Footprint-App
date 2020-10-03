package com.spice.carboncrushersrefactor.viewmodels

import android.annotation.SuppressLint
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
import kotlin.collections.HashMap

class HomeViewModel : ViewModel() {
    val monthlyTotals = HashMap<Int, Float>()

    private val _completedFetch = MutableLiveData<Boolean>()
    val completedFetch: LiveData<Boolean>
        get() = _completedFetch

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    @SuppressLint("SimpleDateFormat")
    fun fetchDailyLogs() {
        val sdf = SimpleDateFormat("yyyyMMdd")
        val request = Request.Builder()
                .url(Constants.SERVER_URL+"/dailylog?number=365")
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
                            val dateStr = log.getString("date")
                            val dailyTotal = log.getDouble("dailyCarbonTotal")
                            val date = sdf.parse(dateStr)
                            val calendar = Calendar.getInstance().apply {
                                time = date!!
                            }
                            val month = calendar.get(Calendar.MONTH) + 1 // Calendar's months start from 0
                            if (!monthlyTotals.containsKey(month)) monthlyTotals[month] = 0f
                            monthlyTotals[month] = monthlyTotals[month]!!.plus(dailyTotal.toFloat())
                        }
                        _completedFetch.postValue(true)
                    } else {
                        _toastMessage.postValue("Could not fetch data.")
                    }
                }
            }

        })
    }
}