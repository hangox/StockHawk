package com.udacity.stockhawk.data;

import java.util.Date;

/**
 * Created With Android Studio
 * User hangox
 * Date 2017/4/23
 * Time 下午10:44
 */

public class History {
    public Date  mDate;
    public float mValue;

    public History(String splitValue){
        String[] values = splitValue.split(",");
        mDate = new Date(Long.valueOf(values[0]));
        mValue = Float.valueOf(values[1]);
    }
}
