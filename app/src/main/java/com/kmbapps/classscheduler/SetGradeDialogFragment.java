package com.kmbapps.classscheduler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

public class SetGradeDialogFragment extends DialogFragment {

    private SetGradeDialogListener mListener;

    static SetGradeDialogFragment newInstance(String grade, Assignment assignment) {
        SetGradeDialogFragment f = new SetGradeDialogFragment();

        Bundle args = new Bundle();
        args.putString("currentGrade", grade);
        args.putSerializable("assignment", assignment);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String currentGrade = getArguments().getString("currentGrade");
        final Assignment assignment = (Assignment) getArguments().getSerializable("assignment");
        final View myView = View.inflate(getActivity().getApplicationContext(), R.layout.dialog_set_grade_layout, null);
        EditText grade = (EditText) myView.findViewById(R.id.grade);
        grade.setText(currentGrade);


        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(myView)
                .setTitle(R.string.dialog_set_grade)
                //set grade
                .setPositiveButton(R.string.dialog_action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText grade = (EditText) myView.findViewById(R.id.grade);
                        try {
                            // Instantiate the NoticeDialogListener so we can send events to the host
                            mListener = (SetGradeDialogListener) getTargetFragment();
                        } catch (ClassCastException e) {
                            // The activity doesn't implement the interface, throw exception
                            throw new ClassCastException("The target fragment must implement NoticeDialogListener");
                        }

                        if (mListener != null) {
                            mListener.onSetGradePositiveClick(grade.getText().toString(), assignment);
                        }

                        dialog.dismiss();
                    }
                })
                //cancel setting grade
                .setNegativeButton(R.string.dialog_action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            // Instantiate the NoticeDialogListener so we can send events to the host
                            mListener = (SetGradeDialogListener) getTargetFragment();
                        } catch (ClassCastException e) {
                            // The activity doesn't implement the interface, throw exception
                            throw new ClassCastException("The target fragment must implement NoticeDialogListener");
                        }
                        if (mListener != null) {
                            mListener.onSetGradeNegativeClick();
                        }
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface SetGradeDialogListener{
        void onSetGradePositiveClick(String grade, Assignment assignment);
        void onSetGradeNegativeClick();
    }
}
