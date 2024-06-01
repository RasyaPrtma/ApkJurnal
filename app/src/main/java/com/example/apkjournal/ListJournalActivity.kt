package com.example.apkjournal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ListJournalActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var journalAdapter: JournalAdapter
    private lateinit var journalList: ArrayList<Journal>
    private lateinit var token: String
    private lateinit var tvUser: TextView

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPref = getSharedPreferences("app_preferences", MODE_PRIVATE)
        return sharedPref.getString("TOKEN", null)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_journal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tvUser = findViewById<TextView>(R.id.tvHelloUser)

        token = getTokenFromSharedPreferences() ?: ""

        val btnAdd = findViewById<ImageView>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(this, CreateJournalActivity::class.java)
            startActivity(intent)
        }

        val btnHome = findViewById<ImageView>(R.id.btnHome)
        btnHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerViewJournal)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        journalList = ArrayList()
        journalAdapter = JournalAdapter(journalList)
        recyclerView.adapter = journalAdapter

        fetchJournals()
        fetchUserDetails()
    }

    private fun fetchJournals() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = getJournals()
            if (result != null) {
                parseJournals(result)
            }
        }
    }

    private suspend fun getJournals(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://apijurnal.ndamelweb.com/public/api/v1/jurnals/users")
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

    private fun parseJournals(response: String) {
        try {
            val jsonResponse = JSONObject(response)
            val dataArray = jsonResponse.getJSONArray("data")
            journalList.clear()
            for (i in 0 until dataArray.length()) {
                val dataObj = dataArray.getJSONObject(i)
                val journal = Journal(
                    title = dataObj.getString("title"),
                    date = dataObj.getString("date"),
                    note = dataObj.getString("content")
                )
                journalList.add(journal)
            }
            runOnUiThread {
                journalAdapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fetchUserDetails() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = getUserDetails()
            if (result != null) {
                updateUserTextView(result)
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

    private fun updateUserTextView(response: String) {
        try {
            val jsonResponse = JSONObject(response)
            val data = jsonResponse.getJSONObject("data")
            val username = data.getString("name")
            runOnUiThread {
                tvUser.text = "Hello $username"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchJournals()
        fetchUserDetails()
    }
}
