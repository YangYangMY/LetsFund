package my.edu.tarc.letsfund.ui.lender.lenderhome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity.BorrowRequest
import my.edu.tarc.letsfund.ui.lender.LenderActivity
import my.edu.tarc.letsfund.ui.lender.lenderhome.RequestAdapter.MyViewHolder

class RequestAdapter(private val requestList: ArrayList<BorrowRequest>) :
    RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fund_record, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val request = requestList[position]
        holder.borrowerNameView.text = request.borrowerName;
        holder.loanAmountView.text = request.loanAmount.toString();
        holder.descriptionView.text = request.loanDesc;
        holder.loanReqEndDateView.text = request.loanReqEndDate;

    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var borrowerNameView: TextView = itemView.findViewById(R.id.borrowerName)
        var loanAmountView: TextView = itemView.findViewById(R.id.lendAmount)
        var descriptionView: TextView = itemView.findViewById(R.id.fundDesc)
        var loanReqEndDateView: TextView = itemView.findViewById(R.id.fundDate)

    }
}