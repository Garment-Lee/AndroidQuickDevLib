package com.ligf.androiddatabaselib.liteorm.bean;

import java.io.Serializable;

/**
 * Created by ligf on 2018/5/10.
 */

public class Person implements Serializable{

    String str = null;
    char[] strArray = str.toCharArray();

    private void arrayToString(){
        str = strArray.toString();
    }


}
