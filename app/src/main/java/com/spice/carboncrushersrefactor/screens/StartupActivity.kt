package com.spice.carboncrushersrefactor.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.spice.carboncrushersrefactor.*
import com.spice.carboncrushersrefactor.receivers.NotificationSender
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * This activity does not display, but serves as an entry point which determines whether to go to
 * the MainActivity or LoginActivity.
 */
class StartupActivity : AppCompatActivity() {
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private val isLoggedIn: Boolean
        get() = sharedPreferencesHelper.rememberedUser != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        val sharedPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferencesHelper = SharedPreferencesHelper(sharedPrefs)
        client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .authenticator(AccessTokenAuthenticator(this))
                .build()
        if (isLoggedIn) {
            val intent = if (sharedPreferencesHelper.areInitialQuestionsAnswered) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, InitialQuestionsActivity::class.java)
            }
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NotificationSender.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}