package com.example.apkjournal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListJournalActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var journalAdapter: JournalAdapter
    private lateinit var journalList: ArrayList<Journal>
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

        val btnAdd = findViewById<ImageView>(R.id.btnAdd)

        btnAdd.setOnClickListener{
            val intent = Intent(this,CreateJournalActivity::class.java)
            startActivity(intent)
        }

        val btnHome = findViewById<ImageView>(R.id.btnHome)
        btnHome.setOnClickListener{
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewJournal)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        journalList = ArrayList()
        journalList.add(Journal("First Entry", "31/05/2024", "This is the first note"))
        journalList.add(Journal("Second Entry","31/05/2024", "This is the second note"))
        journalAdapter = JournalAdapter(journalList)
        recyclerView.adapter = journalAdapter

    }
}