
package com.example.prak4

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FormActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    private lateinit var etNim: EditText
    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etJurusan: EditText
    private lateinit var etNomorHp: EditText
    private lateinit var btnSimpan: Button

    private var editId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        db = DatabaseHelper(this)

        etNim = findViewById(R.id.etNim)
        etNama = findViewById(R.id.etNama)
        etAlamat = findViewById(R.id.etAlamat)
        etJurusan = findViewById(R.id.etJurusan)
        etNomorHp = findViewById(R.id.etNomorHp)
        btnSimpan = findViewById(R.id.btnSimpan)

        val id = intent.getIntExtra(EXTRA_ID, -1)

        // Membaca asal form dari Intent
        val sumber = intent.getStringExtra(EXTRA_SUMBER) ?: "tidak diketahui"

        // Menentukan judul berdasarkan asal form
        val judul = if (id == -1) {
            "Tambah Data - dari $sumber"
        } else {
            "Ubah Data - dari $sumber"
        }

        findViewById<TextView>(R.id.tvJudulForm).text = judul

        // Jika ada ID, berarti sedang mengubah data.
        if (id != -1) {
            editId = id
            val mahasiswa = db.getMahasiswaById(id)

            if (mahasiswa == null) {
                Toast.makeText(
                    this,
                    "Data tidak ditemukan",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return
            }

            isiForm(mahasiswa)
        }

        btnSimpan.setOnClickListener {
            simpan()
        }

        // Batal menutup form tanpa menyimpan.
        findViewById<Button>(R.id.btnBatal).setOnClickListener {
            finish()
        }
    }

    private fun isiForm(mhs: Mahasiswa) {
        etNim.setText(mhs.nim)
        etNama.setText(mhs.nama)
        etAlamat.setText(mhs.alamat)
        etJurusan.setText(mhs.jurusan)
        etNomorHp.setText(mhs.nomorHp)

        btnSimpan.text = "Perbarui"
    }

    private fun simpan() {
        val nim = etNim.text.toString().trim()
        val nama = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val jurusan = etJurusan.text.toString().trim()
        val nomorHp = etNomorHp.text.toString().trim()

        if (!validasi(nim, nama, nomorHp)) return

        val mhs = Mahasiswa(
            id = editId ?: 0,
            nim = nim,
            nama = nama,
            alamat = alamat,
            jurusan = jurusan,
            nomorHp = nomorHp
        )

        val berhasil = if (editId == null) {
            db.insertMahasiswa(mhs) != -1L
        } else {
            db.updateMahasiswa(mhs) > 0
        }

        if (berhasil) {
            val pesan = if (editId == null) {
                "Data berhasil ditambahkan"
            } else {
                "Data berhasil diperbarui"
            }

            Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(
                this,
                "Gagal menyimpan data",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun validasi(
        nim: String,
        nama: String,
        nomorHp: String
    ): Boolean {

        if (nim.isEmpty()) {
            etNim.error = "NIM wajib diisi"
            etNim.requestFocus()
            return false
        }

        if (nim.length < 5 || !nim.all { it.isDigit() }) {
            etNim.error = "NIM minimal 5 digit dan harus angka"
            etNim.requestFocus()
            return false
        }

        if (db.isNimTerdaftar(nim, editId)) {
            etNim.error = "NIM sudah terdaftar"
            etNim.requestFocus()
            return false
        }

        if (nama.isEmpty()) {
            etNama.error = "Nama wajib diisi"
            etNama.requestFocus()
            return false
        }

        if (nomorHp.isNotEmpty() && !hpValid(nomorHp)) {
            etNomorHp.error =
                "Nomor HP tidak valid (contoh 081234567890)"
            etNomorHp.requestFocus()
            return false
        }

        return true
    }

    private fun hpValid(hp: String): Boolean {
        return hp.startsWith("08") &&
                hp.length in 10..13 &&
                hp.all { it.isDigit() }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_SUMBER = "sumber"
    }
}
