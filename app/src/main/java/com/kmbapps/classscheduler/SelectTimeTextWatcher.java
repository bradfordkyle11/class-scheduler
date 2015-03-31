package com.kmbapps.classscheduler;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * Created by Kyle on 2/27/2015.
 */
public class SelectTimeTextWatcher implements TextWatcher {

    private View view;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    SelectTimeTextWatcher(View view){
        this.view = view;
    }
    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start,
                                  int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start,
                              int before, int count) {
        System.out.println("text changed");

    }
}
