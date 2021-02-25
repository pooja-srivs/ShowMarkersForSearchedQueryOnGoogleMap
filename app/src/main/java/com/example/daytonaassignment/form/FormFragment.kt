package com.example.daytonaassignment.form

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.daytonaassignment.R
import com.example.daytonaassignment.mapmyindia.MapinIndiaVM
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.fragment_form.*
import javax.inject.Inject

class FormFragment : Fragment() {

    @Inject
    lateinit var viewModel: MapVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel = ViewModelProvider(this)
//            .get(MapVM::class.java)

        btn_intent_map.setOnClickListener {
            (activity as FormActivity).openActivityForResult()
        }

        addObserver()
    }

    private fun addObserver(){

//        viewModel.placesLiveData.observe(requireActivity(), Observer {
//
//            Log.d("*** FormFragment = ", ""+it)
//        })
    }

    fun getSelectedLocation(location : String){

        tv_address.setText(location)
        Log.d("*** Location Form Fragment = ", ""+location)
    }
}