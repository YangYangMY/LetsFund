package my.edu.tarc.letsfund.ui.borrower.borrowerhome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.letsfund.databinding.FragmentBorrowerhomeBinding

class BorrowerHomeFragment : Fragment() {

    private var _binding: FragmentBorrowerhomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val borrowerHomeViewModel =
            ViewModelProvider(this).get(BorrowerHomeViewModel::class.java)

        _binding = FragmentBorrowerhomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        borrowerHomeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}