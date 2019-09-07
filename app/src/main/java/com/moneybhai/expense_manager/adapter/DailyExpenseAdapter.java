package com.moneybhai.expense_manager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moneybhai.expense_manager.R;
import com.moneybhai.expense_manager.model.DailyExpense;

import java.util.List;

public class DailyExpenseAdapter extends RecyclerView.Adapter<DailyExpenseAdapter.DailyExpenseViewHolder> {

    Context mContext;
    List<DailyExpense> dailyExpenseList;

    public DailyExpenseAdapter(Context mContext, List<DailyExpense> dailyExpenseList) {
        this.mContext = mContext;
        this.dailyExpenseList = dailyExpenseList;
    }

    @NonNull
    @Override
    public DailyExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.daily_expense_layout_item, parent, false);
        DailyExpenseViewHolder dailyExpenseViewHolder = new DailyExpenseViewHolder(view);
        return dailyExpenseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DailyExpenseViewHolder holder, int position) {
        DailyExpense dailyExpense = dailyExpenseList.get(position);
        holder.text.setText(dailyExpense.getText());
        holder.amount.setText(dailyExpense.getAmount());

    }

    @Override
    public int getItemCount() {
        return dailyExpenseList.size();
    }

    public class DailyExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView amount,text;
        public DailyExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            amount =itemView.findViewById(R.id.textView_amount);
            text = itemView.findViewById(R.id.textView_text);
        }
    }
}
