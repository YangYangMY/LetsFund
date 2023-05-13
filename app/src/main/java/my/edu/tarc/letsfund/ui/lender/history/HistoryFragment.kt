package my.edu.tarc.letsfund.ui.lender.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.letsfund.databinding.FragmentLenderhistoryBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.letsfund.databinding.FragmentLenderhistoryBinding
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity
import my.edu.tarc.letsfund.ui.lender.LenderActivity

class HistoryFragment : Fragment() {
    private lateinit var scrollView: ScrollView
    private var _binding: FragmentLenderhistoryBinding? = null

    //Database Initialise
    private lateinit var uid: String
    private lateinit var databaseRef : DatabaseReference
    private lateinit var auth: FirebaseAuth

    //RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var lendHistory: ArrayList<LenderActivity.LendHistory>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLenderhistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        scrollView = binding.lendHistory

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyRecyclerView = binding.recyclerViewHistory
        historyRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        historyRecyclerView.layoutManager
        lendHistory = arrayListOf<LenderActivity.LendHistory>()

        getHistoryData()
    }

    private fun getHistoryData(){
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()



        databaseRef=
            FirebaseDatabase.getInstance().getReference("LendHistory").child(auth.currentUser!!.uid)

        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                lendHistory.clear()
                if(snapshot.exists()){
                    for(historySnapshot in snapshot.children){
                        val historyData = historySnapshot.getValue(LenderActivity.LendHistory::class.java)
                        lendHistory.add(historyData!!)
                    }

                    val historyAdapter = HistoryAdapter(lendHistory)
                    historyRecyclerView.adapter = historyAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to access database", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}