package team12.movietracker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class RegisterActivity extends AppCompatActivity {

    private EditText mFirstName = (EditText) findViewById(R.id.editTextFirstName);
    private EditText mLastName = (EditText) findViewById(R.id.editTextLastName);
    private EditText mUserName = (EditText) findViewById(R.id.editTextUsername);
    private EditText mPassword = (EditText) findViewById(R.id.editTextPassword);
    private EditText mPasswordConfirm = (EditText) findViewById(R.id.editTextPasswordConfirm);
    private View mProgressView = findViewById(R.id.register_form);
    private View mRegisterFormView = findViewById(R.id.progressBar);
    private UserRegisterTask mAuthTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });



    }

    private void attemptRegister()
    {
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String username = mUserName.getText().toString();
        String password = mPassword.getText().toString();
        String passwordConfirm = mPasswordConfirm.getText().toString();


        mFirstName.setError(null);
        mLastName.setError(null);
        mUserName.setError(null);
        mPassword.setError(null);
        mPasswordConfirm.setError(null);

        View focusView = null;
        boolean cancel = false;

        //Check firstName
        if ((TextUtils.isEmpty(firstName)))
        {
            mFirstName.setError(getString(R.string.error_field_required));
            focusView = mFirstName;
            cancel = true;
        }
        else if(!validName(firstName))
        {
            mFirstName.setError(getString(R.string.error_invalid_name));
            focusView = mFirstName;
            cancel = true;
        }

        //Check lastName
        if ((TextUtils.isEmpty(lastName)))
        {
            mLastName.setError(getString(R.string.error_field_required));
            focusView = mLastName;
            cancel = true;
        }
        else if(!validName(lastName))
        {
            mLastName.setError(getString(R.string.error_invalid_name));
            focusView = mLastName;
            cancel = true;
        }

        //Check Username
        if ((TextUtils.isEmpty(username)))
        {
            mUserName.setError(getString(R.string.error_field_required));
            focusView = mUserName;
            cancel = true;
        }
        else if(!validUsername(username))
        {
            mUserName.setError(getString(R.string.error_invalid_username));
            focusView = mUserName;
            cancel = true;
        }

        //Check Password
        if(TextUtils.isEmpty(password))
        {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        }
        else if(!validPassword(password))
        {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        //Check ConfirmPassword
        if(TextUtils.isEmpty(passwordConfirm))
        {
            mPasswordConfirm.setError(getString(R.string.error_field_required));
            focusView = mPasswordConfirm;
            cancel = true;
        }
        else if(!TextUtils.equals(password,passwordConfirm))
        {
            mPasswordConfirm.setError(getString(R.string.error_password_mismatch));
            focusView = mPasswordConfirm;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

        }
    }

    //TODO: Setup the Valid Checks
    private boolean validUsername(String username)
    {
        return false;
    }

    private boolean validName(String name)
    {
        return false;
    }

    private boolean validPassword(String password)
    {
        return false;
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mRegisterFormView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserRegisterTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }


            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {



            if (success) {
                Intent displayLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(displayLogin);
                showProgress(false);
            } else {
                showProgress(false);
                mUserName.setError(getString(R.string.error_inuse_username));
                mUserName.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {

            showProgress(false);
        }
    }
}