package my.edu.tarc.letsfund.ui.lender.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.edu.tarc.letsfund.R

data class Faq(val question: String, val answer: String)

class LenderFAQFragment : Fragment() {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var faqAdapter: LenderFAQAdapter
    private lateinit var faqList: MutableList<Faq>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lender_f_a_q, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ExpandableListView
        expandableListView = view.findViewById(R.id.expandableListViewFAQLender)
        faqList = mutableListOf()
        faqAdapter = LenderFAQAdapter(requireContext(), faqList)
        expandableListView.setAdapter(faqAdapter)

        // Add FAQ data
        faqList.add(Faq("What is LetsFund?", "LetsFund is a local nonprofit, founded in 2023 in Kuala Lumpur, led by Tam Ga Men, Lee Wee Yan, Andrew Hew and Cheong Tzen Yang, with a mission to connect people through lending to alleviate poverty."))
        faqList.add(Faq("Who can lend on LetsFund?", "Any individual or organization, in any country, can lend money on LetsFund."))
        faqAdapter.notifyDataSetChanged()

        faqList.add(Faq("What is the return on investment for LetsFund loans?", "LetsFund loans are interest-free, but lenders can earn social returns by supporting entrepreneurs and communities in need."))
        faqList.add(Faq("How long does it take for a loan to be funded?", "It typically depends on the number of lenders who support the loan request. The time it takes can vary, from a few minutes to a few weeks."))
        faqAdapter.notifyDataSetChanged()

        val launchFragmentButton = view.findViewById<Button>(R.id.LenderFAQBackButton)
        launchFragmentButton.setOnClickListener {
            findNavController().navigate(R.id.action_lenderFAQFragment_to_navigation_lenderprofile)
        }
    }
}