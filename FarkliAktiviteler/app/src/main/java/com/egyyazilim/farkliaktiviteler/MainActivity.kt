package com.egyyazilim.farkliaktiviteler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.egyyazilim.farkliaktiviteler.MainActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStop() {
        super.onStop()
        println("onStop Çağrıldı")
    }

    override fun onResume() {
        super.onResume()
        println("onResume Çağrıldı")
    }

    override fun onPause() {
        super.onPause()
        println("onPause Çağrıldı")
    }

    override fun onStart() {
        super.onStart()
        println("onStart Çağrıldı")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy Çağrıldı")
    }
    fun activiteDegistir(view:View){

        val kullaniciVerisi=editText.text.toString()
        val intent=Intent(applicationContext,IkinciActivity::class.java)
        intent.putExtra("yollananVeri",kullaniciVerisi)
        startActivity(intent)

    }
}