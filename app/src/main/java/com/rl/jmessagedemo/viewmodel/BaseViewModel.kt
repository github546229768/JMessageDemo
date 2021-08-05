package com.rl.jmessagedemo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    var loadingEvent  = MutableLiveData<Boolean>()
        private set
}