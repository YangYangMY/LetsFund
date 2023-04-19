package my.edu.tarc.letsfund.ui.lender.lend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.letsfund.databinding.FragmentLendBinding

class LendFragment : Fragment() {

    private var _binding: FragmentLendBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lendViewModel =
            ViewModelProvider(this).get(LendViewModel::class.java)

        _binding = FragmentLendBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textLend
        lendViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}