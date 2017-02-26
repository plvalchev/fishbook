package com.valchev.plamen.fishbook.Home;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.valchev.plamen.fishbook.Global.FishbookApplication;
import com.valchev.plamen.fishbook.R;


public class AuthenticateUserDialogFragment extends DialogFragment {

    String mTitle;
    protected EditText mEmail;
    protected EditText mPassword;

    public void setTitle( String title ) {

        mTitle = title;
    }

    public void setTitle( int titleId ) {

        mTitle = FishbookApplication.getContext().getResources().getString(titleId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog dialog = createDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                AlertDialog alertDialog = (AlertDialog) dialog;

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if( !validateInputFields() )
                            return;

                        // TODO: Sign In / Sign Up
                        dismiss();
                    }
                });
            }
        });

        return dialog;
    }

    private AlertDialog createDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_authenticate, null);

        mEmail = (EditText) view.findViewById(R.id.email);
        mPassword = (EditText) view.findViewById(R.id.password);

        builder.setView(view);
        builder.setTitle(mTitle);
        builder.setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AuthenticateUserDialogFragment.this.getDialog().cancel();
            }
        });

        AlertDialog dialog = builder.create();

        return dialog;
    }

    private boolean validateInputFields() {

        mEmail.setError(null);
        mPassword.setError(null);

        View focusView = null;
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        boolean isValid = true;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            isValid = false;

        } else if (!isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            isValid = false;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            isValid = false;

        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            isValid = false;
        }

        if( focusView != null )
            focusView.requestFocus();

        return isValid;
    }

    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 4;
    }

}
