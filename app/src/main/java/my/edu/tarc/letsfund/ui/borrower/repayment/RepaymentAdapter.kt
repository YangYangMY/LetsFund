package my.edu.tarc.letsfund.ui.borrower.repayment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity

class RepaymentAdapter(private val repaymentList: List<BorrowerActivity.RepaymentHistory>) :
    RecyclerView.Adapter<RepaymentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepaymentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.record_repay, parent, false)
        return RepaymentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RepaymentViewHolder, position: Int) {
        val transaction = repaymentList[position]
        holder.dateView.text = transaction.date
        holder.nameView.text = transaction.lenderName
        holder.amountView.text = transaction.loanAmount.toString()
    }

    override fun getItemCount(): Int {
        return repaymentList.size
    }
}

class RepaymentViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dateView: TextView = itemView.findViewById(R.id.textViewRepayDate)
    val nameView: TextView = itemView.findViewById(R.id.textViewRepayName)
    val amountView: TextView = itemView.findViewById(R.id.textViewRepayAmount)
}