package com.evento.akay18.evento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ResetActivity extends AppCompatActivity {

    private EditText resetEmail;
    private TextInputLayout resetMailIL;
    private Button resetBtn;
    private String email;
    View focusView = null;
    private ProgressDialog mProgress;
    boolean notEmpty = true;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/circular_std_book.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_reset);

        resetEmail = findViewById(R.id.resetEmailField);
        resetMailIL = findViewById(R.id.resetEmailInputLayout);
        resetBtn = findViewById(R.id.resetBtn);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Sending Reset Link to your Email");
        mAuth = FirebaseAuth.getInstance();


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = resetEmail.getText().toString();
                checkEmail();
                if (notEmpty) {
                    mProgress.show();
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                resetEmail.getText().clear();
                                mProgress.dismiss();
                                Toast.makeText(ResetActivity.this, "Password Reset Link Sent, Check Your Mail", Toast.LENGTH_LONG).show();
                            } else {
                                mProgress.dismiss();
                                Toast.makeText(ResetActivity.this, "Something Went Wrong, Check Email", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    //Email validation
    protected void checkEmail() {
        if (TextUtils.isEmpty(email)) {
            resetMailIL.setError("Email Required");
            focusView = resetEmail;
            notEmpty = false;
        } else {
            notEmpty = true;
            resetMailIL.setError(null);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
