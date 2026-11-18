
package com.example.prak4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var mahasiswa: Mahasiswa? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        db = DatabaseHelper(this)

        val id = intent.getIntExtra(EXTRA_ID, -1)

        if (id == -1 || db.getMahasiswaById(id) == null) {
            Toast.makeText(
                this,
                "Data tidak ditemukan",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        // Tombol Ubah
        findViewById<Button>(R.id.btnUbah).setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)

            intent.putExtra(FormActivity.EXTRA_ID, id)
            intent.putExtra(FormActivity.EXTRA_SUMBER, "detail")

            startActivity(intent)
        }

        // Tombol Hapus
        findViewById<Button>(R.id.btnHapus).setOnClickListener {
            konfirmasiHapus()
        }

        // Tombol Kembali
        findViewById<Button>(R.id.btnKembali).setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        tampilkanData()
    }

    private fun tampilkanData() {
        val id = intent.getIntExtra(EXTRA_ID, -1)
        val mhs = db.getMahasiswaById(id)

        if (mhs == null) {
            finish()
            return
        }

        mahasiswa = mhs

        findViewById<TextView>(R.id.tvNim).text = mhs.nim
        findViewById<TextView>(R.id.tvNama).text = mhs.nama
        findViewById<TextView>(R.id.tvAlamat).text = mhs.alamat
        findViewById<TextView>(R.id.tvJurusan).text = mhs.jurusan
        findViewById<TextView>(R.id.tvNomorHp).text = mhs.nomorHp
    }

    private fun konfirmasiHapus() {
        val mhs = mahasiswa ?: return

        AlertDialog.Builder(this)
            .setTitle("Hapus Data")
            .setMessage("Hapus ${mhs.nama} (${mhs.nim})?")
            .setPositiveButton("Hapus") { _, _ ->
                hapus(mhs)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapus(mhs: Mahasiswa) {
        if (db.deleteMahasiswa(mhs.id) > 0) {
            Toast.makeText(
                this,
                "Data berhasil dihapus",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        } else {
            Toast.makeText(
                this,
                "Gagal menghapus data",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}
