package my.edu.tarc.letsfund.ui.lender.lenderhome

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity
import my.edu.tarc.letsfund.ui.lender.LenderActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter


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

            getWalletAmount {currentWallet ->
                val loanAmount = request.loanAmount?.toDouble()

                if (loanAmount != null && loanAmount < currentWallet!!) {
                    showDialog(holder.itemView.context, requestList[buttonPosition])
                } else {
                    showInvalidDialog(holder.itemView.context)

                }
            }




        }

    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    private fun showInvalidDialog(context: Context){
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Insufficient Balance")
            .setMessage("Please reload your balance in wallet page")
            .setPositiveButton("Close") { _, _ ->

            }
        builder.create().show()
    }

    private fun showDialog(context: Context, request: BorrowerActivity.BorrowRequest){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Lend Now?")
        builder.setMessage("Do you want to lend to ${request.borrowerName}?")
        builder.setPositiveButton("Yes"){ dialog, which ->

            //initialise database
            auth = FirebaseAuth.getInstance()
            uid = auth.currentUser?.uid.toString()
            database = FirebaseDatabase.getInstance()

            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formattedDate = (today.format(formatter)).toString()

            // Initialize Value
            val lenderID = uid
            val borrowerID = request.borrowerID.toString()

            // Update Status for Loan
            val databaseRefStatusLoan =
                database.reference.child("Loans").child(borrowerID)
            databaseRefStatusLoan.child("status").setValue("Borrowed")

            //Update Status for LoanLists
            var count = 1
            var FoundLocation = 1
            getLoanListNumber{totalLoan ->

                while (count < totalLoan) {
                    databaseRef =
                        FirebaseDatabase.getInstance().getReference("LoanLists").child(count.toString())
                    databaseRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                if(snapshot.getValue(BorrowerActivity.BorrowRequest::class.java)?.borrowerID == borrowerID){
                                    FoundLocation = count
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            //No Function dk how to display Toast
                        }
                    })
                    count++
                }
            }
            val databaseRefStatusLoanLists =
                database.reference.child("LoanLists").child(FoundLocation.toString())
            databaseRefStatusLoanLists.child("status").setValue("Borrowed")

            //Insert LenderID to Loan and LoanList
            databaseRefStatusLoan.child("lenderID").setValue(lenderID)
            databaseRefStatusLoanLists.child("lenderID").setValue(lenderID)

            //Update Transaction History
            getTransactionNumber { transactionNumber ->
                if (transactionNumber != -1){
                    val transactionID = transactionNumber.toString()

                    val databaseRefTransactionHistory =
                        database.reference.child("TransactionHistory").child(uid)
                            .child(transactionID)
                    val walletTransaction: LenderActivity.PaymentHistory =
                        LenderActivity.PaymentHistory(formattedDate, "Lent",
                            request.loanAmount?.toDouble()
                        )
                    databaseRefTransactionHistory.setValue(walletTransaction)

                    //Update Wallet
                    val databaseRefWallet = FirebaseDatabase.getInstance().getReference("Wallet").child(uid)
                    getWalletAmount { currentAmount ->
                        val amount: Double? = request.loanAmount?.let { currentAmount?.minus(it.toDouble()) }
                        val wallet = mapOf<String, Double?>(
                            "walletAmount" to amount
                        )
                        databaseRefWallet.setValue(wallet)
                    }
                }
            }

            // Insert Lender History
            getLendHistoryNumber{lendNumber ->
                if (lendNumber != -1) {
                    val transactionID = lendNumber.toString()
                    val databaseRefLendHistory = FirebaseDatabase.getInstance().getReference("LendHistory").child(uid).child(transactionID)
                    val lendList = LenderActivity.LendHistory(
                        lendDate = formattedDate,
                        borrowerName = request.borrowerName,
                        loanAmount = request.loanAmount?.toDouble(),
                        repaymentStatus = "Unpaid"
                    )
                    databaseRefLendHistory.setValue(lendList)
                }

            }


            Toast.makeText(context, "Succeed", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("No"){ dialog, which ->
            Toast.makeText(context, "An Error Occurred", Toast.LENGTH_SHORT).show()
        }

        builder.create().show()
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

    private fun getWalletAmount(callback: (Double?) -> Unit) {

        //initialise database
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("Wallet")

        databaseRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val wallet = snapshot.getValue(LenderActivity.Wallet::class.java)
                val amount = wallet?.walletAmount
                callback(amount)
            }

            override fun onCancelled(error: DatabaseError) {
                //No Function dk how to display Toast
            }
        })
    }

    private fun getLendHistoryNumber(onComplete: (Int) -> Unit) {
        val databaseRefReadTransaction = FirebaseDatabase.getInstance().getReference("LendHistory").child(uid)
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

    private fun getTransactionNumber(onComplete: (Int) -> Unit) {
        val databaseRefReadTransaction = FirebaseDatabase.getInstance().getReference("TransactionHistory").child(uid)
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

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var borrowerNameView: TextView = itemView.findViewById(R.id.borrowerName)
        var loanAmountView: TextView = itemView.findViewById(R.id.fundAmount)
        var descriptionView: TextView = itemView.findViewById(R.id.fundDesc)
        var loanReqEndDateView: TextView = itemView.findViewById(R.id.fundDate)
        var buttonLend : Button = itemView.findViewById(R.id.btnLend)

    }
}