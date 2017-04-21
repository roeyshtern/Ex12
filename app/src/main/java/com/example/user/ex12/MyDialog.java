package com.example.user.ex12;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by User on 4/21/17.
 * this class create and take care of dialogs
 */

public class MyDialog extends DialogFragment {

    //variables
    private ResultsListener listener;

    //this function will create the dialog
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return buildExitDialog().create();
    }

    //this funcrion will attach the dialog with acticty that called her
    @Override
    public void onAttach(Activity activity) {
        try
        {
            if(getParentFragment()!=null)
            {
                this.listener = (ResultsListener)getParentFragment();
            }
            else
            {
                this.listener = (ResultsListener)activity;
            }
        }catch(ClassCastException e)
        {
            String str = getResources().getString(R.string.host);
            throw new ClassCastException(str);
        }
        super.onAttach(activity);
    }

    //this functino will detach from the activity that called her
    @Override
    public void onDetach() {
        this.listener = null;
        super.onDetach();
    }

    //this function will create a new dialog
    public static MyDialog newInstance() {
        Bundle args = new Bundle();
        MyDialog fragment = new MyDialog();
        return fragment;
    }

    //this function will create the exit dialog
    private AlertDialog.Builder buildExitDialog() {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.exit_dialog)
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.OnfinishDialog("ok");
                    }
                })
                .setNegativeButton(R.string.no_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
    }

    //this is the interface of the dialog class for make the class that use this dialog class to take care of function that sposed to call from him
    public interface ResultsListener {
        public void OnfinishDialog(Object result);
    }
}
