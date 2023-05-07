package my.edu.tarc.letsfund.ui.lender.withdraw

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.compose.ui.graphics.Color
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.FragmentWithdrawBinding

class WithdrawFragment : Fragment() {

    private var _binding: FragmentWithdrawBinding? = null
    private val binding get() = _binding!!
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
                }
                else if (position == 1){
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
                }
                else if (position == 2){
                    binding.editTextMobileNum.visibility = View.INVISIBLE
                    binding.textViewMobileNo.visibility = View.INVISIBLE
                    binding.textViewBank.visibility = View.VISIBLE
                    binding.spinnerBank.visibility = View.VISIBLE
                    binding.editTextBankAcc.visibility = View.VISIBLE
                    binding.textViewBankAccountNo.visibility = View.VISIBLE
                    binding.textViewAccNo.visibility = View.VISIBLE
                    binding.btnWithdraw1.visibility = View.GONE
                    binding.btnWithdraw2.visibility = View.VISIBLE
                    binding.editTextMobileNum.setText("")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.btnWithdraw1.visibility = View.VISIBLE
                binding.btnWithdraw2.visibility = View.GONE
            }



        }





        return root
    }

}