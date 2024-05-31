package com.example.apkjournal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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

class CreateJournalActivity : AppCompatActivity() {
    private lateinit var token: String

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPref = getSharedPreferences("app_preferences", MODE_PRIVATE)
        return sharedPref.getString("TOKEN", null)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_journal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        token = getTokenFromSharedPreferences() ?: ""

        // Log the token to verify it's being passed correctly
        Log.d("CreateJournalActivity", "Token: $token")

        val judul = findViewById<EditText>(R.id.etTitle)
        val kegiatan = findViewById<EditText>(R.id.etNote)
        val btnAdd = findViewById<Button>(R.id.btnAdd)

        btnAdd.setOnClickListener {
            val title = judul.text.toString().trim()
            val note = kegiatan.text.toString().trim()

            if (title.isEmpty() || note.isEmpty()) {
                Toast.makeText(this, "Title and Note must not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val result = createJournal(title, note)
                handleCreateJournalResult(result)
            }
        }
    }

    private suspend fun createJournal(title: String, content: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://apijurnal.ndamelweb.com/public/api/v1/jurnals")

                val postData = JSONObject().apply {
                    put("title", title)
                    put("content", content)
                }

                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; utf-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $token")
                connection.doOutput = true

                connection.outputStream.use { os ->
                    val input = postData.toString().toByteArray()
                    os.write(input, 0, input.size)
                }

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

    private fun handleCreateJournalResult(result: String?) {
        if (result != null) {
            try {
                val jsonResponse = JSONObject(result)
                val message = jsonResponse.optString("message")
                runOnUiThread {
                    Toast.makeText(this@CreateJournalActivity, message, Toast.LENGTH_SHORT).show()
                    if (message == "Berhasil Membuat Jurnals") {
                        val intent = Intent(this@CreateJournalActivity, ListJournalActivity::class.java).apply {
                            putExtra("TOKEN", token)
                        }
                        startActivity(intent)
                        finish()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@CreateJournalActivity, "An Error Occurred: Invalid response format", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            runOnUiThread {
                Toast.makeText(this@CreateJournalActivity, "An Error Occurred: No response", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
