package com.spice.carboncrushersrefactor.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.SharedPreferencesHelper
import com.spice.carboncrushersrefactor.client
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class SettingsViewModel : ViewModel() {
    lateinit var pHelper: SharedPreferencesHelper
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    fun updateDiets(diets: ArrayList<String>) {
        pHelper.noMeat = diets.contains(Constants.VEGETARIAN) || diets.contains(Constants.VEGAN)
        val jsonBody = JSONObject()
        jsonBody.put("diets", JSONArray(diets))
        val body = jsonBody.toString()
        val request = Request.Builder()
                .url(Constants.SERVER_URL+"/question")
                .patch(body.toRequestBody("application/json".toMediaType()))
                .build()
        client!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _toastMessage.postValue("Could not connect to server.")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    _toastMessage.postValue("Successfully updated diet.")
                }
            }

        })
    }
}