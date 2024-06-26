package com.example.apkjournal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
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

class LoginActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        progressBar = findViewById(R.id.progressBar)

        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and Password must not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                val result = loginTask(email, password)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    handleLoginResult(result)
                    Log.d("Coroutine", "Hello from coroutine thread")
                }
            }
            Log.d("Coroutine", "Hello from main thread")
        }
    }

    private suspend fun loginTask(email: String, password: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://apijurnal.ndamelweb.com/public/api/v1/auth/login")

                val postData = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                }

                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; utf-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.doOutput = true

                connection.outputStream.use { os ->
                    val input = postData.toString().toByteArray()
                    os.write(input, 0, input.size)
                }

                val responseCode = connection.responseCode
                Log.d("LoginTask", "Response Code: $responseCode")

                val reader = BufferedReader(InputStreamReader(
                    if (responseCode == HttpURLConnection.HTTP_OK) connection.inputStream else connection.errorStream
                ))

                val response = reader.use(BufferedReader::readText)
                Log.d("LoginTask", "Response: $response")
                response
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun saveTokenToSharedPreferences(token: String) {
        val sharedPref = getSharedPreferences("app_preferences", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("TOKEN", token)
            apply()
        }
    }

    private fun handleLoginResult(result: String?) {
        if (result != null) {
            try {
                val jsonResponse = JSONObject(result)
                val message = jsonResponse.optString("message")
                if (message == "Berhasil Login") {
                    val token = jsonResponse.optString("token")
                    saveTokenToSharedPreferences(token) // Save the token

                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("TryCatch", "oh no something went wrong")
            }
        } else {
            Toast.makeText(this@LoginActivity, "An Error Occurred: No response", Toast.LENGTH_SHORT).show()
        }
    }
}
