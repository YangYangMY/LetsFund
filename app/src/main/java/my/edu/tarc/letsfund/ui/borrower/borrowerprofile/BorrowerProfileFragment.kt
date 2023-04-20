package my.edu.tarc.letsfund.ui.borrower.borrowerprofile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.letsfund.databinding.FragmentBorrowerprofileBinding
import my.edu.tarc.letsfund.ui.login.LoginActivity



class BorrowerProfileFragment : Fragment() {


    private var _binding: FragmentBorrowerprofileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val borrowerProfileViewModel =
            ViewModelProvider(this).get(BorrowerProfileViewModel::class.java)

        _binding = FragmentBorrowerprofileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.profile
        borrowerProfileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}