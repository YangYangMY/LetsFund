package my.edu.tarc.letsfund.ui.lender.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WalletViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "500"
    }
    val text: LiveData<String> = _text
}