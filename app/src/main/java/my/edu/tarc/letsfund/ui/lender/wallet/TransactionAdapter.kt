package my.edu.tarc.letsfund.ui.lender.wallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.ui.lender.LenderActivity

// Create an adapter class for the recycler view
class TransactionAdapter(private val transactionList: List<LenderActivity.PaymentHistory>) :
    RecyclerView.Adapter<TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.record_wallet, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.dateView.text = transaction.date
        holder.methodView.text = transaction.method
        holder.amountView.text = transaction.amount.toString()
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }
}

class TransactionViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateView: TextView = itemView.findViewById(R.id.textViewDate)
        val methodView: TextView = itemView.findViewById(R.id.textViewMethod)
        val amountView: TextView = itemView.findViewById(R.id.textViewAmount)
}







