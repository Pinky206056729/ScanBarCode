package com.siphokazimatsheke.scanstuff.models;

public class BarCodeModel {

    private int id;
    private String name;
    private String scannedBarCodeNumber;
    private int countBarCode = 1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScannedBarCodeNumber() {
        return scannedBarCodeNumber;
    }

    public void setScannedBarCodeNumber(String scannedBarCodeNumber) {
        this.scannedBarCodeNumber = scannedBarCodeNumber;
    }

    public int getCountBarCode() {
        return countBarCode;
    }

    public void setCountBarCode(int countBarCode) {
        this.countBarCode = countBarCode;
    }
}
