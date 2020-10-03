package com.spice.carboncrushersrefactor

import android.content.Context
import android.util.Log
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class AccessTokenAuthenticator(context: Context) : Authenticator {
    private val pHelper = SharedPreferencesHelper(
            context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE))

    private val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()

    private var hasRefreshedToken = false

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("Authorization") == null) { // First authentication
            Log.d("Authenticator", "First authentication")
            hasRefreshedToken = false
            pHelper.token?.let {
                return response.request.newBuilder()
                        .header("Authorization", "Bearer $it")
                        .build()
            }
            return null
        } else if (!hasRefreshedToken) { // Try to refresh token
            Log.d("Authenticator", "Attempting to refresh token.")
            hasRefreshedToken = true
            pHelper.refreshToken?.let {
                val url = "${Constants.SERVER_URL}/token?username=${pHelper.rememberedUser}&refreshToken=$it"
                val request = Request.Builder()
                        .url(url)
                        .build()
                client.newCall(request).execute().use { response2 ->
                    if (!response2.isSuccessful) throw IOException()

                    val body = response2.body?.string()?.toJSONObject()

                    if (body != null) {
                        val newToken = body.getString("token")
                        pHelper.token = newToken
                        return response.request.newBuilder()
                                .header("Authorization", "Bearer $newToken")
                                .build()
                    }
                }
            }
            return null
        } else { // Refresh failed
            return null
        }
    }
}