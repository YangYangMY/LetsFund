package my.edu.tarc.letsfund.ui.borrower.applyloan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ApplyLoanViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is apply loan Fragment"
    }
    val text: LiveData<String> = _text
}