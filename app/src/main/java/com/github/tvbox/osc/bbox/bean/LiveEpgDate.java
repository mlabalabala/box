package com.github.tvbox.osc.bbox.bean;
import java.util.Date;

public class LiveEpgDate {

    private int index;
    private String datePresented;
    private Date dateParamVal;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDatePresented() {
        return datePresented;
    }

    public void setDatePresented(String datePresented) {
        this.datePresented = datePresented;
    }

    public Date getDateParamVal() {
        return dateParamVal;
    }

    public void setDateParamVal(Date dateParamVal) {
        this.dateParamVal = dateParamVal;
    }
}
