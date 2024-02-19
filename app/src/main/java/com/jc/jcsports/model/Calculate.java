package com.jc.jcsports.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class Calculate implements Serializable, Comparable<Calculate> {

    @SerializedName("name")
    public String name;
    @SerializedName("dateCtn")
    public int dateCtn;


    @Override
    public int compareTo(Calculate calculate) {
        return name.compareTo(calculate.name);
    }
}
