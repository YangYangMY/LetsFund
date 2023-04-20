package my.edu.tarc.letsfund.ui.borrower.applyloan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.letsfund.databinding.FragmentApplyloanBinding

class ApplyLoanFragment : Fragment() {

    private var _binding: FragmentApplyloanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val applyLoanViewModel =
            ViewModelProvider(this).get(ApplyLoanViewModel::class.java)

        _binding = FragmentApplyloanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        applyLoanViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}