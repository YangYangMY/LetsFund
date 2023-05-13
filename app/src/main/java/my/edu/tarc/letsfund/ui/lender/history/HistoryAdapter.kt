package my.edu.tarc.letsfund.ui.lender.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.ui.lender.LenderActivity
import my.edu.tarc.letsfund.ui.lender.lenderhome.RequestAdapter

class HistoryAdapter(private val lendHistory: ArrayList<LenderActivity.LendHistory>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.record_lenderhistory, parent, false)
        return HistoryViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return lendHistory.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = lendHistory[position]

        holder.borrowerNameView.text = history.borrowerName;
        holder.loanAmountView.text = history.loanAmount.toString();
        holder.loanDateView.text = history.lendDate;
        holder.repaymentStatusView.text = history.repaymentStatus;
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var borrowerNameView : TextView = itemView.findViewById(R.id.textViewBorrowerName)
        var loanAmountView : TextView = itemView.findViewById(R.id.textViewLoanAmount)
        var loanDateView : TextView = itemView.findViewById(R.id.textViewLoanDate)
        var repaymentStatusView : TextView = itemView.findViewById(R.id.textViewRepayStatus)
    }


}