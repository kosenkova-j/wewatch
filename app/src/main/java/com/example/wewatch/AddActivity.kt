package com.example.wewatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        findViewById<Button>(R.id.btn_search).setOnClickListener {
            val query = findViewById<EditText>(R.id.et_search).text.toString()
            val year = findViewById<EditText>(R.id.et_movie_year).text.toString()
            val intent = Intent(this, SearchActivity::class.java)

            intent.putExtra("QUERY", query)
            intent.putExtra("YEAR", year)
            startActivity(intent)
        }
    }
}