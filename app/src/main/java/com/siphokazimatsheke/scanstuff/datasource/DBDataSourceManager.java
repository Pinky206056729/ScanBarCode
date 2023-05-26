package com.siphokazimatsheke.scanstuff.datasource;

import android.content.Context;

import com.siphokazimatsheke.scanstuff.models.BarCodeModel;
import java.util.List;

public class DBDataSourceManager {

    private static DBDataSourceManager instance = null;
    private DBDataSource dataSource;

    public DBDataSourceManager() {
    }

    public DBDataSource getDataSource() {
        return dataSource;
    }

    public void setup(Context context) {
        if (dataSource == null) {
            dataSource = new DBDataSource(context);
            dataSource.open();
        }
    }

    public static DBDataSourceManager getInstance() {
        if (instance == null) {
            instance = new DBDataSourceManager();
        }
        return instance;
    }

    public List<BarCodeModel> loadBarCodeList() {
        List<BarCodeModel> barCode = dataSource.loadBarCodeList();
        return barCode;
    }

    public void saveBarCode(BarCodeModel barCodeModel) {
        dataSource.saveBarCode(barCodeModel);
    }

    public void deleteSelectedBarCode(BarCodeModel barCodeModel) {
        dataSource.deleteBarCode(barCodeModel);
    }
}
