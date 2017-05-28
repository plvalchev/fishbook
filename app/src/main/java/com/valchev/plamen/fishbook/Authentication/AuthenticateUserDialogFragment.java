package com.valchev.plamen.fishbook.authentication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.valchev.plamen.fishbook.R;


public class AuthenticateUserDialogFragment extends DialogFragment {

    public interface UserAuthenticator {

        void authenticateUser(final String email, final String password);
    }

    protected EditText mEmail;
    protected EditText mPassword;
    protected ProgressDialog mProgressDialog;
    protected FragmentManager mFragmentManager;
    protected UserAuthenticator mUserAuthenticator;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = createDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                final AlertDialog alertDialog = (AlertDialog) dialog;

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if( !validateInputFields() )
                            return;

                        dismiss();

                        String email = mEmail.getText().toString();
                        String password = mPassword.getText().toString();

                        mUserAuthenticator.authenticateUser(email, password);
                    }
                });
            }
        });

        return dialog;
    }

    protected Dialog createDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_authenticate, null);

        mEmail = (EditText) view.findViewById(R.id.email);
        mPassword = (EditText) view.findViewById(R.id.password);

        builder.setView(view);

        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                AuthenticateUserDialogFragment.this.getDialog().cancel();
            }
        });

        Bundle arguments = getArguments();

        String title = arguments.getString("title");
        String message = arguments.getString("message");
        String email = arguments.getString("email");
        String password = arguments.getString("password");

        mEmail.setText(email);
        mPassword.setText(password);

        builder.setTitle(title);
        builder.setPositiveButton(title, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        if( message != null && !message.isEmpty() ) {

            Spanned htmlMessage;
            String html = "<font color='#FF0000'>" + message + "</font>";

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                htmlMessage = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
            }
            else {

                htmlMessage = Html.fromHtml(html);
            }

            builder.setMessage(htmlMessage);
        }

        AlertDialog dialog = builder.create();

        return dialog;
    }

    protected boolean validateInputFields() {

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

    protected boolean isEmailValid(String email) {

        return true;
    }

    protected boolean isPasswordValid(String password) {

        return true;
    }

    public void show(String title, String message, String email, String password, FragmentManager fragmentManager, UserAuthenticator userAuthenticator) {

        mFragmentManager = fragmentManager;
        mUserAuthenticator = userAuthenticator;

        Bundle arguments = new Bundle();
        arguments.putString("title", title);
        arguments.putString("message", message);
        arguments.putString("email", email);
        arguments.putString("password", password);

        setArguments(arguments);
        show(fragmentManager, getClass().getName());
    }

    public void show(String title, FragmentManager fragmentManager, UserAuthenticator userAuthenticator) {

        show(title, null, null, null, fragmentManager, userAuthenticator);
    }
}
