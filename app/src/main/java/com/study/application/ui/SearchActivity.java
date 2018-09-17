package com.study.application.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.study.application.R;
import com.study.application.leanCloud.DisplayData;
import com.study.application.leanCloud.ListViewDataAdapter;
import com.study.application.leanCloud.Reader;
import com.study.application.leanCloud.RecyclerViewAdapter;
import com.study.application.speech.Classification;
import com.study.application.speech.StatusDefinition;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private final String TAG = "SearchActivity";
    public static Context searchContext;

    private Spinner spnType;
    private Spinner spnItem;
    private Button btnSearch;
    private RecyclerView rycData;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean isDataReady = false;

    private final DataBroadcast dataBroadcast = new DataBroadcast();
    private final Reader reader = new Reader();
    private ArrayAdapter<CharSequence> spnTypeAdapter;

    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search);
        searchContext = this;
        findViews();

        broadcastRegister();
        setListeners();
        setSpinnerTypeElements();
        spnType.setAdapter(spnTypeAdapter);
        StatusDefinition.CURRENT_STATUS = StatusDefinition.BORROW_RETURN_SEARCH;
    }

    @Override
    protected void onResume() {

        super.onResume();
        StatusDefinition.CURRENT_STATUS = StatusDefinition.BORROW_RETURN_SEARCH;
    }

    private void findViews() {
        spnType = findViewById(R.id.spnType);
        spnItem = findViewById(R.id.spnItem);
        btnSearch = findViewById(R.id.btnSearch);
        rycData = findViewById(R.id.rycData);
        layoutManager = new LinearLayoutManager(this);
        rycData.setLayoutManager(layoutManager);
    }

    private void broadcastRegister() {
        registerReceiver(dataBroadcast, new IntentFilter("DelverData"));
        registerReceiver(dataBroadcast, new IntentFilter("SpinnerItemElement"));
        registerReceiver(dataBroadcast, new IntentFilter("DelverConditionData"));
        registerReceiver(dataBroadcast, new IntentFilter(StatusDefinition.BORROW_RETURN_SEARCH));
    }

    private void setListeners() {
        btnSearch.setOnClickListener(v -> {
            if (isNetworkConnected()){
                if (isDataReady) {
                    reader.conditionSearch(spnType.getSelectedItem().toString(), spnItem.getSelectedItem().toString());
                }
            }else
                new AlertDialog.Builder(this).setTitle(getString(R.string.dialog_title_error)).
                        setMessage(getString(R.string.dialog_message_network_error)).
                        setPositiveButton(getString(R.string.dialog_button_check), (DialogInterface dialog, int which)-> {
                        }).setIcon(R.drawable.error).show();
        });

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reader.spinnerElementSearch(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean isNetworkConnected(){
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable();
    }

    private void setSpinnerTypeElements() {
        spnTypeAdapter = ArrayAdapter.createFromResource(searchContext, R.array.search_type, R.layout.spinner_setting);
    }

    private void setSpinnerItemElements(String[] spinnerItemElements) {
        ArrayAdapter<String> spnItemAdapter;

        spnItemAdapter = new ArrayAdapter<>(searchContext, R.layout.spinner_setting, spinnerItemElements);
        spnItem.setAdapter(spnItemAdapter);
    }

    private void voiceToSearchActivity(String inputClassification){
        switch (inputClassification){
            case Classification.GETBACK:
                finish();
                break;
        }
    }

    private class DataBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            ArrayList<DisplayData> dataArrayList;
            ArrayList<DisplayData> conditionDataArrayList;

            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case "DelverData":
                        dataArrayList = (ArrayList<DisplayData>) intent.getSerializableExtra("data");
                        adapter = new RecyclerViewAdapter(dataArrayList);
                        rycData.setAdapter(adapter);
                        break;
                    case "SpinnerItemElement":
                        setSpinnerItemElements(intent.getStringArrayExtra("SpinnerItemElementArray"));
                        isDataReady = true;
                        break;
                    case "DelverConditionData":
                        conditionDataArrayList = (ArrayList<DisplayData>) intent.getSerializableExtra("conditionData");
                        adapter = new RecyclerViewAdapter(conditionDataArrayList);
                        rycData.setAdapter(adapter);
                        break;
                    case StatusDefinition.BORROW_RETURN_SEARCH:
                        voiceToSearchActivity(intent.getStringExtra(StatusDefinition.BORROW_RETURN_SEARCH));
                        break;
                }
            }
        }
    }


}
