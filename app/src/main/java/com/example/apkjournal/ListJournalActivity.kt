package com.example.apkjournal

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListJournalActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var journalAdapter: JournalAdapter
    private lateinit var journalList: ArrayList<Journal>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_journal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewJournal)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.setHasFixedSize(true)
//
//        journalList = ArrayList()
//        journalList.add(Journal("First Entry", "2024-05-01", "This is the first note"))
//        journalList.add(Journal("Second Entry", "2024-05-02", "This is the second note"))
//        journalAdapter = JournalAdapter(journalList)
//        recyclerView.adapter = journalAdapter
    }
}