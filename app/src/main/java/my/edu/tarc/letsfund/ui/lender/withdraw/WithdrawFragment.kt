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
                    binding.editTextMobileNum.visibility = View.INVISIBLE
                    binding.textViewMobileNo.visibility = View.INVISIBLE
                    binding.btnWithdraw1.setTextColor(R.color.black)
                    binding.btnWithdraw1.setBackgroundColor(R.color.white)
                    binding.btnWithdraw1.isEnabled = false
                    binding.btnWithdraw1.isClickable = false
                }
                else if (position == 1){
                    binding.editTextMobileNum.visibility = View.VISIBLE
                    binding.textViewMobileNo.visibility = View.VISIBLE
                    binding.btnWithdraw1.isEnabled = true
                    binding.btnWithdraw1.isClickable = true
                    binding.btnWithdraw1.setTextColor(R.color.white)
                    binding.btnWithdraw1.setBackgroundColor(R.color.light_green)
                }
                else if (position == 2){
                    binding.editTextMobileNum.visibility = View.INVISIBLE
                    binding.textViewMobileNo.visibility = View.INVISIBLE
                    binding.btnWithdraw1.isEnabled = true
                    binding.btnWithdraw1.isClickable = true
                    binding.btnWithdraw1.setTextColor(R.color.white)
                    binding.btnWithdraw1.setBackgroundColor(R.color.light_green)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.btnWithdraw1.isActivated = false
            }

        }





        return root
    }

}