/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author liang
 */
public class CrmPosVipInfo implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -4307591540924474082L;
	private String vipId;
    private String name;
    private String classId;
    private Character gender;
    private Date birthDate;
    private String vipPhone1;
    private String locId;
    private String empId;
    private BigDecimal points;
    
    private static final String EMPTY = ""; 
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    
    public CrmPosVipInfo() {    
        super();
        this.vipId = EMPTY;
        this.name = EMPTY;
        this.classId = EMPTY;
        this.gender = null;
        this.birthDate = null;
        this.vipPhone1 = EMPTY;
        this.locId = EMPTY;
        this.empId = EMPTY;
        this.points = ZERO;
    }

    public String getVipId() {
        return vipId;
    }

    public void setVipId(String vipId) {
        this.vipId = vipId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getVipPhone1() {
        return vipPhone1;
    }

    public void setVipPhone1(String vipPhone1) {
        this.vipPhone1 = vipPhone1;
    }

    public String getLocId() {
        return locId;
    }

    public void setLocId(String locId) {
        this.locId = locId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public BigDecimal getPoints() {
        return points;
    }

    public void setPoints(BigDecimal points) {
        this.points = points;
    }
    
}
