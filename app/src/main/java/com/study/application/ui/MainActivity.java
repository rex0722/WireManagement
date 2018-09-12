package com.study.application.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;
import com.google.zxing.activity.CaptureActivity;
import com.study.application.R;
import com.study.application.leanCloud.Reader;
import com.study.application.leanCloud.SpeechDataReader;
import com.study.application.scanner.ScanQrCodeActivity;
import com.study.application.speech.Classification;
import com.study.application.speech.SpeechRecognition;
import com.study.application.speech.SpeechSynthesis;
import com.study.application.speech.StatusDefinition;
import com.study.application.util.Constant;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private static final int REQUEST_RECORD_PERMISSION = 100;
    public static Context mContext;
    private MainBroadcast mainBroadcast = new MainBroadcast();
    private SpeechDataReader speechDataReader = new SpeechDataReader();
    public static SpeechRecognizer speech = null;
    public static Intent recognizerIntent = new Intent();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Owen","MainActivity func");
        Intent intent = new Intent(this,AutoService.class);
        intent.setAction("android.intent.action.RESPOND_VIA_MESSAGE");
        MainActivity.this.startService(intent);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initView();
        broadcastRegister();
        SpeechSynthesis.createTTS();

        /* Connect to LeanCloud database. */
        AVOSCloud.initialize(this,"oOxUTjO1eEyTxGS32vWa4uhT-gzGzoHsz","oEruGz6TQaE8iLQESN06tBBK");

        /* Read ObjectId from database */
        Reader.setObjectID();

        /* Disable Speech Recognition and Speech Synthesis functions temporarily

        speechDataReader.loginDataLoad();
        recognizerIntent = new Intent(ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        SpeechRecognition speechRecognition = new SpeechRecognition();
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(speechRecognition);


        Disable Speech Recognition function temporarily
        ActivityCompat.requestPermissions
                (MainActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_PERMISSION);
       */
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        StatusDefinition.CURRENT_STATUS = StatusDefinition.LOGIN;
//        speech.startListening(recognizerIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        speech.stopListening();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.REQ_PERM_CAMERA:
                // 摄像头权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(MainActivity.this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void initView() {
        mContext = this;
        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(v -> {
            //scanQrCodeActivityStartUp();
            startQrCode();
        });
    }

    // 开始扫码
    private void startQrCode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.REQ_PERM_CAMERA);
            return;
        }
        // 二维码扫码
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, Constant.REQ_QR_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {

                Bundle bundle = data.getExtras();
                String scanCode = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
                Log.i("Owen","MainActivity-onActivityResult"+scanCode);
                Intent intent = new Intent();
                intent.putExtra("USER",scanCode);
                intent.setClass(this, WelcomeActivity.class);

            startActivity(intent);
        }
            else{

            Log.i("Owen","MainActivity-onActivityResult else end");

        }
    }
    private void scanQrCodeActivityStartUp(){
        Bundle bundle = new Bundle();
        Intent intent = new Intent();

        bundle.putString("TARGET", "USER");
        intent.putExtras(bundle);
        /* Modified it for testing */
        intent.setClass(this, WelcomeActivity.class);

        startActivity(intent);
    }

    public void voiceToLogin(String inputClassification){

        if (inputClassification.equals(Classification.LOGIN)){
            Log.v("TAG","QR code Scan START!!");
                StatusDefinition.CURRENT_STATUS = StatusDefinition.QR_CODE_SCAN;
                scanQrCodeActivityStartUp();
        }
    }

    private void broadcastRegister(){
        registerReceiver(mainBroadcast, new IntentFilter("LoginData"));
        registerReceiver(mainBroadcast, new IntentFilter(StatusDefinition.LOGIN));
    }


    private class MainBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null){
                switch (intent.getAction()){
                    case StatusDefinition.LOGIN:
                        voiceToLogin(intent.getStringExtra(StatusDefinition.LOGIN));
                        break;
                }
            }
        }
    }


}
