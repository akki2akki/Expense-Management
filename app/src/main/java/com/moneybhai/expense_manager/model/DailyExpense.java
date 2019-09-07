package com.moneybhai.expense_manager.model;

public class DailyExpense {
    String Text,Amount;

    public DailyExpense(){

    }

    public DailyExpense(String text, String amount) {
        Text = text;
        Amount = amount;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }
}
