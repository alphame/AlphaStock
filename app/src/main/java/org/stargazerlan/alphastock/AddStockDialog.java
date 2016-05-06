package org.stargazerlan.alphastock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by StargazerLan on 2016/5/1.
 */
public class AddStockDialog extends DialogFragment {
    private EditText editText;
    private DialogListener dialogListener;

    public interface DialogListener {
        public void onPositiveButtonClicked(AddStockDialog dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dialogListener = (DialogListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_stock_dialog, null);
        editText = (EditText)view.findViewById(R.id.addStockEditText);

        builder.setView(view);
        builder.setTitle("Input the stock code");
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogListener.onPositiveButtonClicked(AddStockDialog.this);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }

    public String getStockCode() {
        return editText.getText().toString();
    }
}
