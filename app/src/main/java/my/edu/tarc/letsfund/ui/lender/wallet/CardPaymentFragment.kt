package my.edu.tarc.letsfund.ui.lender.wallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.edu.tarc.letsfund.R
import androidx.navigation.fragment.findNavController
import androidx.activity.result.contract.ActivityResultContracts

class CardPaymentFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card_payment, container, false)
    }


}