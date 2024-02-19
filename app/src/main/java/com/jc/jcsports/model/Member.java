package com.jc.jcsports.model;

import android.graphics.Color;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class Member implements Serializable, Comparable<Member> {

    @Override
    public int compareTo(Member member) {
        return name.compareTo(member.name);
    }

    public enum CurrentStatus {
        IN("IN"),
        OUT("OUT");

        private final String label;

        CurrentStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum Colors {
        GREEN("GREEN"),
        RED("RED"),
        ORANGE("ORANGE"),
        YELLOW("YELLOW"),
        BLACK("BLACK");

        private final String label;
        private int colorInt;

        Colors(String label) {
            this.label = label;
            colorInt = 0;
        }

        public int getColorInt() {
            switch (this) {
                case ORANGE:
                    colorInt = Color.argb(255, 255, 127, 0);
                    break;
                case RED:
                    colorInt = Color.RED;
                    break;
                case GREEN:
                    colorInt = Color.GREEN;
                    break;
                case YELLOW:
                    colorInt = Color.YELLOW;
                    break;
                case BLACK:
                    colorInt = Color.BLACK;
                    break;
            }
            return colorInt;
        }

        public String getLabel() {
            return label;
        }
    }

    @SerializedName("clubName")
    public String clubName;
    @SerializedName("identity")
    public int m_Id;
    @SerializedName("name")
    public String name;
    @SerializedName("gender")
    public String gender;
    @SerializedName("color")
    public Colors color;
    @SerializedName("currentStatus")
    public CurrentStatus currentStatus;


}
