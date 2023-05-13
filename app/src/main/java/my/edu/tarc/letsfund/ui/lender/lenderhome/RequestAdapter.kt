package my.edu.tarc.letsfund.ui.lender.lenderhome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity


class RequestAdapter(private val requestList: ArrayList<BorrowerActivity.BorrowRequest>) :
    RecyclerView.Adapter<RequestAdapter.MyViewHolder>() {

    //Initialise Database
    private lateinit var uid: String
    private lateinit var databaseRef : DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.record_fund, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val request = requestList[position]
        holder.borrowerNameView.text = request.borrowerName;
        holder.loanAmountView.text = request.loanAmount.toString();
        holder.descriptionView.text = request.loanDesc;
        holder.loanReqEndDateView.text = request.loanReqEndDate;

        // Set a unique identifier or tag for each button
        holder.buttonLend.tag = position

        // Set an OnClickListener for the button
        holder.buttonLend.setOnClickListener { view ->
            val buttonPosition = view.tag as Int

            //initialise database
            auth = FirebaseAuth.getInstance()
            uid = auth.currentUser?.uid.toString()
            database = FirebaseDatabase.getInstance()


            // Insert lender ID to Loan
            val databaseRefLenderIDLoan =
                database.reference.child("Loans").child(auth.currentUser!!.uid)


        }

    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var borrowerNameView: TextView = itemView.findViewById(R.id.borrowerName)
        var loanAmountView: TextView = itemView.findViewById(R.id.fundAmount)
        var descriptionView: TextView = itemView.findViewById(R.id.fundDesc)
        var loanReqEndDateView: TextView = itemView.findViewById(R.id.fundDate)
        var buttonLend : Button = itemView.findViewById(R.id.btnLend)

    }
}