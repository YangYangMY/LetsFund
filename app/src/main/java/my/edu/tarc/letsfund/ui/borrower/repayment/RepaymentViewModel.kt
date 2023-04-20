package my.edu.tarc.letsfund.ui.borrower.repayment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RepaymentViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is repayment Fragment"
    }
    val text: LiveData<String> = _text
}