package com.dogoo.office.authz.advice;

import java.util.Date;

public class ErrorMessage {
    private int status;

    private Date date;

    private String message;

    private String desc;

    public ErrorMessage(int status, Date date, String message, String desc) {
        this.status = status;
        this.date = date;
        this.message = message;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
