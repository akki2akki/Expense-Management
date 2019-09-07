package com.moneybhai.expense_manager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moneybhai.expense_manager.R;
import com.moneybhai.expense_manager.activity.DailyExpenseActivity;
import com.moneybhai.expense_manager.model.DailyExpense;
import com.moneybhai.expense_manager.model.Date;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    Context mContext;
    List<Date> dateList;

    public DateAdapter(Context mContext, List<Date> dateList) {
        this.mContext = mContext;
        this.dateList = dateList;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.date_layout_item, parent, false);
        DateViewHolder dateViewHolder = new DateViewHolder(view);
        return dateViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {

        final Date date = dateList.get(position);
        holder.textView.setText(date.getDate());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DailyExpenseActivity.class);
                intent.putExtra("DATE",date.getDate());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {

        CardView card;
        TextView textView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.datecardItem);
            textView = itemView.findViewById(R.id.date_text_view);
        }
    }
}
