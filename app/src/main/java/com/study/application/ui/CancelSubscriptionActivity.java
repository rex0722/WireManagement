package com.study.application.ui;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.study.application.R;
import com.study.application.leanCloud.ActivityID;
import com.study.application.leanCloud.CancelSubscriptionCallback;
import com.study.application.leanCloud.Reader;
import com.study.application.leanCloud.Writer;

public class CancelSubscriptionActivity extends AppCompatActivity implements CancelSubscriptionCallback{

    private Spinner spnSubscribeItem;
    private EditText edtName;
    private Button btnSubmit;
    private Reader reader;
    private Writer writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cancel_subscription);

        initView();
        initSetup();
        setListener();
    }

    private void initView(){
        spnSubscribeItem = findViewById(R.id.spnSubscribeItem);
        edtName = findViewById(R.id.nameEdt);
        btnSubmit = findViewById(R.id.submitBtn);
    }

    private void initSetup(){
        reader =  new Reader(this, ActivityID.CANCEL_SUBSCRIBE_ACTIVITY);
        writer = new Writer();
        edtName.setText(WelcomeActivity.userName);
        reader.checkSubscriptionItemCanCancel(WelcomeActivity.userName);
    }

    private void setListener(){
        btnSubmit.setOnClickListener(v -> {
            writer.writeCancelSubscriptionDataToDatabase(spnSubscribeItem.getSelectedItem().toString());
            Toast.makeText(this, getString(R.string.dialog_message_cancel_success),Toast.LENGTH_LONG).show();
            reader.checkSubscriptionItemCanCancel(WelcomeActivity.userName);
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
                    }).setIcon(R.drawable.inform).show();
        }else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_setting, list);
            spnSubscribeItem.setAdapter(adapter);
        }

    }
}
