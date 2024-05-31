// HomeActivity.kt
package com.example.apkjournal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HomeActivity : AppCompatActivity() {
    private lateinit var token: String

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPref = getSharedPreferences("app_preferences", MODE_PRIVATE)
        return sharedPref.getString("TOKEN", null)
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        token = getTokenFromSharedPreferences() ?: ""

        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        fetchUserData(tvUsername)

        val btnJournal = findViewById<Button>(R.id.btnJournal)
        btnJournal.setOnClickListener {
            val intent = Intent(this, ListJournalActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchUserData(tvUsername: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = getUserData()
            result?.let { userData ->
                withContext(Dispatchers.Main) {
                    tvUsername.text = userData.optString("name")
                }
            }
        }
    }

    private suspend fun getUserData(): JSONObject? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://apijurnal.ndamelweb.com/public/api/v1/users")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json; utf-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $token")
                connection.doInput = true

                val responseCode = connection.responseCode
                Log.d("GetUserData", "Response Code: $responseCode")

                val reader = BufferedReader(InputStreamReader(
                    if (responseCode == HttpURLConnection.HTTP_OK) connection.inputStream else connection.errorStream
                ))

                val response = reader.use(BufferedReader::readText)
                Log.d("GetUserData", "Response: $response")

                val jsonResponse = JSONObject(response)
                if (jsonResponse.optString("message") == "Berhasil Mengirim Data User Sekarang") {
                    jsonResponse.optJSONObject("data")
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
