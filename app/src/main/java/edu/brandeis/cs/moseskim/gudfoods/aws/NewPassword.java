/*
 *  Copyright 2013-2016 Amazon.com,
 *  Inc. or its affiliates. All Rights Reserved.
 *
 *  Licensed under the Amazon Software License (the "License").
 *  You may not use this file except in compliance with the
 *  License. A copy of the License is located at
 *
 *      http://aws.amazon.com/asl/
 *
 *  or in the "license" file accompanying this file. This file is
 *  distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 *  CONDITIONS OF ANY KIND, express or implied. See the License
 *  for the specific language governing permissions and
 *  limitations under the License.
 */

package edu.brandeis.cs.moseskim.gudfoods.aws;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.brandeis.cs.moseskim.gudfoods.R;

public class NewPassword extends AppCompatActivity {
    private String TAG = "NewPassword";
    private EditText newPassword;

    private Button continueSignIn;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();
    }

    @Override
    public void onBackPressed() {
        exit(false);
    }

    private void init() {
        newPassword = (EditText) findViewById(R.id.editTextNewPassPass);
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewNewPassPassLabel);
                    label.setText(newPassword.getHint());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewNewPassPassMessage);
                label.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewNewPassPassLabel);
                    label.setText("");
                }
            }
        });

        continueSignIn = (Button) findViewById(R.id.buttonNewPass);
        continueSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUserPassword = newPassword.getText().toString();
                if (newUserPassword != null) {
                    AWSService.setPasswordForFirstTimeLogin(newUserPassword);
                    if (checkAttributes()) {
                        exit(true);
                    }
                }
                showDialogMessage("Error", "Enter all required attributed", false);
            }
        });
    }

    private boolean checkAttributes() {
        // Check if all required attributes have values
        return true;
    }

    private void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if (exit) {
                        exit(false);
                    }
                } catch (Exception e) {
                    exit(false);
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void exit(Boolean continueWithSignIn) {
        Intent intent = new Intent();
        intent.putExtra("continueSignIn", continueWithSignIn);
        setResult(RESULT_OK, intent);
        finish();
    }
}
