package com.example.daytonaassignment

import com.example.daytonaassignment.di.DaggerAppComponent
import com.example.daytonaassignment.di.modules.DatabaseResolver
import com.mapbox.mapboxsdk.MapmyIndia
import com.mmi.services.account.MapmyIndiaAccountManager
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class MyApp : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        /*  MapmyIndiaAccountManager.getInstance().restAPIKey = "hxhuuxpuv1fld3x5iywyyl1jpkexcvps"
      MapmyIndiaAccountManager.getInstance().mapSDKKey = "s7ymgngc189paj74h33r1fmpbdym8nqz"
      MapmyIndiaAccountManager.getInstance().atlasClientId = "33OkryzDZsIPpKdCDcWqpfoGvluX5e3ENsWSqfyumc1QbWfgSeMC8oneW9XVrqnR4tsyE3_vbHqeqVxqpxa457lRYjnnCiTVAmrOJN8IZpMm7A75hBoV5w=="
      MapmyIndiaAccountManager.getInstance().atlasClientSecret = "lrFxI-iSEg8XC1YkQ_EuWFfpVShSmjrVpSXdhPVqHF2zG3wPZH9misBuZpXf4JZ448q_LPjdtPopLlefFS_zVSWe-eb1eeJuH_KpsSd9ZPMemA7bG4ojd7IKyhh9gmfA"
      //      MapmyIndiaAccountManager.getInstance().atlasGrantType = getAtlasGrantType()
      */
        
        MapmyIndiaAccountManager.getInstance().restAPIKey = "gxb5j4xv5vrvm4llv29qsj45pn5wjd4y"
        MapmyIndiaAccountManager.getInstance().mapSDKKey = "aff6uc1b8sit8fe3hys9h8pi7tqqju15"
        MapmyIndiaAccountManager.getInstance().atlasClientId = "33OkryzDZsLm0UQCEY4KxaUT1hsOsjHj338ABzZ1-kaSN5dieuvk7QoYqRwYCXfcDLrP1_4QWq0aLLmab-PcXuLsbVjHXNaHb6o7CpIq9KJO4r5YOZ8M1w=="
        MapmyIndiaAccountManager.getInstance().atlasClientSecret = "lrFxI-iSEg9C2UEcHSXpMOGTLiVkdwzJVJz719v6cawlYboZb9nJzwVSRPXZWmKm1qsnO4yy4ShcT3eqWIpd9qIhBesIEzLcRf7thfgNykPuP_CfvUpuggg1YFS7Krdl"
        //      MapmyIndiaAccountManager.getInstance().atlasGrantType = getAtlasGrantType()
        MapmyIndia.getInstance(this)
    }
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = DaggerAppComponent
        .builder()
        .application(this)
        .build()
}