package my.edu.tarc.letsfund.ui.lender.lenderhome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity
import com.bumptech.glide.Glide
import android.content.Context

class RequestAdapter(private val requestList: ArrayList<BorrowerActivity.BorrowRequest>) :
    RecyclerView.Adapter<RequestAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.record_fund, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context

        val request = requestList[position]
        holder.borrowerNameView.text = request.borrowerName;
        holder.loanAmountView.text = request.loanAmount.toString();
        holder.descriptionView.text = request.loanDesc;
        holder.loanReqEndDateView.text = request.loanReqEndDate;

        Glide.with(context)
            .load(request.uri)
            .into(holder.fundImage)

        //holder.fundImage.setImageURI(request.uri)

    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var fundImage: ImageView = itemView.findViewById(R.id.fundImage)
        var borrowerNameView: TextView = itemView.findViewById(R.id.borrowerName)
        var loanAmountView: TextView = itemView.findViewById(R.id.fundAmount)
        var descriptionView: TextView = itemView.findViewById(R.id.fundDesc)
        var loanReqEndDateView: TextView = itemView.findViewById(R.id.fundDate)
        var button: Button = itemView.findViewById(R.id.button)

        //button.setOnClickListener(this)
    }


}