package com.example.prak4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: MahasiswaAdapter

    private val dataMahasiswa = ArrayList<Mahasiswa>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)
        adapter = MahasiswaAdapter(this, dataMahasiswa)

        val listView = findViewById<ListView>(R.id.listMahasiswa)
        listView.adapter = adapter

        // Tombol Tambah Data
        findViewById<Button>(R.id.btnTambah).setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            intent.putExtra(FormActivity.EXTRA_SUMBER, "daftar")
            startActivity(intent)
        }

        // Klik mahasiswa untuk membuka DetailActivity
        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, DetailActivity::class.java)

            intent.putExtra(
                DetailActivity.EXTRA_ID,
                dataMahasiswa[position].id
            )

            startActivity(intent)
        }
    }

    // Memuat ulang daftar ketika kembali ke halaman utama
    override fun onResume() {
        super.onResume()
        loadData()
    }

    // Mengambil data mahasiswa dari SQLite
    private fun loadData() {
        dataMahasiswa.clear()
        dataMahasiswa.addAll(db.getAllMahasiswa())
        adapter.notifyDataSetChanged()
    }
}
