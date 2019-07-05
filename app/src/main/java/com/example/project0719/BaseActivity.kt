package com.example.project0719

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity: AppCompatActivity() {

    private lateinit var loader: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loader = ProgressDialog(this)
        loader.setMessage("Loading, please wait.")
        loader.setCancelable(false)
    }

    fun showLoader() {
        loader.show()
    }

    fun hideLoader() {
        loader.hide()
    }
}