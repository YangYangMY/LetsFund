package my.edu.tarc.letsfund.ui.lender.lenderhome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.letsfund.databinding.FragmentLenderhomeBinding
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity

class LenderHomeFragment : Fragment() {
    private lateinit var scrollView: ScrollView
    private var _binding: FragmentLenderhomeBinding? = null

    private val binding get() = _binding!!

    //Database Initialise
    private lateinit var uid: String
    private lateinit var databaseRef : DatabaseReference
    private lateinit var auth: FirebaseAuth

    //RecyclerView
    private lateinit var requestRecyclerView: RecyclerView
    private lateinit var requestList: ArrayList<BorrowerActivity.BorrowRequest>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLenderhomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        scrollView = binding.fundRequest
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestRecyclerView = binding.recyclerViewRequest
        requestRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        requestRecyclerView.layoutManager
        requestList = arrayListOf<BorrowerActivity.BorrowRequest>()

        getRequestData()
    }


    private fun getRequestData(){
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()

        requestRecyclerView.visibility = View.VISIBLE
        var count = 1
        getLoanListNumber{totalLoan ->
            while (count < totalLoan){
                databaseRef = FirebaseDatabase.getInstance().getReference("LoanLists").child(auth.currentUser!!.uid).child(count.toString())

                databaseRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        requestList.clear()
                        if (snapshot.exists()) {
                            for (requestSnapshot in snapshot.children) {
                                val requestData =
                                    requestSnapshot.getValue(BorrowerActivity.BorrowRequest::class.java)
                                requestList.add(requestData!!)
                                requestRecyclerView.visibility = View.VISIBLE
                            }

                            requestRecyclerView.adapter = RequestAdapter(requestList)
                            binding.fundRequest.fullScroll(View.FOCUS_UP)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Failed to access database", Toast.LENGTH_SHORT).show()
                    }

                })
                count++
            }
        }
    }

    private fun getLoanListNumber(onComplete: (Int) -> Unit) {
        val databaseRefReadTransaction = FirebaseDatabase.getInstance().getReference("LoanLists")
        databaseRefReadTransaction.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val numberOfTransactions = snapshot.childrenCount.toInt()
                val newTransactionNumber = if (numberOfTransactions > 0) numberOfTransactions + 1 else 1
                onComplete(newTransactionNumber)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(-1) // indicates an error occurred
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}