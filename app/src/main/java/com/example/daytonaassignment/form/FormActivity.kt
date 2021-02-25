package com.example.daytonaassignment.form

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.daytonaassignment.R
import com.example.daytonaassignment.mapmyindia.MapinIndiaActivity
import com.example.daytonaassignment.mapmyindia.MapinIndiaVM
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.activity_option.*
import javax.inject.Inject

class FormActivity : AppCompatActivity(), MapFragment.MapListener {

    @Inject
    lateinit var viewModel: MapVM

    private lateinit var formFragment : FormFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        formFragment = FormFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, formFragment).addToBackStack("Form").commit()

    }

    fun replaceFrag(){
        supportFragmentManager.beginTransaction().replace(R.id.container, MapFragment()).addToBackStack("Form").commit()
    }

    override fun selectedPlace(location: String) {
        Log.d("*** LOCATION FORM ACTIVITY = ", ""+location)
        formFragment.getSelectedLocation(location)
    }

    fun openActivityForResult() {
        startActivityForResult(Intent(this, MapinIndiaActivity::class.java), 102)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 102){
            Log.d("*** ",""+data?.getStringExtra("address"))
            formFragment.getSelectedLocation(""+data?.getStringExtra("address"))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        Log.d("*** Backstack entry count = ", ""+supportFragmentManager.backStackEntryCount)
    }
}
