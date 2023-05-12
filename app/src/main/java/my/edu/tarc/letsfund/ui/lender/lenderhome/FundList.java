package my.edu.tarc.letsfund.ui.lender.lenderhome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.edu.tarc.letsfund.R;
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity;
import my.edu.tarc.letsfund.ui.lender.LenderActivity;

public class FundList extends RecyclerView.Adapter<FundList.MyViewHolder> {

    Context context;

    ArrayList<BorrowerActivity.BorrowRequest> fundList;

    public FundList(Context context, ArrayList<BorrowerActivity.BorrowRequest> fundListArrayList) {
        this.context = context;
        this.fundList = fundListArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.fund_record, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return fundList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView borrowerName, loanAmount, description, loanReqEndDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            borrowerName = itemView.findViewById(R.id.borrowerName);
            loanAmount = itemView.findViewById(R.id.lendAmount);
            description = itemView.findViewById(R.id.fundDesc);
            loanReqEndDate = itemView.findViewById(R.id.fundDate);
        }
    }
}
