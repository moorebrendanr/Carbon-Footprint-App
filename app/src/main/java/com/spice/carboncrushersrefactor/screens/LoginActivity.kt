package com.spice.carboncrushersrefactor.screens

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.SharedPreferencesHelper
import com.spice.carboncrushersrefactor.client
import com.spice.carboncrushersrefactor.databinding.ActivityLoginBinding
import com.spice.carboncrushersrefactor.toJSONObject
import okhttp3.*
import java.io.IOException

/**
 * A login screen that offers login via username/password.
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var pHelper: SharedPreferencesHelper
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        // Set up the login form.
        binding.password.setOnEditorActionListener(OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        binding.emailSignInButton.setOnClickListener { attemptLogin() }
        binding.registerUser.setOnClickListener {
            val intent = Intent(this, RegisterUser::class.java)
            startActivity(intent)
        }
        pHelper = SharedPreferencesHelper(getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE))
        setContentView(binding.root)
    }

    private fun authorizeUser(username: String, password: String) {
        val url = Constants.SERVER_URL + "/signup/login?username=" + username + "&password=" + password
        val progressDialog = ProgressDialog(this@LoginActivity)
        val request = Request.Builder()
                .url(url)
                .build()
        client!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                Log.i("Login", e.toString())
                Toast.makeText(this@LoginActivity, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val body = it.body?.string()
                    Log.d("connection", "Login response: $body")
                    val jsonObject = body?.toJSONObject()
                    val isSuccess = jsonObject?.getBoolean("success") ?: false
                    if (isSuccess) {
                        val token = jsonObject!!.getString("token")
                        val refreshToken = jsonObject.getString("refreshToken")
                        pHelper.loginUser(username)
                        pHelper.token = token
                        pHelper.refreshToken = refreshToken
                        syncData(token, progressDialog)
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed. Please try again or register", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
        progressDialog.setMessage("Logging in...")
        progressDialog.show()
        progressDialog.setCancelable(false)
    }

    private fun syncData(token: String, progressDialog: ProgressDialog) {
        val url = Constants.SERVER_URL + "/question"
        val request = Request.Builder()
                .url(url)
                .build()
        client!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                e.printStackTrace()
                Toast.makeText(this@LoginActivity, "Failed to sync data", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                progressDialog.dismiss()
                response.use {
                    val jsonObject = it.body?.string()?.toJSONObject()
                    Log.d("json", jsonObject?.toString(2) ?: "null")
                    val initialQuestions = jsonObject?.optJSONObject("document")
                    if (initialQuestions != null) {
                        pHelper.areInitialQuestionsAnswered = true
                        val dietsJSON = initialQuestions.getJSONArray("diets")
                        val length = dietsJSON.length()
                        val diets = arrayOfNulls<String>(length)
                        for (i in 0 until length) {
                            diets[i] = dietsJSON.getString(i)
                        }
                        pHelper.noMeat = diets.contains(Constants.VEGETARIAN) || diets.contains(Constants.VEGAN)
                        pHelper.ownsCar = initialQuestions.getBoolean("car")
                        pHelper.carMiles = initialQuestions.optInt("miles")
                        pHelper.avgElectricBill = initialQuestions.getInt("power")
                        pHelper.hasNaturalGas = initialQuestions.getBoolean("gas")
                        pHelper.avgGasBill = initialQuestions.optInt("therms")
                        pHelper.hasFlownInLastYear = initialQuestions.getBoolean("flown")
                        pHelper.flightMiles = initialQuestions.optInt("flymiles")
                        pHelper.homeSqFt = initialQuestions.getInt("housearea")
                        pHelper.homeNumberOfPeople = initialQuestions.getInt("people")
                        pHelper.avgCarbon = initialQuestions.getInt("score")
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@LoginActivity, InitialQuestionsActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        })
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        authorizeUser(binding.username.text.toString(), binding.password.text.toString())
    }
}