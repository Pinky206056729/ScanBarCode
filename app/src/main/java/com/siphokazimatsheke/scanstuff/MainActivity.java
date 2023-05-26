package com.siphokazimatsheke.scanstuff;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.siphokazimatsheke.scanstuff.adapter.BarCodeListAdapter;
import com.siphokazimatsheke.scanstuff.datasource.DBDataSourceManager;
import com.siphokazimatsheke.scanstuff.models.BarCodeModel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button btnScan_Bar_Code, btnAdd_bar_code;
    EditText etDisplay_Bar_Code;
    private ListView lvQRCode;
    BarCodeModel barCodeModel;
    BarCodeListAdapter barCodeListAdapter;
    List<BarCodeModel> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan_Bar_Code = (Button) findViewById(R.id.btnScan_bar_code);
        btnAdd_bar_code = findViewById(R.id.btnAdd_bar_code);

        etDisplay_Bar_Code = findViewById(R.id.etDisplay_Bar_Code);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(12);
        etDisplay_Bar_Code.setFilters(filterArray);

        lvQRCode = findViewById(R.id.lvQRCode);
        DBDataSourceManager.getInstance().setup(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadBarCode();

        btnScan_Bar_Code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setPrompt("Scan a barcode or QR Code");
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.initiateScan();
            }
        });

        etDisplay_Bar_Code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etDisplay_Bar_Code.callOnClick();
                }
            }
        });

        etDisplay_Bar_Code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnScan_Bar_Code.getVisibility() == View.VISIBLE){
                    btnScan_Bar_Code.setVisibility(View.INVISIBLE);
                    btnAdd_bar_code.setVisibility(View.VISIBLE);
                }
            }
        });

        btnAdd_bar_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String etBar_Code = etDisplay_Bar_Code.getText().toString().trim();
                if (isQRCodeValidate(etBar_Code)) {

                    if (!listItems.isEmpty()) {
                        if (!isBarCodeAvailable(etBar_Code)) {
                            addNewBarCode(etBar_Code);
                        }
                    } else {
                        addNewBarCode(etBar_Code);
                    }
                    etDisplay_Bar_Code.getText().clear();
                    loadBarCode();
                }else{
                    Toast.makeText(getBaseContext(), "Barcode is invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "Barcode is invalid"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Barcode is invalid", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                etDisplay_Bar_Code.setText(intentResult.getContents());

                String etBar_Code = etDisplay_Bar_Code.getText().toString().trim();
                if (isQRCodeValidate(etBar_Code)) {
                    if (!listItems.isEmpty()) {
                        if (!isBarCodeAvailable(etBar_Code)) {
                            addNewBarCode(etBar_Code);
                        }
                    } else {
                        addNewBarCode(etBar_Code);
                    }

                    barCodeListAdapter = new BarCodeListAdapter(this, listItems);
                    lvQRCode.setAdapter(barCodeListAdapter);
                    etDisplay_Bar_Code.getText().clear();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean isBarCodeAvailable(String etBar_Code) {

        for (int i = 0; i < listItems.size(); i++) {
            String getSelectBarCode = listItems.get(i).getScannedBarCodeNumber();
            if (etBar_Code.equals(getSelectBarCode)) {
                int count = listItems.get(i).getCountBarCode() + 1;
                listItems.get(i).setCountBarCode(count);

                barCodeModel = new BarCodeModel();
                barCodeModel.setScannedBarCodeNumber(listItems.get(i).getScannedBarCodeNumber());
                barCodeModel.setId(listItems.get(i).getId());
                barCodeModel.setName(listItems.get(i).getName());
                barCodeModel.setCountBarCode(count);
                saveBarCode(barCodeModel);
                return true;
            }
        }
        return false;
    }

    private boolean isQRCodeValidate(String etBar_Code) {

        String regExpn = "[a-zA-Z 0-9]+";

        if (etBar_Code.isEmpty()) {
            etDisplay_Bar_Code.setError("Field can not be empty");
            return false;
        } else {
            Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(etBar_Code);

            if (matcher.matches()) {
                etDisplay_Bar_Code.setError(null);
                return true;
            } else {
                etDisplay_Bar_Code.setError("Please enter a valid bar code");
                return false;
            }
        }
    }

    private void addNewBarCode(String etBar_Code) {
        barCodeModel = new BarCodeModel();
        barCodeModel.setScannedBarCodeNumber(etBar_Code);
        barCodeModel.setCountBarCode(1);
        saveBarCode(barCodeModel);
    }

    private void loadBarCode() {
        this.listItems = DBDataSourceManager.getInstance().loadBarCodeList();

        if (!listItems.isEmpty()) {
            barCodeListAdapter = new BarCodeListAdapter(this, listItems);
            lvQRCode.setAdapter(barCodeListAdapter);
        }
    }

    private void saveBarCode(BarCodeModel Item) {
        DBDataSourceManager.getInstance().saveBarCode(Item);
    }
}