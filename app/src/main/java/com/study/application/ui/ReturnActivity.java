package com.study.application.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.activity.CaptureActivity;
import com.study.application.R;

import com.study.application.leanCloud.ActivityID;
import com.study.application.leanCloud.DisplayData;
import com.study.application.leanCloud.Reader;
import com.study.application.leanCloud.ReturnCheckCallback;
import com.study.application.leanCloud.Writer;
import com.study.application.scanner.ScanQrCodeActivity;
import com.study.application.speech.Classification;
import com.study.application.speech.SpeechSynthesis;
import com.study.application.speech.StatusDefinition;
import com.study.application.util.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReturnActivity extends AppCompatActivity implements ReturnCheckCallback{

    private final String TAG = "ReturnActivity";

    private String status, conditionSearchValue, dialogSuccessMessage;
    private boolean isReturnerSameAsBorrow = false;

    private TextInputLayout itemInputLayout;
    private EditText dateEdt;
    private EditText nameEdt;
    private EditText itemEdt;
    private Button submitBtn;

    private Reader reader;
    private ArrayList<DisplayData> conditionDataArrayList;
    private final StatusBroadcast statusBroadcast = new StatusBroadcast();
    private final Writer writer = new Writer();
    public static final int REQUEST_CODE = 50;


    private final Date date = new Date();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_return);

        StatusDefinition.CURRENT_STATUS = StatusDefinition.BORROW_RETURN_SEARCH;
        initView();
        initSetup();
        setListeners();
        setEditText();
        broadcastRegister();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusDefinition.CURRENT_STATUS = StatusDefinition.BORROW_RETURN_SEARCH;
    }

    private void initView() {
        itemInputLayout = findViewById(R.id.itemInputLayout);
        dateEdt = findViewById(R.id.dateEdt);
        nameEdt = findViewById(R.id.nameEdt);
        itemEdt = findViewById(R.id.itemEdt);
        submitBtn = findViewById(R.id.submitBtn);
    }

    private void initSetup(){
        status = getString(R.string.status_return);
        reader = new Reader(this, ActivityID.RETURN_ACTIVITY);
    }

    private void setListeners() {
        itemEdt.setOnClickListener(view -> {
            /* Disable it temporarily */
//            scanQrCodeActivityStartUp();
            startQrCode();
        });

        submitBtn.setOnClickListener(v -> {
                submitFunction();
        });
    }
    // 开始扫码 Owen add
    private void startQrCode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Constant.REQ_PERM_CAMERA);
            return;
        }
        // 二维码扫码
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, Constant.REQ_QR_CODE);
    }
    private void scanQrCodeActivityStartUp(){
        Bundle bundle = new Bundle();
        Intent intent = new Intent();

        bundle.putString("TARGET", "ITEM");
        intent.putExtras(bundle);
        intent.setClass(this, ScanQrCodeActivity.class);

        startActivityForResult(intent, REQUEST_CODE);
    }

    private void submitFunction(){
        if (itemEdt.getText().toString().equals("")) {
            Toast.makeText(ReturnActivity.this, getString(R.string.dialog_message_no_data), Toast.LENGTH_LONG).show();
            SpeechSynthesis.textToSpeech.speak(getString(R.string.dialog_message_no_data),TextToSpeech.QUEUE_FLUSH, null );
        } else{
            reader.itemStatusCheck(itemEdt.getText().toString(), ActivityID.RETURN_ACTIVITY);
        }
    }


    private void setEditText() {
        // date
        dateEdt.setText(dateFormat.format(date.getTime()));

        // name
        nameEdt.setText(WelcomeActivity.userName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK && data != null){
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            boolean isScanResultCorrect = false;
            for (int i = 0; i < Reader.objectIdDataArrayList.size(); i++){
                if (scanResult.equals(Reader.objectIdDataArrayList.get(i).getItem())){
                    isScanResultCorrect = true;
                    break;
                }else
                    isScanResultCorrect = false;
            }

            if (isScanResultCorrect)
                itemEdt.setText(scanResult);
            else
                Toast.makeText(this, getString(R.string.dialog_message_scan_result_error), Toast.LENGTH_LONG).show();
        }
    }

    private void voiceToBorrowOrSubmit(String inputClassification){

        switch (inputClassification){
            case Classification.SCAN:
                scanQrCodeActivityStartUp();
                break;
            case Classification.SUBMIT:
                submitFunction();
                break;
            case Classification.GETBACK:
                finish();
                break;
        }

    }

    private void broadcastRegister(){
        registerReceiver(statusBroadcast, new IntentFilter("DelverConditionData"));
        registerReceiver(statusBroadcast, new IntentFilter(StatusDefinition.BORROW_RETURN_SEARCH));
    }

    @Override
    public void isItemCanReturn(String  result) {
        if (result.equals(getString(R.string.status_borrow)))
            reader.checkReturnerSameAsBorrower(itemEdt.getText().toString(), nameEdt.getText().toString());
        else{
            Toast.makeText(ReturnActivity.this,
                    itemEdt.getText().toString() + getString(R.string.dialog_message_return_error),
                    Toast.LENGTH_LONG).show();
            itemEdt.setText("");
        }

    }

    @Override
    public void isReturnerSameAsBorrower(boolean result) {
        if (!result) {
            Toast.makeText(ReturnActivity.this,
                    itemEdt.getText().toString() + getString(R.string.dialog_message_return_user_error),
                    Toast.LENGTH_LONG).show();
        }else{
            writer.writeReturnDataToDatabase(
                    dateEdt.getText().toString(),
                    nameEdt.getText().toString(),
                    itemEdt.getText().toString(),
                    status,
                    date.getTime()
            );

            Toast.makeText(ReturnActivity.this, getString(R.string.dialog_message_return_success), Toast.LENGTH_LONG).show();
//            SpeechSynthesis.textToSpeech.speak(getString(R.string.dialog_message_return_success), TextToSpeech.QUEUE_FLUSH, null);
            dateEdt.setText(dateFormat.format(date.getTime()));
        }
        itemEdt.setText("");
    }

    private class StatusBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case "DelverConditionData":
                        conditionDataArrayList = (ArrayList<DisplayData>) intent.getSerializableExtra("conditionData");
                        for (int i = 0; i < conditionDataArrayList.size(); i++)
                            Log.i("TAG", "Item:" + conditionDataArrayList.get(i).getIndex());
                        break;
                    case StatusDefinition.BORROW_RETURN_SEARCH:
                        voiceToBorrowOrSubmit(intent.getStringExtra(StatusDefinition.BORROW_RETURN_SEARCH));
                        break;
                }
            }
        }
    }

}
