package com.jc.jcsports.model;

public enum MoveDirectory {
    ATTENDANCEDIALOG("AttendanceDialog"),
    WAITLISTDIALOG("WaitListDialog");
    private final String label;
    MoveDirectory(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
}
