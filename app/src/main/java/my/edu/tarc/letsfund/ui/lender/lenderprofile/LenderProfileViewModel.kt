package my.edu.tarc.letsfund.ui.lender.lenderprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LenderProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Profile"
    }
    val text: LiveData<String> = _text
}