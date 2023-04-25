package my.edu.tarc.letsfund.ui.lender.lenderprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LenderProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Lender Profile Fragment"
    }
    val text: LiveData<String> = _text
}