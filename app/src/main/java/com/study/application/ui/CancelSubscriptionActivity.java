package com.study.application.ui;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.study.application.R;
import com.study.application.leanCloud.ActivityID;
import com.study.application.leanCloud.CancelSubscriptionCallback;
import com.study.application.leanCloud.Reader;

public class CancelSubscriptionActivity extends AppCompatActivity implements CancelSubscriptionCallback{

    private Spinner spnSubscribeItem;
    private EditText edtName;
    private Button btnSubmit;
    private Reader reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_subscription);

        initView();
        initSetup();
    }

    private void initView(){
        spnSubscribeItem = findViewById(R.id.spnSubscribeItem);
        edtName = findViewById(R.id.nameEdt);
        btnSubmit = findViewById(R.id.submitBtn);
    }

    private void initSetup(){
        reader =  new Reader(this, ActivityID.CANCEL_SUBSCRIBE_ACTIVITY);
        edtName.setText(WelcomeActivity.userName);
        reader.checkSubscriptionItemCanCancel(WelcomeActivity.userName);
    }

    private void setListener(){
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void checkSubscriptionItemCanCancel(String[] list) {
        if(list.length == 0){
            new AlertDialog.Builder(this).
                    setTitle(getString(R.string.dialog_title_inform)).
                    setMessage(getString(R.string.dialog_message_nothing_can_cancel_subscription)).
                    setPositiveButton(getString(R.string.dialog_button_check),(DialogInterface dialog, int which) -> {
                        finish();
                    }).show();
        }else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_setting, list);
            spnSubscribeItem.setAdapter(adapter);
        }

    }
}
