package my.edu.tarc.letsfund.ui.lender.lenderhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LenderHomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is lender home Fragment"
    }
    val text: LiveData<String> = _text
}