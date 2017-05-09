package com.kmbapps.classscheduler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

public class SortByDialogFragment extends DialogFragment {

    private SortByDialogListener mListener;

    static SortByDialogFragment newInstance(int itemSelected) {
        SortByDialogFragment f = new SortByDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("itemSelected", itemSelected);
        f.setArguments(args);

        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use the Builder class for convenient dialog construction
        //View myView = View.inflate(getActivity().getApplicationContext(), R.layout.dialog_sort_by_layout, null);

        int itemSelected = getArguments().getInt("itemSelected");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_sort_by)
                .setSingleChoiceItems(R.array.sort_by_dialog_array, itemSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onSortingKeySelected(which);
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = null;
        if (context instanceof Activity){
            activity = (Activity) context;
        }
        // Verify that the host activity implements the callback interface
        if (activity != null) {
            try {
                // Instantiate the NoticeDialogListener so we can send events to the host
                mListener = (SortByDialogListener) activity;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(activity.toString()
                        + " must implement NoticeDialogListener");
            }
        }
    }

    private void onSortingKeySelected(int which){
        if(mListener!=null){
            mListener.onAssignmentsSortingKeySelected(which);
        }
    }

    public interface SortByDialogListener{
        void onAssignmentsSortingKeySelected(int which);
    }
}
