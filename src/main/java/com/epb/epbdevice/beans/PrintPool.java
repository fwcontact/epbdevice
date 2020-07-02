/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrintPool implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4768110116317408705L;

	private BigDecimal lineNo;
	private BigInteger orderNo;
	private String printCommand;
	private String const1;
	private String const2;
	private String format;
	private BigInteger length;
	private String align;
	private String breakFlg;
	private String fillBlankFlg;
	private String val;
	private String printPort;
	private BigInteger printSeq;

	private static final String EMPTY = "";

	public PrintPool() {
		super();
		this.lineNo = null;
		this.orderNo = null;
		this.printCommand = EMPTY;
		this.const1 = EMPTY;
		this.const2 = EMPTY;
		this.format = EMPTY;
		this.length = null;
		this.align = EMPTY;
		this.breakFlg = EMPTY;
		this.fillBlankFlg = EMPTY;
		this.val = EMPTY;
		this.printPort = EMPTY;
		this.printSeq = null;
	}

	public BigDecimal getLineNo() {
		return lineNo;
	}

	public void setLineNo(BigDecimal lineNo) {
		this.lineNo = lineNo;
	}

	public BigInteger getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(BigInteger orderNo) {
		this.orderNo = orderNo;
	}

	public String getPrintCommand() {
		return printCommand;
	}

	public void setPrintCommand(String printCommand) {
		this.printCommand = printCommand;
	}

	public String getConst1() {
		return const1;
	}

	public void setConst1(String const1) {
		this.const1 = const1;
	}

	public String getConst2() {
		return const2;
	}

	public void setConst2(String const2) {
		this.const2 = const2;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public BigInteger getLength() {
		return length;
	}

	public void setLength(BigInteger length) {
		this.length = length;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getBreakFlg() {
		return breakFlg;
	}

	public void setBreakFlg(String breakFlg) {
		this.breakFlg = breakFlg;
	}

	public String getFillBlankFlg() {
		return fillBlankFlg;
	}

	public void setFillBlankFlg(String fillBlankFlg) {
		this.fillBlankFlg = fillBlankFlg;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getPrintPort() {
		return printPort;
	}

	public void setPrintPort(String printPort) {
		this.printPort = printPort;
	}

	public BigInteger getPrintSeq() {
		return printSeq;
	}

	public void setPrintSeq(BigInteger printSeq) {
		this.printSeq = printSeq;
	}

}
