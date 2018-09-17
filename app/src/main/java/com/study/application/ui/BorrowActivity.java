package com.study.application.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import com.google.zxing.activity.CaptureActivity;
import com.study.application.R;
import com.study.application.leanCloud.ActivityID;
import com.study.application.leanCloud.BorrowCheckCallback;
import com.study.application.leanCloud.DisplayData;
import com.study.application.leanCloud.Reader;
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
import java.util.Calendar;

public class BorrowActivity extends AppCompatActivity implements BorrowCheckCallback {

    private final String TAG = "BorrowActivity";

    private String status;
    public static final int NO_SUBSCRIBER = 10001, NOT_SAME_AS_SUBSCRIBER = 10002, SAME_AS_SUBSCRIBER = 10003;

    private boolean isItemReturn = false;

    private EditText borrowDateEdt;
    private EditText returnDateEdt;
    private EditText nameEdt;
    private EditText itemEdt;
    private Button submitBtn;

    private Reader reader;
    private final StatusBroadcast statusBroadcast = new StatusBroadcast();
    private final Writer writer = new Writer();
    public static final int REQUEST_CODE = 50;


    private final Date date = new Date();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
    private Calendar calendar;

    ArrayList<DisplayData> conditionDataArrayList;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_borrow);

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
        borrowDateEdt = findViewById(R.id.borrowdateEdt);
        returnDateEdt = findViewById(R.id.returndateEdt);
        nameEdt = findViewById(R.id.nameEdt);
        itemEdt = findViewById(R.id.itemEdt);
        submitBtn = findViewById(R.id.submitBtn);
    }

    private void initSetup(){
        reader = new Reader(this, ActivityID.BORROW_ACTIVITY);
        status = getString(R.string.status_borrow);
        StatusDefinition.CURRENT_STATUS = StatusDefinition.BORROW_RETURN_SEARCH;
        calendar = Calendar.getInstance();
    }

    private void setListeners() {
        itemEdt.setOnLongClickListener(v -> {
                startQrCode();
                return false;
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

    private void submitFunction(){
        if (isNetworkConnected()){
            if (itemEdt.getText().toString().equals("")) {
                Toast.makeText(BorrowActivity.this, getString(R.string.dialog_message_no_data), Toast.LENGTH_LONG).show();
                SpeechSynthesis.textToSpeech.speak(getString(R.string.dialog_message_no_data),TextToSpeech.QUEUE_FLUSH, null );
            } else {
                if (isInputItemCorrect(itemEdt.getText().toString()))
                    reader.itemStatusCheck(itemEdt.getText().toString(), ActivityID.BORROW_ACTIVITY);
                else{
                    Toast.makeText(this, getString(R.string.dialog_message_scan_result_error), Toast.LENGTH_LONG).show();
                    itemEdt.setText("");
                }
            }
        }else
            new AlertDialog.Builder(this).setTitle(getString(R.string.dialog_title_error)).
                    setMessage(getString(R.string.dialog_message_network_error)).
                    setPositiveButton(getString(R.string.dialog_button_check), (DialogInterface dialog, int which)-> {
                    }).setIcon(R.drawable.error).show();
    }

    private boolean isNetworkConnected(){
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable();
    }

    private void setEditText() {
        // borrow_date
        borrowDateEdt.setText(dateFormat.format(date.getTime()));
        
        // return_date
        returnDateEdt.setText(dateFormat.format(date.getTime()));
        returnDateEdt = findViewById(R.id. returndateEdt);


        // name
        nameEdt.setText(WelcomeActivity.userName);
    }

    public void DatePicker(View v) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (DatePicker view, int selectYear, int selectMonth, int selectDay) -> {

        /* Modified it by Rex
            String dateTime = String.valueOf(selectYear) + "/" + String.valueOf(selectMonth + 1) + "/" + String.valueOf(selectDay);
            returnDateEdt.setText(dateTime);
        */

        /* Added below codes by Rex for testing */
            calendar.set(selectYear, selectMonth , selectDay);
            Log.d("TAG", calendar.getTime().getTime() + "");
            returnDateEdt.setText(dateFormat.format(calendar.getTime().getTime()));
        /* End */

        }, year, month, day);
        /* Modified it by Rex
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        */
        datePickerDialog.getDatePicker().setMinDate(date.getTime());
        datePickerDialog.show();

    }

    private boolean isInputItemCorrect(String item){

        boolean isInputItemCorrect = false;
        for (int i = 0; i < Reader.objectIdDataArrayList.size(); i++){
            if (item.equals(Reader.objectIdDataArrayList.get(i).getItem())){
                isInputItemCorrect = true;
                break;
            }else
                isInputItemCorrect = false;
        }

        return isInputItemCorrect;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK && data != null){
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);


            if (isInputItemCorrect(scanResult))
                itemEdt.setText(scanResult);
            else
                Toast.makeText(this, getString(R.string.dialog_message_scan_result_error), Toast.LENGTH_LONG).show();
        }
    }

    private void voiceToBorrowOrSubmit(String inputClassification){

        switch (inputClassification){
            case Classification.SCAN:

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
    public void isItemReturn(String result) {
        Log.e("TAG","In isItemReturn");
        if (result.equals(getString(R.string.status_return)))
            isItemReturn = true;
        else
            isItemReturn =false;

        reader.subscriberCheck(itemEdt.getText().toString(), nameEdt.getText().toString());
    }

    @Override
    public void isSubscriberSameAsUser(int result) {
        String itemName = itemEdt.getText().toString();
        if (isItemReturn && result == NOT_SAME_AS_SUBSCRIBER){
            Toast.makeText(BorrowActivity.this, itemEdt.getText().toString() + getString(R.string.dialog_message_not_subscriber), Toast.LENGTH_LONG).show();
        }else if(isItemReturn && (result == SAME_AS_SUBSCRIBER || result == NO_SUBSCRIBER)) {
            Log.w("TAG", "The date of calendar:" + dateFormat.format(calendar.getTime().getTime()));
            writer.writeBorrowDataToDatabase(
                    borrowDateEdt.getText().toString(),
                    returnDateEdt.getText().toString(),
                    nameEdt.getText().toString(),
                    itemEdt.getText().toString(),
                    status,
                    date.getTime(),
                    calendar.getTime().getTime(),
                    result
            );
            Log.v("TAG", "" + calendar.getTime().getTime() + "  " + dateFormat.format(calendar.getTime().getTime()));
            Toast.makeText(BorrowActivity.this, getString(R.string.dialog_message_borrow_success), Toast.LENGTH_LONG).show();
//            SpeechSynthesis.textToSpeech.speak(getString(R.string.dialog_message_borrow_success), TextToSpeech.QUEUE_FLUSH, null);
            borrowDateEdt.setText(dateFormat.format(date.getTime()));
        }else if (!isItemReturn && result == NO_SUBSCRIBER){
            new AlertDialog.Builder(BorrowActivity.this).
                    setTitle(R.string.dialog_title_inform).
                    setMessage(itemName + getString(R.string.dialog_message_turn_to_subscribe)).
                    setPositiveButton(R.string.dialog_button_cancel, (DialogInterface dialog, int which) -> {
                    }).setNegativeButton(R.string.dialog_button_check, (DialogInterface dialog, int which) -> {
                        Intent intent = new Intent(BorrowActivity.this, SubscribeActivity.class);
                        intent.putExtra("ITEM", itemName);
                        startActivity(intent);
                        finish();
                    }).setIcon(R.drawable.inform).show();
        }else {
            Toast.makeText(BorrowActivity.this,
                    itemName + getString(R.string.dialog_message_borrow_fail),
                    Toast.LENGTH_LONG).show();
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
