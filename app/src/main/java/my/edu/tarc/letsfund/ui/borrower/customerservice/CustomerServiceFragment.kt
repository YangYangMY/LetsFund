package my.edu.tarc.letsfund.ui.borrower.customerservice

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

class CustomerServiceFragment : Fragment() {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var faqAdapter: FaqAdapter
    private lateinit var faqList: MutableList<Faq>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ExpandableListView
        expandableListView = view.findViewById(R.id.expandableListViewFAQ)
        faqList = mutableListOf()
        faqAdapter = FaqAdapter(requireContext(), faqList)
        expandableListView.setAdapter(faqAdapter)

        // Add FAQ data
        faqList.add(Faq("What is LetsFund?", "LetsFund is a local nonprofit, founded in 2023 in Kuala Lumpur, led by Tam Ga Men, Lee Wee Yan, Andrew Hew and Cheong Tzen Yang, with a mission to connect people through lending to alleviate poverty."))
        faqList.add(Faq("Who can borrow on LetsFund?", "Any entrepreneur, in any country, can apply for a loan on LetsFund"))
        faqAdapter.notifyDataSetChanged()

        faqList.add(Faq("What is the interest rate for LetsFund loans?", "LetsFund loans are interest-free."))
        faqList.add(Faq("How long does it take for a loan to be funded?", "It typically depends on the lenders to loan. The time it takes depends on many factors, including the amount requested, and the generosity of our lenders."))
        faqAdapter.notifyDataSetChanged()


        val previousButton = view.findViewById<Button>(R.id.btnPrevious)
        previousButton.setOnClickListener {
            findNavController().navigate(R.id.action_customerServiceFragment_to_navigation_borrowerprofile)
        }

    }
}
