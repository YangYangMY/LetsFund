package my.edu.tarc.letsfund.ui.lender.lend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LendViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Lend Fragment"
    }
    val text: LiveData<String> = _text
}