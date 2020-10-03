package com.spice.carboncrushersrefactor.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.SharedPreferencesHelper
import com.spice.carboncrushersrefactor.client
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class ReportOffsetsViewModel: ViewModel() {
    lateinit var pHelper: SharedPreferencesHelper

    enum class Donation {
        EDEN,
        TERRAPASS
    }

    var donationOption: Donation? = null
    var offsetAmount: Double? = null
    val isFilledOut: Boolean by lazy { donationOption != null && offsetAmount != null }

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val _dataSubmitted = MutableLiveData<Boolean>(false)
    val dataSubmitted: LiveData<Boolean>
        get() = _dataSubmitted

    fun uploadData() {
        val url = Constants.SERVER_URL + "/offsets"
        val body = JSONObject()
        val donationKey = if (donationOption == Donation.TERRAPASS) {
            "terrapass"
        } else {
            "eden"
        }
        body.put(donationKey, offsetAmount)
        val request = Request.Builder()
                .url(url)
                .post(body.toString().toRequestBody("application/json".toMediaType()))
                .build()
        client!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                _toastMessage.postValue("Failed to submit data.")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {}
                _toastMessage.postValue("Successfully submitted data.")
                _dataSubmitted.postValue(true)
            }

        })
    }
}