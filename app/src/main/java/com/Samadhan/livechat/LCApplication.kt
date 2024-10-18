package com.Samadhan.livechat

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LCApplication : Application(){
    val tempData :HashMap<String,String> = HashMap()
}