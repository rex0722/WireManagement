package com.study.application.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import com.google.zxing.activity.CaptureActivity;
import com.study.application.R;
import com.study.application.leanCloud.ActivityID;
import com.study.application.leanCloud.DisplayData;
import com.study.application.leanCloud.Reader;
import com.study.application.leanCloud.SubscribeCallback;
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

    public class SubscribeActivity extends AppCompatActivity implements SubscribeCallback{

    private final String TAG = "SubscribeActivity";
    private String status, dialogSuccessMessage, fromActivity, itemName;

    private EditText dateEdt;
    private EditText subDateEdt;
    private EditText nameEdt;
    private EditText itemEdt;
    private Button submitBtn;
    private Spinner spnSubscribeItem;
    private String data1,data2;
    private boolean isFromBorrowActivity;

    private ArrayList<DisplayData> conditionDataArrayList;
    private Calendar borCalendar, retCalendar;
    private final StatusBroadcast statusBroadcast = new StatusBroadcast();
    private final Reader reader = new Reader(this, ActivityID.SUBSCRIBE_ACTIVITY);
    private final Writer writer = new Writer();
    public static final int REQUEST_CODE = 60;

    private final Date date = new Date();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_subscribe);

        initView();
        initSetup();
        setListeners();
        setEditText();
        broadcastRegister();
    }

    private void initView() {
        dateEdt = findViewById(R.id.dateEdt);
        subDateEdt = findViewById(R.id.subdateEdt);
        nameEdt = findViewById(R.id.nameEdt);
        itemEdt = findViewById(R.id.itemEdt);
        submitBtn = findViewById(R.id.submitBtn);
        spnSubscribeItem = findViewById(R.id.spnSubscribeItem);
    }

    private void initSetup(){

        if (StatusDefinition.CURRENT_STATUS.equals(StatusDefinition.BORROW_RETURN_SEARCH)){
            Intent intent = getIntent();
            String item = intent.getExtras().getString("ITEM");

            isFromBorrowActivity = true;
            spnSubscribeItem.setVisibility(View.GONE);
            itemEdt.setVisibility(View.VISIBLE);
            itemEdt.setText(item);
            fromActivity = StatusDefinition.BORROW_RETURN_SEARCH;
        }else {
            isFromBorrowActivity = false;
            spnSubscribeItem.setVisibility(View.VISIBLE);
            itemEdt.setVisibility(View.GONE);
            fromActivity = StatusDefinition.FUNCTION_SELECT;
        }

        reader.checkSubscribeItem();
        borCalendar = Calendar.getInstance();
        retCalendar = Calendar.getInstance();
        StatusDefinition.CURRENT_STATUS = StatusDefinition.SUBSCRIBE_SEARCH;
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

        dateEdt.setOnClickListener(view -> {
            DatePicker(dateEdt, 1);
        });

        subDateEdt.setOnClickListener(v -> {
            DatePicker(subDateEdt, 2);
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

            bundle.putString("TARGET", "SUBSCRIBEITEM");
            intent.putExtras(bundle);
            intent.setClass(this, ScanQrCodeActivity.class);

            startActivityForResult(intent, REQUEST_CODE);
        }

    private void submitFunction(){
        if (fromActivity.equals(StatusDefinition.BORROW_RETURN_SEARCH) && itemEdt.getText().toString().equals("")) {
            Toast.makeText(SubscribeActivity.this, getString(R.string.dialog_message_no_data), Toast.LENGTH_LONG).show();
            SpeechSynthesis.textToSpeech.speak(getString(R.string.dialog_message_no_data),TextToSpeech.QUEUE_FLUSH, null );
        } else {

            if (checkReturnDateLaterThanBorrowDate(borCalendar.getTime().getTime(), retCalendar.getTime().getTime())){
                if (fromActivity.equals(StatusDefinition.FUNCTION_SELECT))
                    itemName = spnSubscribeItem.getSelectedItem().toString();
                else
                    itemName = itemEdt.getText().toString();

                reader.checkSubscribeDate(itemName);
            }else
                Toast.makeText(SubscribeActivity.this, getString(R.string.dialog_message_return_date_earlier_than_borrow_date), Toast.LENGTH_LONG).show();
        }
    }

    private void setEditText() {
        // date
        dateEdt.setText(dateFormat.format(date.getTime()));

        //subdate  waiting for API
        subDateEdt.setText(dateFormat.format(date.getTime()));
        subDateEdt = (EditText)findViewById(R.id. subdateEdt);

        // name
        nameEdt.setText(WelcomeActivity.userName);

        status = getString(R.string.status_subscribe);
        dialogSuccessMessage = getString(R.string.dialog_message_subscribe_success);
    }

    public void DatePicker(View v, int viewId) {
        int year = borCalendar.get(Calendar.YEAR);
        int month = borCalendar.get(Calendar.MONTH);
        int day = borCalendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (DatePicker view, int selectYear, int selectMonth, int selectDay) -> {
        /* Modified it by Rex
            String dateTime = String.valueOf(selectYear) + "/" + String.valueOf(selectMonth + 1) + "/" + String.valueOf(selectDay);
            returnDateEdt.setText(dateTime);
        */

        /* Added below codes by Rex for testing */
        switch (viewId){
            case 1:
                borCalendar.set(selectYear, selectMonth, selectDay);
                Log.d("TAG", borCalendar.getTime().getTime() + "");
                dateEdt.setText(dateFormat.format(borCalendar.getTime().getTime()));
                break;
            case 2:
                retCalendar.set(selectYear, selectMonth, selectDay);
                Log.d("TAG", retCalendar.getTime().getTime() + "");
                subDateEdt.setText(dateFormat.format(retCalendar.getTime().getTime()));
                break;
        }
        /* End */
        }, year, month, day);

        /* Modified it by Rex
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        */
        datePickerDialog.getDatePicker().setMinDate(date.getTime());
        datePickerDialog.show();

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
        registerReceiver(statusBroadcast, new IntentFilter(StatusDefinition.SUBSCRIBE_SEARCH));
    }

    @Override
    public void canSubscribeItem(String[] itemList) {
        Log.w("TAG", "size:" + itemList.length);

        if(itemList.length == 0){
            new AlertDialog.Builder(this).setTitle(R.string.dialog_title_inform).
                        setMessage(R.string.dialog_message_nothing_can_subscribe).
                        setPositiveButton(R.string.dialog_button_check, (DialogInterface dialog, int which) -> {
                            finish();
                        }).show();
        }else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_setting, itemList);
            spnSubscribeItem.setAdapter(adapter);
        }

    }

    @Override
    public void checkItemEstimatedTimeReturn(Long estimatedTimeReturnNum) {
        if (estimatedTimeReturnNum < borCalendar.getTime().getTime()){
            Log.w("TAG", "borCalendar:" + dateFormat.format(borCalendar.getTime().getTime()) + "\n" +
                    "retCalendar:" + dateFormat.format(retCalendar.getTime().getTime()));
            writer.writeSubscriptionDataToDatabase(
                    dateEdt.getText().toString(),
                    subDateEdt.getText().toString(),
                    nameEdt.getText().toString(),
                    itemName,
                    borCalendar.getTime().getTime(),
                    retCalendar.getTime().getTime()
            );

            Toast.makeText(SubscribeActivity.this, dialogSuccessMessage, Toast.LENGTH_LONG).show();

            SpeechSynthesis.textToSpeech.speak(dialogSuccessMessage, TextToSpeech.QUEUE_FLUSH, null);
//            dateEdt.setText(dateFormat.format(date.getTime()));
//            subDateEdt.setText(dateFormat.format(date.getTime()));//waiting for API
            itemEdt.setText("");
            reader.checkSubscribeItem();

            if (isFromBorrowActivity)
                finish();
        }else {
            String message = itemName + getString(R.string.dialog_message_subscribe_date_error1) +
                            dateFormat.format(estimatedTimeReturnNum) +
                            getString(R.string.dialog_message_subscribe_date_error2);

            Toast.makeText(SubscribeActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkReturnDateLaterThanBorrowDate(Long borrowDate, Long returnDate){
        return borrowDate <= returnDate;
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
                    case StatusDefinition.SUBSCRIBE_SEARCH:
                        voiceToBorrowOrSubmit(intent.getStringExtra(StatusDefinition.SUBSCRIBE_SEARCH));
                        break;
                }
            }
        }
    }

}
