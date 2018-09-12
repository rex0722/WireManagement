package com.study.application.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.study.application.R;
import com.study.application.speech.Classification;
import com.study.application.speech.StatusDefinition;

public class FunctionActivity extends AppCompatActivity {

    private final String TAG = "FunctionActivity";
    private boolean doubleBackToExitPressedOnce = false;
    FunctionBroadcast funcBroadcast = new FunctionBroadcast();
    Intent intent = new Intent();
    Bundle bundle = new Bundle();

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_function);

        initView();
        broadcastRegister();
        StatusDefinition.CURRENT_STATUS = StatusDefinition.FUNCTION_SELECT;
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusDefinition.CURRENT_STATUS = StatusDefinition.FUNCTION_SELECT;
    }

    private void initView() {
        Button borrowBtn = findViewById(R.id.borrowedBtn);
        Button returnBtn = findViewById(R.id.returnedBtn);
        Button searchBtn = findViewById(R.id.searchedBtn);
        Button subscribeBtn = findViewById(R.id.subscribeBtn);
        Button cancelSubscriptionBtn = findViewById(R.id.cancelSubscriptionBtn);

        Button.OnClickListener listener = view -> {

            switch (view.getId()) {
                case R.id.borrowedBtn:
                    borrowSet();
                    break;
                case R.id.returnedBtn:
                    returnSet();
                    break;
                case R.id.searchedBtn:
                    searchSet();
                    break;
                case R.id.subscribeBtn:
                    subscribeSet();
                    break;
                case R.id.cancelSubscriptionBtn:
                    cancelSubscriptionSet();
                    break;
            }
        };

        borrowBtn.setOnClickListener(listener);
        returnBtn.setOnClickListener(listener);
        searchBtn.setOnClickListener(listener);
        subscribeBtn.setOnClickListener(listener);
        cancelSubscriptionBtn.setOnClickListener(listener);
    }

    private void borrowSet(){
        bundle.putString("FUNCTION_STRING", getResources().getString(R.string.borrow_item));
        intent.putExtras(bundle);
        intent.setClass(this, BorrowActivity.class);
        startActivity(intent);
    }

    private void returnSet(){
        bundle.putString("FUNCTION_STRING", getResources().getString(R.string.return_item));
        intent.putExtras(bundle);
        intent.setClass(this, ReturnActivity.class);
        startActivity(intent);
    }
    private void subscribeSet(){
        bundle.putString("FUNCTION_STRING", getResources().getString(R.string.subscribe_item));
        intent.putExtras(bundle);
        intent.setClass(this, SubscribeActivity.class);
        startActivity(intent);
    }

    private void cancelSubscriptionSet(){
        bundle.putString("FUNCTION_STRING", getResources().getString(R.string.subscribe_item));
        intent.putExtras(bundle);
        intent.setClass(this, CancelSubscriptionActivity.class);
        startActivity(intent);
    }

    private void searchSet(){
        intent.setClass(this, SearchActivity.class);
        startActivity(intent);
    }

    private void broadcastRegister(){
        registerReceiver(funcBroadcast, new IntentFilter("FUNCTION_SELECT"));
    }

    private void voiceToFunctionSelect(String inputClassification){

        switch (inputClassification){
            case Classification.BORROW:
                borrowSet();
                break;
            case Classification.RETURN:
                returnSet();
                break;
            case Classification.SUBSCRIBE:
                returnSet();
                break;
            case Classification.SEARCH:
                searchSet();
                break;
        }

        Log.w("TAG", "StatusDefinition : " + StatusDefinition.CURRENT_STATUS);
    }

    private class FunctionBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null){
                switch (intent.getAction()){
                    case StatusDefinition.FUNCTION_SELECT:
                        voiceToFunctionSelect(intent.getStringExtra(StatusDefinition.FUNCTION_SELECT));
                        break;
                }
            }
        }
    }
}
