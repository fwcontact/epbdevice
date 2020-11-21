/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.beans;

import java.io.Serializable;

/**
 *
 * @author liang
 */
public class CrmCoupon implements Serializable{
    
    private String couponNo;
    private String disAmt;
    private int paySerialno;
    
    private static final String EMPTY = ""; 
    
    public CrmCoupon() {    
        super();
        this.couponNo = EMPTY;
        this.disAmt = EMPTY;
        this.paySerialno = 0;
    }

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    public String getDisAmt() {
        return disAmt;
    }

    public void setDisAmt(String disAmt) {
        this.disAmt = disAmt;
    }

    public int getPaySerialno() {
        return paySerialno;
    }

    public void setPaySerialno(int paySerialno) {
        this.paySerialno = paySerialno;
    }
}
