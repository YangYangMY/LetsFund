package my.edu.tarc.letsfund.ui.lender.withdraw

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.selects.select
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.FragmentWithdrawBinding

class WithdrawFragment : Fragment() {

    private var _binding: FragmentWithdrawBinding? = null
    private val binding get() = _binding!!
    private var selectedWithdrawOptionCheck: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWithdrawBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.spinnerWithdrawOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            @SuppressLint("ResourceAsColor")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0){
                    //Reset
                    selectedWithdrawOptionCheck = 0
                    binding.editTextMobileNum.visibility = View.INVISIBLE
                    binding.textViewMobileNo.visibility = View.INVISIBLE
                    binding.textViewBank.visibility = View.INVISIBLE
                    binding.spinnerBank.visibility = View.INVISIBLE
                    binding.editTextBankAcc.visibility = View.INVISIBLE
                    binding.textViewAccNo.visibility = View.INVISIBLE
                    binding.btnWithdraw1.visibility = View.VISIBLE
                    binding.btnWithdraw2.visibility = View.GONE
                    binding.spinnerBank.setSelection(0)
                    binding.editTextBankAcc.setText("")
                    binding.editTextMobileNum.setText("")
                    binding.textViewErrorMobile.visibility = View.INVISIBLE
                    binding.textViewErrorMobile.setText("")
                    binding.textViewErrorBank.visibility = View.INVISIBLE
                    binding.textViewErrorBank.setText("")
                    binding.textViewErrorBankAcc.visibility = View.INVISIBLE
                    binding.textViewErrorBankAcc.setText("")
                }
                else if (position == 1){
                    // Reset
                    selectedWithdrawOptionCheck = 1
                    binding.editTextMobileNum.visibility = View.VISIBLE
                    binding.textViewMobileNo.visibility = View.VISIBLE
                    binding.textViewBank.visibility = View.INVISIBLE
                    binding.spinnerBank.visibility = View.INVISIBLE
                   binding.editTextBankAcc.visibility = View.INVISIBLE
                   binding.textViewAccNo.visibility = View.INVISIBLE
                    binding.btnWithdraw1.visibility = View.GONE
                    binding.btnWithdraw2.visibility = View.VISIBLE
                    binding.spinnerBank.setSelection(0)
                    binding.editTextBankAcc.setText("")
                    binding.textViewErrorMobile.visibility = View.VISIBLE
                    binding.textViewErrorBank.visibility = View.INVISIBLE
                    binding.textViewErrorBankAcc.visibility = View.INVISIBLE
                    binding.textViewErrorBank.setText("")
                    binding.textViewErrorBankAcc.setText("")
                }
                else if (position == 2){
                    // Reset
                    selectedWithdrawOptionCheck = 2
                    binding.editTextMobileNum.visibility = View.INVISIBLE
                    binding.textViewMobileNo.visibility = View.INVISIBLE
                    binding.textViewBank.visibility = View.VISIBLE
                    binding.spinnerBank.visibility = View.VISIBLE
                    binding.btnWithdraw1.visibility = View.GONE
                    binding.btnWithdraw2.visibility = View.VISIBLE
                    binding.editTextMobileNum.setText("")
                    binding.textViewErrorBank.visibility = View.VISIBLE
                    binding.textViewErrorBankAcc.visibility = View.VISIBLE
                    binding.textViewErrorMobile.visibility = View.INVISIBLE
                    binding.textViewErrorMobile.setText("")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.btnWithdraw1.visibility = View.VISIBLE
                binding.btnWithdraw2.visibility = View.GONE
            }
        }
        binding.spinnerBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position == 0){
                    binding.editTextBankAcc.setText("")
                    binding.textViewAccNo.visibility = View.INVISIBLE
                    binding.editTextBankAcc.visibility = View.INVISIBLE
                    binding.textViewErrorBank.setText("")
                    binding.textViewErrorBankAcc.setText("")
                }
                else if(position == 1){
                    binding.editTextBankAcc.filters = arrayOf(InputFilter.LengthFilter(10))
                    binding.editTextBankAcc.setText("")
                    binding.textViewAccNo.visibility = View.VISIBLE
                    binding.editTextBankAcc.visibility = View.VISIBLE
                    binding.textViewErrorBankAcc.setText("")
                }
                else if (position == 2){
                    binding.editTextBankAcc.filters = arrayOf(InputFilter.LengthFilter(12))
                    binding.editTextBankAcc.setText("")
                    binding.textViewAccNo.visibility = View.VISIBLE
                    binding.editTextBankAcc.visibility = View.VISIBLE
                    binding.textViewErrorBankAcc.setText("")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //No Action
            }

        }

        binding.btnWithdraw2.setOnClickListener {
            if(selectedWithdrawOptionCheck == 1){
                binding.textViewErrorMobile.text = validPhone()
            }else if(selectedWithdrawOptionCheck == 2){
                binding.textViewErrorBank.text = validBankSelect()
                if(binding.spinnerBank.selectedItemPosition == 1 || binding.spinnerBank.selectedItemPosition == 2){
                    binding.textViewErrorBankAcc.text = validBankAcc()
                }

            }


        }



        return root
    }

    private fun validBankAcc(): String? {
        val bankAccNum = binding.editTextBankAcc.text.toString()
        val selectOption = binding.spinnerBank.selectedItemPosition
        if(bankAccNum.isEmpty()){
            return "Required"
        }
        if(selectOption == 1){
            if(bankAccNum.length != 10){
                return "Must be 10 Digits"
            }
        }else if(selectOption == 2){
            if(bankAccNum.length != 12){
                return "Must be 12 Digits"
            }
        }
        return null
    }

    private fun validPhone(): String? {
        val phoneText = binding.editTextMobileNum.text.toString()
        if (phoneText.isEmpty()) {
            return "Required"
        }
        if (!phoneText.matches(".*[0-9].*".toRegex())) {
            return "Must be all Digits"
        }
        if(phoneText.length != 10) {
            return "Must be 10 Digits"
        }
        return null
    }

    private fun validBankSelect(): String? {
        val selectOption = binding.spinnerBank.selectedItemPosition

        if(selectOption == 0){
            return "Please select a bank"
        }

        return null
    }


}