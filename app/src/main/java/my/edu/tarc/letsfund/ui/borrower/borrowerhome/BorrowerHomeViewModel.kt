package my.edu.tarc.letsfund.ui.borrower.borrowerhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BorrowerHomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is borrower home Fragment"
    }
    val text: LiveData<String> = _text
}