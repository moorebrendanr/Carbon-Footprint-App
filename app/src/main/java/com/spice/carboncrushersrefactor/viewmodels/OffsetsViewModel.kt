package com.spice.carboncrushersrefactor.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.SharedPreferencesHelper
import com.spice.carboncrushersrefactor.client
import com.spice.carboncrushersrefactor.toJSONObject
import okhttp3.*
import java.io.IOException

class OffsetsViewModel : ViewModel() {
    lateinit var pHelper: SharedPreferencesHelper

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val _offsetTotal = MutableLiveData<Int>()
    val offsetTotal: LiveData<Int>
        get() = _offsetTotal

    fun getOffsetTotal() {
        val url = Constants.SERVER_URL + "/offsets"
        val request = Request.Builder()
                .url(url)
                .build()
        client!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                _toastMessage.postValue("Could not connect to server!")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { res ->
                    val jsonObject = res.body?.string()?.toJSONObject()
                    if (jsonObject != null) {
                        Log.d("json", jsonObject.toString(2))
                        if (jsonObject.getBoolean("success")) {
                            val total = jsonObject
                                    .getJSONObject("document")
                                    .getInt("total")
                            _offsetTotal.postValue(total)
                        } else {
                            _offsetTotal.postValue(0)
                        }
                    } else _offsetTotal.postValue(0)
                }
            }

        })
    }
}