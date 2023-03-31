package com.egyyazilim.fotografpaylasma

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import java.util.*

class FotografPaylasmaActivity : AppCompatActivity() {
    var secilenGorsel: Uri?=null
    var secilenBitmap:Bitmap?=null
    private lateinit var storage: FirebaseStorage
    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fotograf_paylasma)
        storage=FirebaseStorage.getInstance()
        auth=FirebaseAuth.getInstance()
        database=FirebaseFirestore.getInstance()
    }
    fun paylas(view: View) {
        //depo işlemleri
        //UUID -Unıversal unıque ıd
        val uuıd = UUID.randomUUID()
        val gorselIsmı = "${uuıd}.jpg"

        val refrence = storage.reference
        val gorsekReference = refrence.child("İmages").child(gorselIsmı)
        if (secilenGorsel != null) {
            gorsekReference.putFile(secilenGorsel!!).addOnSuccessListener { taskSnapsot ->
                val yuklenenGorselRef =
                    FirebaseStorage.getInstance().reference.child("İmages").child(gorselIsmı)
                yuklenenGorselRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val guncelKullanıcıEmaıl = auth.currentUser!!.email.toString()
                    val yorumText = findViewById<EditText>(R.id.yorumText)
                    val kullanıcıYorumu = yorumText.text.toString()
                    val tarih = Timestamp.now()

                    //verıtabanı ıslemlerı
                    val postHashMap = hashMapOf<String, Any>()
                    postHashMap.put("gorselUrl", downloadUrl)
                    postHashMap.put("kullanıcıEmail", guncelKullanıcıEmaıl)
                    postHashMap.put("kulllanıcıyorum", kullanıcıYorumu)
                    postHashMap.put("tarıh", tarih)

                    database.collection("Post").add(postHashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            finish()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(
                            applicationContext,
                            exception.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }
            }


        }

    }
    fun gorselSec(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //İZNİ almamışız
            ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 1)
        } else {
            //galerıye gıdılıyor-İzin zaten varsa
            val galeriIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent, 2)

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode ==1){
            if (grantResults.size>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                //izin verilince yapılacaklar
                val galeriIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode ==2 && resultCode == Activity.RESULT_OK && data!=null){
            secilenGorsel=data.data
            var imageView=findViewById<ImageView>(R.id.imageView)
            if (secilenGorsel!=null){
                if (Build.VERSION.SDK_INT>=28){
                    val source=ImageDecoder.createSource(this.contentResolver,secilenGorsel!!)
                    secilenBitmap=ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(secilenBitmap)


                }else{
                    secilenBitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,secilenGorsel)
                    imageView.setImageBitmap(secilenBitmap)//6,17
                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}