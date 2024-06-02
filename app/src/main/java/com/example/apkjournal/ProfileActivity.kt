package com.example.apkjournal

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

class ProfileActivity : AppCompatActivity() {
    private lateinit var profileUsername: TextView
    private lateinit var profileEmail: TextView
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        profileUsername = findViewById(R.id.profileUsername)
        profileEmail = findViewById(R.id.profileEmail)

        token = getTokenFromSharedPreferences() ?: ""

        val btnBackToJournal = findViewById<Button>(R.id.btnProfileJournal)
        btnBackToJournal.setOnClickListener{
            val intent = Intent(this, ListJournalActivity::class.java)
            startActivity(intent)
        }

        fetchUserProfile()
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPref = getSharedPreferences("app_preferences", MODE_PRIVATE)
        return sharedPref.getString("TOKEN", null)
    }

    private fun fetchUserProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = getUserDetails()
            if (result != null) {
                updateProfileDetails(result)
            }
        }
    }

    private suspend fun getUserDetails(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://apijurnal.ndamelweb.com/public/api/v1/users")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $token")

                val responseCode = connection.responseCode

                val reader = BufferedReader(InputStreamReader(
                    if (responseCode == HttpURLConnection.HTTP_OK) connection.inputStream else connection.errorStream
                ))

                val response = reader.use(BufferedReader::readText)
                response
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun updateProfileDetails(response: String) {
        try {
            val jsonResponse = JSONObject(response)
            val data = jsonResponse.getJSONObject("data")
            val username = data.getString("name")
            val email = data.getString("email")

            runOnUiThread {
                profileUsername.text = username
                profileEmail.text = email
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
