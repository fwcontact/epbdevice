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
public class CrmPosline implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7617356889682008902L;
	private String productNo;
    private String saleQty;
    private String saleAmt;
    private String lineType;
    
    private static final String EMPTY = ""; 
    
    public CrmPosline() {    
        super();
        this.productNo = EMPTY;
        this.saleQty = EMPTY;
        this.saleAmt = EMPTY;
        this.lineType = EMPTY;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getSaleQty() {
        return saleQty;
    }

    public void setSaleQty(String saleQty) {
        this.saleQty = saleQty;
    }

    public String getSaleAmt() {
        return saleAmt;
    }

    public void setSaleAmt(String saleAmt) {
        this.saleAmt = saleAmt;
    }

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }
    
}
