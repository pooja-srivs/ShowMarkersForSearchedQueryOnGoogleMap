package com.example.daytonaassignment.form

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.daytonaassignment.R
import com.example.daytonaassignment.mapmyindia.MapinIndiaActivity
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.activity_option.*
import kotlinx.android.synthetic.main.activity_option.btn_activity_intent
import kotlinx.android.synthetic.main.activity_option.btn_frag_intent
import kotlinx.android.synthetic.main.activity_option.tv_user_address

class OptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)

        btn_activity_intent.setOnClickListener {
            startActivityForResult(Intent(this, MapinIndiaActivity::class.java), 102)
        }

        btn_frag_intent.setOnClickListener {
            startActivityForResult(Intent(this, FormActivity::class.java), 102)
//            supportFragmentManager.beginTransaction().add(R.id.container, FormFragment()).commit()
        }
    }

    fun replaceFrag(){
        supportFragmentManager.beginTransaction().replace(R.id.container, MapFragment()).addToBackStack("Form").commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 102){
            tv_user_address.setText(""+data?.getStringExtra("address"))
        }
    }
}