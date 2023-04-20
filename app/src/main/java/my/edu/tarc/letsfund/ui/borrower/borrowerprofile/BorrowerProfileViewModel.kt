package my.edu.tarc.letsfund.ui.borrower.borrowerprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BorrowerProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Profile"
    }
    val text: LiveData<String> = _text
}