/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.memberson;

import com.epb.epbdevice.utl.CommonUtility;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author sim_liang
 */
public class Epbmemberson {
    
    private static final String SLASH = "/";
    private static final String EMPTY = "";
    private static final String COMMA = ",";
    private static final String ACTIVE = "ACTIVE";
    private static final String EXPIRED = "EXPIRED";
    private static final String TERMINATED = "TERMINATED";
    public static final String MSG_ID = "msgId";
    public static final String MSG = "msg";
    public static final String RESULT = "result";
    public static final String RETURN_OK = "OK";
    public static final String FAIL = "FAIL";    
//    public static final String RETURN_CUSTOMER_MAP_LIST = "CUSTOMERMAPLIST";
    public static final String RETURN_CUSTOMER_NUMBER = "CustomerNumber";
    public static final String RETURN_NAME = "Name";
    public static final String RETURN_MOBILE_NUMBER = "MobileNumber";
    public static final String RETURN_EMAIL_ADDRESS = "EmailAddress";
    public static final String RETURN_DOB = "DOB";
    public static final String RETURN_FIRST_NAME = "FirstName";
    public static final String RETURN_LAST_NAME = "LastName";
    public static final String RETURN_GENDER_CODE = "GenderCode";
    public static final String RETURN_NATIONALITY_CODE = "NationalityCode";
    public static final String RETURN_HAS_ACTIVE_MEMBERSHIP = "HasActiveMembership";
//    public static final String RETURN_TIER = "Tier";
    public static final String RETURN_TYPE = "Type";
    public static final String RETURN_MEMBER_NO = "MemberNo";
    public static final String RETURN_CUSTOMIZE_NO = "CardNumber";  // it is funny, do not modify
    public static final String RETURN_STATUS = "Status";
    public static final String RETURN_VALID_FROM = "ValidFrom";
    public static final String RETURN_EXPIRY_DATE = "ExpiryDate";
    public static final String RETURN_BALANCE = "Balance";
    public static final String RETURN_FROM_RATE = "FromRate";
    public static final String RETURN_TO_RATE = "ToRate";
    public static final String RETURN_AMOUNT = "Amount";
    public static final String RETURN_VIP_DISC = "VipDiscount";
    public static final String RETURN_EPB_TYPE_ID = "typeId";
    public static final String RETURN_CUSTOMER_SOURCE = "CustomerSource";
//    private static final String RETURN_EPB_VIP_CLASS = "classId";
//    private static final String RETURN_EPB_VIP_DISC = "vipDisc";
    
    private static final Log LOG = LogFactory.getLog(Epbmemberson.class);
//    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
////    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//    private static final SimpleDateFormat DATEFORMAT2 = new SimpleDateFormat("yyyy-MM-dd 'T'HH:mm:ssZ");
    private static final SimpleDateFormat DATEFORMAT3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    /**
     * call memberson API, get vip ID, name, mobile
     *
     * @param conn JDBC connection
     * @param opentableRecKey opentable.rec_key, BigDecimal
     * @param vipId CustomerNumber, String
     * @param cardNumber cardNumber, String
     * @param nric nric, String
     * @param vipName name, String
     * @param vipPhoneCountryCode MobileCountryCode, String 
     * @param vipPhone Mobile, String
     * @param emailAddress emailAddress, String 
     * @return Map<String, String> 
     */
    public static Map<String, String> getVip(final Connection conn,
            final BigDecimal opentableRecKey,
            final String vipId, final String cardNumber, final String nric, final String vipName,
            final String vipPhoneCountryCode, final String vipPhone, final String emailAddress) {
        final Map<String, String> returnMap = new HashMap<>();
        // log version        
        CommonUtility.printVersion();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // get system setting
            String sql = "SELECT SET_ID, SET_STRING FROM EP_SYS_SETTING WHERE SET_ID IN ('POSO2OCONT', 'POSO2OURL', 'POSO2OVENDOR', 'POSO2OTOKEN', 'POSO2OAPPKEY', 'POSO2OSECRET', 'DiscFormat')";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String setId;
            String setString;
            String posO2oCont = EMPTY;
            String posO2oVendor = EMPTY;
            String posO2oUrl = EMPTY;
            String posO2oAccessToken = EMPTY;
            String posO2oAppKey = EMPTY;
            String posO2oAppSecret = EMPTY;
            String posO2oAuth = EMPTY;
            
            while (rs.next()) {
                setId = EMPTY;
                setString = EMPTY;
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(columnName);
                    if ("SET_ID".equals(columnName.toUpperCase())) {
                        setId = (String) value;
                    } else if ("SET_STRING".equals(columnName.toUpperCase())) {
                        setString = (String) value;
                    }
                }
                if ("POSO2OCONT".equals(setId)) {
                        posO2oCont = setString;
                } else if ("POSO2OURL".equals(setId)) {
                        posO2oUrl = setString;
                } else if ("POSO2OTOKEN".equals(setId)) {
                        posO2oAccessToken = setString;
                } else if ("POSO2OVENDOR".equals(setId)) {
                        posO2oVendor = setString;
                } else if ("POSO2OAPPKEY".equals(setId)) {
                        posO2oAppKey = setString;
                } else if ("POSO2OSECRET".equals(setId)) {
                        posO2oAppSecret = setString;
                }
            }
            
            // free mem
            pstmt.close();
            rs.close();
            
            if ("B".equals(posO2oVendor)) {
                if (!"Y".equals(posO2oCont)) {
                    returnMap.put(MSG_ID, FAIL);
                    returnMap.put(MSG, "API is disable");
                    return returnMap;
                }
                if (posO2oUrl == null ||posO2oUrl.length() == 0) {
                    returnMap.put(MSG_ID, FAIL);
                    returnMap.put(MSG, "API URL is empty");
                    return returnMap;
                }
                if (posO2oAccessToken == null || posO2oAccessToken.length() == 0) {
                    returnMap.put(MSG_ID, FAIL);
                    returnMap.put(MSG, "API token is empty");
                    return returnMap;
                }
                if (posO2oAppKey == null || posO2oAppKey.length() == 0) {
                    returnMap.put(MSG_ID, FAIL);
                    returnMap.put(MSG, "API user is empty");
                    return returnMap;
                }
                if (posO2oAppSecret == null || posO2oAppSecret.length() == 0) {                    
                    returnMap.put(MSG_ID, FAIL);
                    returnMap.put(MSG, "API password is empty");
                    return returnMap;
                }
            } else {
                returnMap.put(MSG_ID, FAIL);
                returnMap.put(MSG, "Disable memberson API");
                return returnMap;
            }
            
            // call memberson API
            posO2oAuth = Epbmemberson.getAuth(posO2oAppKey, posO2oAppSecret);
            Map<String, Object> retMap = searchVip(conn, opentableRecKey, posO2oUrl, posO2oAuth, posO2oAccessToken, vipId, cardNumber, nric, vipName == null || EMPTY.equals(vipName) ? EMPTY : "%" + vipName + "%", vipPhoneCountryCode, vipPhone, emailAddress);
            if (!Epbmemberson.RETURN_OK.equals(retMap.get(MSG_ID))) {
                returnMap.put(MSG_ID, (String) retMap.get(MSG_ID));
                returnMap.put(MSG, (String) retMap.get(MSG));
                returnMap.put(RESULT, EMPTY);
                return returnMap;
            }
            returnMap.put(MSG_ID, RETURN_OK);   
            returnMap.put(MSG, EMPTY); 
            returnMap.put(RESULT, (String) retMap.get(MSG));          
            
            return returnMap;
        } catch (SQLException ex) {
            String msg = "error getVip:" + ex.getMessage();
            LOG.error(msg, ex);
            returnMap.put(MSG_ID, FAIL);
            returnMap.put(MSG, msg);
            return returnMap;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                // DO NOTHING
                LOG.error("error getVip 2", ex);
            }
        }
    }
    
    /**
     * call memberson API, get vip class, vip discount, points, redeem ratio and so on
     * get this value and update to table OPENTABLE
     *
     * @param conn JDBC connection
     * @param opentableRecKey opentable.rec_key, BigDecimal
     * @param customerSource MEMBERSON OR EPB, String
     * @param customerNumber CustomerNumber, String
     * @param name name, String
     * @param mobileNumber Mobile, String
     * @param emailAddress emailAddress, String 
     * @param dob dob, String 
     * @return Map<String, String> 
     */
    public static Map<String, String> updateVipSummary(final Connection conn,
            final BigDecimal opentableRecKey,
            final String customerSource, final String customerNumber, final String name, String mobileNumber, final String emailAddress, final String dob) {
        final Map<String, String> returnMap = new HashMap<>();
//        // log version        
//        CommonUtility.printVersion();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // get system setting
            String sql = "SELECT SET_ID, SET_STRING FROM EP_SYS_SETTING WHERE SET_ID IN ('POSO2OCONT', 'POSO2OURL', 'POSO2OVENDOR', 'POSO2OTOKEN', 'POSO2OAPPKEY', 'POSO2OSECRET', 'DiscFormat')";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String setId;
            String setString;
            String posO2oCont = EMPTY;
            String posO2oVendor = EMPTY;
            String posO2oUrl = EMPTY;
            String posO2oAccessToken = EMPTY;
            String posO2oAppKey = EMPTY;
            String posO2oAppSecret = EMPTY;
            String posO2oAuth = EMPTY;
            String discFormat = "A";
            
            while (rs.next()) {
                setId = EMPTY;
                setString = EMPTY;
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(columnName);
                    if ("SET_ID".equals(columnName.toUpperCase())) {
                        setId = (String) value;
                    } else if ("SET_STRING".equals(columnName.toUpperCase())) {
                        setString = (String) value;
                    }
                }
                if ("POSO2OCONT".equals(setId)) {
                        posO2oCont = setString;
                } else if ("POSO2OURL".equals(setId)) {
                        posO2oUrl = setString;
                } else if ("POSO2OTOKEN".equals(setId)) {
                        posO2oAccessToken = setString;
                } else if ("POSO2OVENDOR".equals(setId)) {
                        posO2oVendor = setString;
                } else if ("POSO2OAPPKEY".equals(setId)) {
                        posO2oAppKey = setString;
                } else if ("POSO2OSECRET".equals(setId)) {
                        posO2oAppSecret = setString;
                } else if ("DiscFormat".equals(setId)) {
                        discFormat = setString;
                }
            }
            
            // free mem
            pstmt.close();
            rs.close();
            
            if ("B".equals(posO2oVendor)) {
                if (!"Y".equals(posO2oCont)) {
                    returnMap.put(MSG_ID, FAIL);
                    returnMap.put(MSG, "API is disable");
                    return returnMap;
                }
                if (posO2oUrl == null ||posO2oUrl.length() == 0) {
                    returnMap.put(MSG_ID, FAIL);
                    returnMap.put(MSG, "API URL is empty");
                    return returnMap;
                }
                if (posO2oAccessToken == null || posO2oAccessToken.length() == 0) {
                    returnMap.put(MSG_ID, FAIL);
                    returnMap.put(MSG, "API token is empty");
                    return returnMap;
                }
                if (posO2oAppKey == null || posO2oAppKey.length() == 0) {
                    returnMap.put(MSG_ID, FAIL);
                    returnMap.put(MSG, "API user is empty");
                    return returnMap;
                }
                if (posO2oAppSecret == null || posO2oAppSecret.length() == 0) {                    
                    returnMap.put(MSG_ID, FAIL);
                    returnMap.put(MSG, "API password is empty");
                    return returnMap;
                }
            } else {
                returnMap.put(MSG_ID, FAIL);
                returnMap.put(MSG, "Disable memberson API");
                return returnMap;
            }
                       
            // get parameter
            sql = "SELECT A.SHOP_ID, B.CURR_ID, A.ORG_ID FROM OPENTABLE A, EP_ORG B WHERE A.REC_KEY = ? AND A.ORG_ID = B.ORG_ID";
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, opentableRecKey);
            rs = pstmt.executeQuery();
            metaData = (ResultSetMetaData) rs.getMetaData();
            columnCount = metaData.getColumnCount();
            String shopId = EMPTY;
            String currId = EMPTY;
            String orgId = EMPTY;
            
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(columnName);
                    if ("SHOP_ID".equals(columnName.toUpperCase())) {
                        shopId = (String) value;
                    } else if ("CURR_ID".equals(columnName.toUpperCase())) {
                        currId = (String) value;
                    } else if ("ORG_ID".equals(columnName.toUpperCase())) {
                        orgId = (String) value;
                    }
                }
            }
            // free mem
            pstmt.close();
            rs.close();
            
            if (shopId == null || shopId.length() == 0 
                    || currId == null || currId.length() == 0) {
                returnMap.put(MSG_ID, FAIL);
                returnMap.put(MSG, "Invalid shop or currency");
                return returnMap;
            }
            
            String classId = EMPTY;
            String epbTypeId = EMPTY;  // browrrow qr code
            BigDecimal fromRate;
            BigDecimal toRate;
            BigDecimal cumPts;
            String memberNo;
            String customizeNo;
            String type;
            if ("EPB".equals(customerSource)) {
                type = EMPTY;
                fromRate = BigDecimal.ONE;
                toRate = BigDecimal.ONE;
                cumPts = BigDecimal.ZERO;
                memberNo = customerNumber;
                customizeNo = EMPTY;
                // get parameter
                sql = "SELECT CLASS_ID FROM POS_VIP_MAS WHERE VIP_ID = ? AND (ORG_ID IS NULL OR ORG_ID = ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setObject(1, customerNumber);
                pstmt.setObject(2, orgId);
                rs = pstmt.executeQuery();
                metaData = (ResultSetMetaData) rs.getMetaData();
                columnCount = metaData.getColumnCount();
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(columnName);
                        if ("CLASS_ID".equals(columnName.toUpperCase())) {
                            classId = (String) value;
                        }
                    }
                }
                // free mem
                pstmt.close();
                rs.close();
            } else {
                // call memberson API
                posO2oAuth = Epbmemberson.getAuth(posO2oAppKey, posO2oAppSecret);
                Map<String, Object> retMap = Epbmemberson.getRedeemPointsConversionRate(posO2oUrl, posO2oAuth, posO2oAccessToken, currId);
                if (!Epbmemberson.RETURN_OK.equals(retMap.get(MSG_ID))) {
                    returnMap.put(MSG_ID, (String) retMap.get(MSG_ID));
                    returnMap.put(MSG, (String) retMap.get(MSG));
                    return returnMap;
                }
//            BigDecimal posO2oRedeemRatio = retMap.containsKey(Epbmemberson.RETURN_TO_RATE) ? (BigDecimal) retMap.get(Epbmemberson.RETURN_TO_RATE) : null;
                fromRate = retMap.containsKey(Epbmemberson.RETURN_FROM_RATE) ? (BigDecimal) retMap.get(Epbmemberson.RETURN_FROM_RATE) : null;
                toRate = retMap.containsKey(Epbmemberson.RETURN_TO_RATE) ? (BigDecimal) retMap.get(Epbmemberson.RETURN_TO_RATE) : null;

//            Map<String, String> customerMap = (Map<String, String>) retMap.get(Epbmemberson.RETURN_CUSTOMER_MAP);
                retMap = getVipSummary(posO2oUrl, posO2oAuth, posO2oAccessToken, customerNumber);
                if (!Epbmemberson.RETURN_OK.equals(retMap.get(MSG_ID))) {
                    returnMap.put(MSG_ID, (String) retMap.get(MSG_ID));
                    returnMap.put(MSG, (String) retMap.get(MSG));
                    return returnMap;
                }
//            String classId = (String) retMap.get(Epbmemberson.RETURN_TIER);
                type = getSmoothType((String) retMap.get(Epbmemberson.RETURN_TYPE));
//            System.out.println("type:" + type);
//            String classId = EMPTY;
                if (type != null && type.length() != 0) {
                    // free mem
                    pstmt.close();
                    rs.close();
                    sql = "SELECT REMARK, NAME FROM VIP_SELFMAS1 WHERE SELF1_ID = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setObject(1, type);
//                pstmt.setObject(2, orgId);
                    rs = pstmt.executeQuery();
                    metaData = (ResultSetMetaData) rs.getMetaData();
                    columnCount = metaData.getColumnCount();
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnLabel(i);
                            Object value = rs.getObject(columnName);
                            if ("REMARK".equals(columnName.toUpperCase())) {
                                classId = (String) value;
                            } else if ("NAME".equals(columnName.toUpperCase())) {
                                epbTypeId = (String) value;
                            }
                        }
                    }
                    // free mem
                    pstmt.close();
                    rs.close();
                }                
//            System.out.println("classId:" + classId);
                cumPts = retMap.get(Epbmemberson.RETURN_BALANCE) == null || EMPTY.equals(retMap.get(Epbmemberson.RETURN_BALANCE))
                        ? BigDecimal.ZERO
                        : new BigDecimal((String) retMap.get(Epbmemberson.RETURN_BALANCE));
                memberNo = (String) retMap.get(Epbmemberson.RETURN_MEMBER_NO);
                customizeNo = (String) retMap.get(Epbmemberson.RETURN_CUSTOMIZE_NO);
//            retMap = getMemberDiscounts(posO2oUrl, posO2oAuth, posO2oAccessToken, memberNo, shopId);
//            if (!Epbmemberson.RETURN_OK.equals(retMap.get(MSG_ID))) {
//                returnMap.put(MSG_ID, (String) retMap.get(MSG_ID));
//                returnMap.put(MSG, (String) retMap.get(MSG));
//                return returnMap;
//            }
            }
            //get vip Discount
            // get parameter
            BigDecimal vipDisc = null;
            sql = "SELECT VIP_DISC FROM POS_VIP_CLASS WHERE CLASS_ID = ? AND (ORG_ID IS NULL OR ORG_ID = ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, classId);
            pstmt.setObject(2, orgId);
            rs = pstmt.executeQuery();
            metaData = (ResultSetMetaData) rs.getMetaData();
            columnCount = metaData.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(columnName);
                    if ("VIP_DISC".equals(columnName.toUpperCase())) {
                        vipDisc = value == null || (value + EMPTY).length() == 0 ? null : new BigDecimal(value + EMPTY);
                    }
                }
            }
            // free mem
            pstmt.close();
            rs.close();
            
            if (vipDisc == null) {
                vipDisc = "B".equals(discFormat) ? new BigDecimal(100) : BigDecimal.ZERO;
            }
//            System.out.println("vipDisc:" + vipDisc);
            
            // update vip information
//            sql = "UPDATE OPENTABLE "
//                    + " SET VIP_ID = ?, VIP_NAME = ?, VIP_PHONE = ?, CLASS_ID = ?, VIP_DISC = ?, CUM_PTS = ?, VIP_PTS_MONEY = ?, CARD_NO = ? "
//                    + " WHERE REC_KEY = ?";
//            pstmt = conn.prepareStatement(sql);
//            pstmt.setObject(1, retCustomerNumber);
//            pstmt.setObject(2, retName);
//            pstmt.setObject(3, retMobile);
//            pstmt.setObject(4, classId);
//            pstmt.setObject(5, vipDisc);
//            pstmt.setObject(6, cumPts);
//            pstmt.setObject(7, fromRate == null || fromRate.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : cumPts.divide(fromRate).multiply(toRate).setScale(2, RoundingMode.DOWN));
//            pstmt.setObject(8, memberNo);
//            pstmt.setObject(9, opentableRecKey);
//            pstmt.execute();

            //调用函数
//            v_opentable_rec_key varchar2,
//    v_vip_id in varchar2,v_vip_name in varchar2,v_class_id in varchar2,v_vip_disc in varchar2,v_cum_pts in varchar2,
//    v_pts in varchar2,v_money in varchar2,
//    v_card_no in varchar2,v_birthday
            CallableStatement stmt = (CallableStatement ) conn.prepareCall("call EP_BISTRO.update_opentable_vip_info(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            stmt.registerOutParameter(1, java.sql.Types.VARCHAR);
            stmt.registerOutParameter(2, java.sql.Types.VARCHAR);
            stmt.setString(3, opentableRecKey + EMPTY);
            stmt.setString(4, customerNumber);
            stmt.setString(5, name);
            stmt.setString(6, classId);
            stmt.setString(7, vipDisc + EMPTY);
            stmt.setString(8, cumPts + EMPTY);
            stmt.setString(9, fromRate + EMPTY);
            stmt.setString(10, toRate + EMPTY);
            stmt.setString(11, memberNo);
            stmt.setString(12, dob);
            stmt.setString(13, epbTypeId);
            stmt.setString(14, customizeNo);
            stmt.execute();
            String strRtn = stmt.getString(1);
            String strMsg = stmt.getString(2);
            if (!RETURN_OK.equals(strRtn)) {
                returnMap.put(MSG_ID, FAIL);   
                returnMap.put(MSG, strMsg);   
                return returnMap;
            }

            returnMap.put(MSG_ID, RETURN_OK);            
            return returnMap;
        } catch (SQLException thr) {
            String msg = "error updateVipSummary:" + thr.getMessage();
            LOG.error(msg, thr);
            returnMap.put(MSG_ID, FAIL);
            returnMap.put(MSG, msg);
            return returnMap;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException thr) {
                // DO NOTHING
                LOG.error("error updateVipSummary 2", thr);
            }
        }
    }
    
    //
    // private
    //
    
    private static String getAuth(final String userName, final String userPassword) {
        try {
            return AES.Encrypt(userName) + COMMA + AES.Encrypt(userPassword);
        } catch (Exception thr) {
            LOG.error("error getAuth", thr);
            return null;
        }
    }
    
//    private static Map<String, String> getToken(final String baseurl, final String userName, final String userPassword, final String callAuth) {
//        final Map<String, String> returnMap = new HashMap<>();
//        try {
//            String callHttpUrl = baseurl + SLASH + "api/user/authenticate";
//            JSONObject jsonBody = new JSONObject();
//            jsonBody.put("UserName", userName);
//            jsonBody.put("Password", userPassword);
//            System.out.println(jsonBody.toString());
//            final Map<String, String> callMap = HttpUtil.callHttpMethod(callHttpUrl, callAuth, null, HttpUtil.POST_METHOD, jsonBody.toString());
//            if (Epbmemberson.RETURN_OK.equals(callMap.get(MSG_ID))) {
//                returnMap.put(MSG_ID, RETURN_OK);
//                returnMap.put(MSG, callMap.get(MSG));
//            } else {
//                returnMap.put(MSG_ID, callMap.get(MSG_ID));
//                returnMap.put(MSG, callMap.get(MSG));
//            }            
//            return returnMap;
//        } catch (JSONException thr) {
//            System.out.println("error getToken:" + thr.getMessage());
//            return returnMap;
//        }
//    }
//    
//    private static Map<String, String> getProfile(final String baseurl, final String callAuth, final String token, final String vipID) {
//        final Map<String, String> returnMap = new HashMap<>();
//        try {
////            Map<String, String> callMap = getToken(baseurl, userName, userPassword, callAuth);
////            String token = null;
////            if (HttpUtil.OK.equals(callMap.get(HttpUtil.MSG_ID))) {
////                token = callMap.get(HttpUtil.MSG);
////            } else {
////                returnMap.put(HttpUtil.MSG_ID, callMap.get(HttpUtil.MSG_ID));
////                returnMap.put(HttpUtil.MSG, callMap.get(HttpUtil.MSG));
////                return returnMap;
////            }            
//            String callHttpUrl = baseurl + SLASH + "api/profile" + SLASH + vipID;
////            JSONObject jsonBody = new JSONObject();
////            jsonBody.put("UserName", userName);
////            jsonBody.put("Password", userPassword);
////            callMap = HttpUtil.callHttpGetMethod(callHttpUrl, callAuth, token);
//            HttpUtil.callHttpGetMethod(callHttpUrl, callAuth, token);
////            if (HttpUtil.OK.equals(callMap.get(HttpUtil.MSG_ID))) {
////                returnMap.put(HttpUtil.MSG_ID, HttpUtil.OK);
////                returnMap.put(HttpUtil.MSG, callMap.get(HttpUtil.MSG));
////            } else {
////                returnMap.put(HttpUtil.MSG_ID, callMap.get(HttpUtil.MSG_ID));
////                returnMap.put(HttpUtil.MSG, callMap.get(HttpUtil.MSG));
////            }     
////            
////            System.out.println("msgId:" + returnMap.get(HttpUtil.MSG_ID));
////            System.out.println("msg:" + returnMap.get(HttpUtil.MSG));
////            HttpUtil.callGetRequest(callHttpUrl, callAuth, token);
//            return returnMap;
//        } catch (Throwable thr) {
//            System.out.println("error getProfile:" + thr.getMessage());
//            return returnMap;
//        }
//    }
    
    // Search Profile:{baseurl}/api/profile/search 
    private static Map<String, Object> searchVip(final Connection conn, final BigDecimal opentableRecKey, final String baseurl, final String callAuth, final String token,
            final String customerNumber, final String cardNumber, final String nric, final String name,
            final String mobileNumberCountryCode, final String mobileNumber, final String emailAddress) {
        final Map<String, Object> returnMap = new HashMap<>();
        try {
            String callHttpUrl = baseurl + SLASH + "api/profile/search";
            JSONArray rtnDataArray = new JSONArray();
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("CustomerNumber", customerNumber == null ? EMPTY : customerNumber);
            jsonBody.put("IsCustomerNumberWildcardSearch", (customerNumber != null && !EMPTY.equals(customerNumber)));
            jsonBody.put("CardNumber", cardNumber == null ? EMPTY : cardNumber);
            jsonBody.put("IsCardNumberWildcardSearch", (cardNumber != null && !EMPTY.equals(cardNumber)));
            jsonBody.put("Nric", nric == null ? EMPTY : nric);
            jsonBody.put("IsNricWildcardSearch", (nric != null && !EMPTY.equals(nric)));
            jsonBody.put("Name", name == null ? EMPTY : name);
            jsonBody.put("IsNameWildcardSearch", (name != null && !EMPTY.equals(name)));
            jsonBody.put("MobileNumberCountryCode", mobileNumberCountryCode == null ? EMPTY : mobileNumberCountryCode);
            jsonBody.put("MobileNumber", mobileNumber == null ? EMPTY : mobileNumber);
            jsonBody.put("IsMobileNumberWildcardSearch", (mobileNumber != null && !EMPTY.equals(mobileNumber)));
            jsonBody.put("EmailAddress", emailAddress == null ? EMPTY : emailAddress);
            jsonBody.put("IsEmailAddressWildcardSearch", (emailAddress != null && !EMPTY.equals(emailAddress)));
//            System.out.println(jsonBody.toString());
            Map<String, String> callMap = HttpUtil.callHttpMethod(callHttpUrl, callAuth, token, HttpUtil.POST_METHOD, jsonBody.toString());
            if (RETURN_OK.equals(callMap.get(MSG_ID))) {
//                returnMap.put(MSG_ID, RETURN_OK);
//                returnMap.put(MSG, callMap.get(MSG));
                JSONArray dataArray = new JSONArray(callMap.get(MSG));
                if (dataArray.length() > 0) {
                    int count = dataArray.length();
                    for (int i = 0; i < count; i++) {
                        JSONObject dataObject = (JSONObject) dataArray.get(i);
                        dataObject.put(RETURN_CUSTOMER_SOURCE, "MEMBERSON");
                        rtnDataArray.put(dataObject);
                    }
                }
//            } else {
//                returnMap.put(MSG_ID, callMap.get(MSG_ID));
//                returnMap.put(MSG, callMap.get(MSG));
//                return returnMap;
            }
            
            List<Map<String, Object>> mapList = getEpbVip(conn, opentableRecKey, customerNumber, name, cardNumber, mobileNumber, emailAddress);
            for (Map<String, Object> map : mapList) {
                JSONObject dataObject = new JSONObject();
                dataObject.put(RETURN_CUSTOMER_NUMBER, map.get(RETURN_CUSTOMER_NUMBER));
//                dataObject.put(RETURN_MEMBER_NO, map.get(RETURN_CUSTOMER_NUMBER));
                dataObject.put(RETURN_NAME, map.get(RETURN_NAME));
                dataObject.put(RETURN_EMAIL_ADDRESS, map.get(RETURN_EMAIL_ADDRESS));
                dataObject.put(RETURN_MOBILE_NUMBER, map.get(RETURN_MOBILE_NUMBER));
                dataObject.put(RETURN_FIRST_NAME, EMPTY);
                dataObject.put(RETURN_LAST_NAME, EMPTY);
                dataObject.put(RETURN_CUSTOMER_SOURCE, "EPB");
                rtnDataArray.put(dataObject);
            }
            
            LOG.info("rtnDataArray:" +rtnDataArray.toString());
            
            if (rtnDataArray.length() > 0) {
                returnMap.put(MSG_ID, RETURN_OK);
                returnMap.put(MSG, rtnDataArray.toString());
            } else {
                returnMap.put(MSG_ID, callMap.get(MSG_ID));
                returnMap.put(MSG, callMap.get(MSG));
            }
            
            return returnMap;

//            JSONObject jsonResult = new JSONObject(callMap.get(HttpUtil.MSG));
//            returnMap.put(RETURN_CUSTOMER_NUMBER, jsonResult.getString(RETURN_CUSTOMER_NUMBER));
//            returnMap.put(RETURN_NAME, jsonResult.getString(RETURN_NAME));
//            returnMap.put(RETURN_MOBILE_NUMBER, jsonResult.getString(RETURN_MOBILE_NUMBER));
            
//            System.out.println("msgId:" + returnMap.get(HttpUtil.MSG_ID));
//            System.out.println("msg:" + returnMap.get(HttpUtil.MSG));
//            HttpUtil.callGetRequest(callHttpUrl, callAuth, token);
            
//            JSONObject jsonResult = new JSONObject(callMap.get(HttpUtil.MSG));
//            JSONArray dataArray = new JSONArray(callMap.get(MSG));
//            List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();
//            Map<String, String> customerMap;
//            String custNo;
//            String custName;
//            String custMobile;
//            String custEmailAddress;
//            String custDob;
//            String custFirstName;
//            String custLastName;
//            String custGenderCode;
//            String custNationalityCode;
//            String custHasActiveMembership;
//            boolean allowMoreRecords
//                    = !(mobileNumber == null || EMPTY.equals(mobileNumber)) || !(emailAddress == null || EMPTY.equals(emailAddress));
//            if (dataArray.length() > 0) {
//                int count = dataArray.length();
//                if (!allowMoreRecords && count > 1) {
//                    returnMap.put(MSG_ID, FAIL);
//                    returnMap.put(MSG, "Multiple records returned, please narrow your search condition");
//                    return returnMap;
//                }
//                for (int i = 0; i < count; i++) {
//                    JSONObject dataObject = (JSONObject) dataArray.get(i);
//                    if (dataObject != null) {
//                        customerMap = new HashMap<>();
//                        custNo = dataObject.optString(RETURN_CUSTOMER_NUMBER);
////                        System.out.println("custNo:" + custNo);
//                        custName = dataObject.optString(RETURN_NAME);
//                        custMobile = dataObject.optString(RETURN_MOBILE_NUMBER);
//                        custEmailAddress = dataObject.optString(RETURN_EMAIL_ADDRESS);
//                        custDob = dataObject.optString(RETURN_DOB);
//                        custFirstName = dataObject.optString(RETURN_FIRST_NAME);
//                        custLastName = dataObject.optString(RETURN_LAST_NAME);
//                        custGenderCode = dataObject.optString(RETURN_GENDER_CODE);
//                        custNationalityCode = dataObject.optString(RETURN_NATIONALITY_CODE);
////                        custHasActiveMembership = dataObject.getString(RETURN_HAS_ACTIVE_MEMBERSHIP);                        
//                        
//                        customerMap.put(RETURN_CUSTOMER_NUMBER, custNo);
//                        customerMap.put(RETURN_NAME, custName);
//                        customerMap.put(RETURN_MOBILE_NUMBER, custMobile);
//                        customerMap.put(RETURN_EMAIL_ADDRESS, custEmailAddress);
//                        customerMap.put(RETURN_DOB, custDob);
//                        customerMap.put(RETURN_FIRST_NAME, custFirstName);
//                        customerMap.put(RETURN_LAST_NAME, custLastName);
//                        customerMap.put(RETURN_GENDER_CODE, custGenderCode);
//                        customerMap.put(RETURN_NATIONALITY_CODE, custNationalityCode);
////                        customerMap.put(RETURN_HAS_ACTIVE_MEMBERSHIP, custHasActiveMembership);
//                        
////                        returnMap.put(RETURN_CUSTOMER_MAP, customerMap);
//                        customerMapList.add(customerMap);
//                    }      
//                    returnMap.put(RETURN_CUSTOMER_MAP_LIST, customerMapList);
//                }
//            } else {
//                returnMap.put(MSG_ID, FAIL);
//                returnMap.put(MSG, "No records returned");
////                return returnMap;
//            }
            
//            return returnMap;
        } catch (JSONException thr) {
            returnMap.put(MSG_ID, FAIL);
            returnMap.put(MSG, "error searchVip:" + thr.getMessage());
//            System.out.println("error searchVip:" + thr.getMessage());
            LOG.error("error searchVip", thr);
            return returnMap;
        }
    }
    
    // 2.3 Get Membership Summary GET /api/profile/00385338/summary HTTP/1.1 
    // get vip points and class
    // validate/active
    // points:balance
    // class:"Tier": "TH Tier21"
    private static Map<String, Object> getVipSummary(final String baseurl, final String callAuth, final String token,
            final String customerNumber) {
        final Map<String, Object> returnMap = new HashMap<>();
        try {           
            String callHttpUrl = baseurl + SLASH + "api/profile" + SLASH + customerNumber + SLASH + "summary";
//            Map<String, String> callMap = getToken(baseurl, "EPBPOS", "8tS20hX1D45S", callAuth);
//            String token2 = callMap.get(HttpUtil.MSG);
            Map<String, String> callMap = HttpUtil.callHttpGetMethod(callHttpUrl, callAuth, token);
            if (RETURN_OK.equals(callMap.get(MSG_ID))) {
                returnMap.put(MSG_ID, RETURN_OK);
                returnMap.put(MSG, callMap.get(MSG));
            } else {
                returnMap.put(MSG_ID, callMap.get(MSG_ID));
                returnMap.put(MSG, callMap.get(MSG));
                return returnMap;
            }     
            
            boolean existExpired = false;
            boolean existActive = false;
            boolean existTerminated = false;  //TERMINATED
//            System.out.println("msg:" + returnMap.get(HttpUtil.MSG));
            JSONObject jsonResult = new JSONObject(callMap.get(MSG));
//            String membershipSummaries = jsonResult.getString("MembershipSummaries");
            JSONArray dataArray = jsonResult.getJSONArray("MembershipSummaries");
            if (dataArray.length() > 0) {
                int count = dataArray.length();
                for (int i = 0; i < count; i++) {
                    JSONObject dataObject = (JSONObject) dataArray.get(i);
                    if (dataObject != null) {
//                        System.out.println("AccountSummaries:" + dataObject.getString("AccountSummaries"));
                        JSONArray accountSummariesArray = dataObject.getJSONArray("AccountSummaries");
                        String balance = null;
                        if (accountSummariesArray.length() > 0) {
                            int asCount = accountSummariesArray.length();
                            for (int k = 0; k < asCount; k++) {
                                JSONObject asDataObject = (JSONObject) accountSummariesArray.get(k);
                                if (asDataObject != null) {
                                    balance = asDataObject.getDouble(RETURN_BALANCE) + EMPTY;
                                }
                            }
                        }
                        
                        JSONArray cardSummariesArray = dataObject.getJSONArray("CardSummaries");
                        String customizeNo = null;
                        if (cardSummariesArray != null && cardSummariesArray.length() > 0) {
                            int asCount = cardSummariesArray.length();
                            for (int k = 0; k < asCount; k++) {
                                JSONObject asDataObject = (JSONObject) cardSummariesArray.get(k);
                                if (asDataObject != null) {
                                    customizeNo = asDataObject.getString(RETURN_CUSTOMIZE_NO);
                                }
                            }
                        }
                        
//                        String tier = dataObject.getString(RETURN_TIER);  // vip class
                        String type = dataObject.getString(RETURN_TYPE);  // vip class
                        String memberNo = dataObject.getString(RETURN_MEMBER_NO);  // vipId
                        String status = dataObject.getString(RETURN_STATUS);  // vip class
                        String expiryDateStr = dataObject.getString(RETURN_EXPIRY_DATE);  // ExpiryDate
                        LOG.info("type:" + type + ", memberNo:" + memberNo + ",status:" + status + ",expiryDateStr:" + expiryDateStr);
                    
                        final Date expiryDate = DATEFORMAT3.parse(expiryDateStr);
                        if (ACTIVE.equals(status) && 
                                (expiryDateStr == null || expiryDateStr.length() == 0 || !expiryDate.before((new Date())))) {
//                            System.out.println("OK");
//                            returnMap.put(RETURN_TIER, tier);
                            returnMap.put(RETURN_TYPE, type);
                            returnMap.put(RETURN_MEMBER_NO, memberNo);
                            returnMap.put(RETURN_CUSTOMIZE_NO, customizeNo);
                            returnMap.put(RETURN_STATUS, status);
                            returnMap.put(RETURN_EXPIRY_DATE, expiryDateStr);
                            returnMap.put(RETURN_BALANCE, balance);
                            existActive = true;
                            break;
//                            final Date expiryDate = DATEFORMAT.parse(expiryDateStr);
//                            if (expiryDate.before((new Date()))) {
//                                System.out.println("expriy");
//                            } else {
//                                System.out.println("valid");
//                            }
                        } else if (EXPIRED.equals(status)) {
                            existExpired = true;
                        } else if (TERMINATED.equals(status)) {
                            existTerminated = true;
                        }                        
                    }
                }
            } else {
                returnMap.put(MSG_ID, FAIL);
                returnMap.put(MSG, "Invalid member");
            }
//            System.out.println("msgId:" + returnMap.get(HttpUtil.MSG_ID));
//            System.out.println("msg:" + returnMap.get(HttpUtil.MSG));
//            HttpUtil.callGetRequest(callHttpUrl, callAuth, token);


            if (!existActive) {
                if (existExpired) {
                    returnMap.put(MSG_ID, FAIL);
                    returnMap.put(MSG, "Expired member");
                } else if (existTerminated) {
                    returnMap.put(MSG_ID, FAIL);
                    returnMap.put(MSG, "Member status is terminated");
                } else {
                    returnMap.put(MSG_ID, FAIL);
                    returnMap.put(MSG, "Invalid member");
                }
            }

            return returnMap;
        } catch (ParseException | JSONException thr) {
            returnMap.put(MSG_ID, FAIL);
            returnMap.put(MSG, "error getVipSummary:" + thr.getMessage());
//            System.out.println("error getVipSummary:" + thr.getMessage());
            LOG.error("error getVipSummary", thr);
            return returnMap;
        }
    }    
    
    //2.10 Get Conversion Rate GET type/conversion-rate 
    //2.11 Redeem Points POST /api//transaction/redeem-point     
    private static Map<String, Object> getRedeemPointsConversionRate(final String baseurl, final String callAuth, final String token, final String homeCurrId) {
        final Map<String, Object> returnMap = new HashMap<>();
        try {           
            String callHttpUrl = baseurl + SLASH + "api/type/conversion-rate";
//            Map<String, String> callMap = getToken(baseurl, "EPBPOS", "8tS20hX1D45S", callAuth);
//            String token2 = callMap.get(HttpUtil.MSG);
            Map<String, String> callMap = HttpUtil.callHttpGetMethod(callHttpUrl, callAuth, token);
            if (RETURN_OK.equals(callMap.get(MSG_ID))) {
                returnMap.put(MSG_ID, RETURN_OK);
                returnMap.put(MSG, callMap.get(MSG));
            } else {
                returnMap.put(MSG_ID, callMap.get(MSG_ID));
                returnMap.put(MSG, callMap.get(MSG));
            }     
            
            LOG.info("msgId:" + returnMap.get(MSG_ID) + ",msg:" + returnMap.get(MSG));
//            HttpUtil.callGetRequest(callHttpUrl, callAuth, token);
            
            JSONArray dataArray = new JSONArray(callMap.get(MSG));
            if (dataArray.length() > 0) {
                int count = dataArray.length();
                for (int i = 0; i < count; i++) {
                    JSONObject dataObject = (JSONObject) dataArray.get(i);
                    if (dataObject != null) {
                        String toCurrency = dataObject.getString("ToCurrency");
                        if (homeCurrId.equals(toCurrency)) {
                            double fromRate = dataObject.getDouble(RETURN_FROM_RATE);
                            double toRate = dataObject.getDouble(RETURN_TO_RATE);
//                            BigDecimal toRateB = new BigDecimal(fromRate/toRate);
                            LOG.info("toRateB:" + toRate + ",fromRateB:" + fromRate);
                            returnMap.put(RETURN_FROM_RATE, new BigDecimal(fromRate));  
                            returnMap.put(RETURN_TO_RATE, new BigDecimal(toRate));                           
                            break;
                        }
                    }
                }
            }
            
            return returnMap;
        } catch (JSONException thr) {
            returnMap.put(MSG_ID, FAIL);
            returnMap.put(MSG, "error getRedeemPointsConversionRate:" + thr.getMessage());
//            System.out.println("error getRedeemPointsConversionRate:" + thr.getMessage());
            LOG.error("error getRedeemPointsConversionRate", thr);
            return returnMap;
        }
    }
    
//    public static Map<String, Object> getVipDiscount(final String orgId, final String locId, final String type) {
//        final Map<String, Object> returnMap = new HashMap<String, Object>();
//        try {
//            List<Object> entities = LocalPersistence.getResultList(
//                    VipSelfmas1.class,
//                    "SELECT * FROM VIP_SELFMAS1 WHERE SELF1_ID = ?",
//                    new Object[]{type});
//            if (entities == null || entities.isEmpty()) {
//                returnMap.put(RETURN_EPB_VIP_CLASS, EMPTY);
//                returnMap.put(RETURN_EPB_VIP_DISC, null);
//                return returnMap;
//            } else {
//                String classId = ((VipSelfmas1) entities.get(0)).getRemark();
//                if (classId == null || classId.length() == 0) {
//                    returnMap.put(RETURN_EPB_VIP_CLASS, EMPTY);
//                    returnMap.put(RETURN_EPB_VIP_DISC, null);
//                    return returnMap;
//                }
//                // free mom
//                entities.clear();
//                
//                entities = LocalPersistence.getResultList(
//                    PosVipClass.class,
//                    "SELECT * FROM POS_VIP_CLASS WHERE CLASS_ID = ? AND (ORG_ID IS NULL OR ORG_ID = '' OR ORG_ID = ?)",
//                    new Object[]{classId, orgId});
//                if (entities == null || entities.isEmpty()) {
//                    returnMap.put(RETURN_EPB_VIP_CLASS, EMPTY);
//                    returnMap.put(RETURN_EPB_VIP_DISC, null);
//                    return returnMap;
//                }
//                returnMap.put(RETURN_EPB_VIP_CLASS, classId);
//                returnMap.put(RETURN_EPB_VIP_DISC, ((PosVipClass) entities.get(0)).getVipDisc());
//                return returnMap;
//            }
//        } catch (Throwable thrl) {
//            returnMap.put(RETURN_EPB_VIP_CLASS, EMPTY);
//            returnMap.put(RETURN_EPB_VIP_DISC, null);
//            return returnMap;
//        }
//    }
    
    @SuppressWarnings("unused")
	private static Map<String, Object> getMemberDiscounts(final String baseurl, final String callAuth, final String token, 
            final String memberNo, final String locId) {
        final Map<String, Object> returnMap = new HashMap<>();
        try {      
            // {"ReceiptNo":"034534599","RedemptionCode":"RD001","MemberNo":"C80006235M","Description":"1 SGD","Amount":100,"PointType":"C21 Points","TransactionDate":"2018-12-07T12:02:00+08:00"}
            String callHttpUrl = baseurl + SLASH + "api/customize/get-member-discounts ";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("MemberNumber", memberNo);
            jsonBody.put("LocationCode", locId);

//            System.out.println(jsonBody.toString());
            Map<String, String> callMap = HttpUtil.callHttpMethod(callHttpUrl, callAuth, token, HttpUtil.POST_METHOD, jsonBody.toString());
            if (RETURN_OK.equals(callMap.get(MSG_ID))) {
                returnMap.put(MSG_ID, RETURN_OK);
                returnMap.put(MSG, callMap.get(MSG));
            } else {
                returnMap.put(MSG_ID, callMap.get(MSG_ID));
                returnMap.put(MSG, callMap.get(MSG));
            }     
            
            LOG.info("msgId:" + returnMap.get(MSG_ID) + ",msg:" + returnMap.get(MSG));
//            HttpUtil.callGetRequest(callHttpUrl, callAuth, token);
            
            JSONArray dataArray = new JSONArray(callMap.get(MSG));
            if (dataArray.length() > 0) {
                int count = dataArray.length();
                for (int i = 0; i < count; i++) {
                    JSONObject dataObject = (JSONObject) dataArray.get(i);
                    if (dataObject != null) {
                        double amount = dataObject.getDouble(RETURN_AMOUNT);
                        String validFromStr = dataObject.optString(RETURN_VALID_FROM);  // ValidFrom
                        String expiryDateStr = dataObject.optString(RETURN_EXPIRY_DATE);  // ExpiryDate
                        final Date validFrom = validFromStr == null || validFromStr.length() == 0 ? null : DATEFORMAT3.parse(validFromStr);
                        final Date expiryDate = expiryDateStr == null || expiryDateStr.length() == 0 ? null : DATEFORMAT3.parse(expiryDateStr);
                        String status = dataObject.getString(RETURN_STATUS);  // Status
                        if (ACTIVE.equals(status) && 
                                (expiryDate == null || !expiryDate.before((new Date())))
                                && validFrom != null && validFrom.before((new Date()))) {
                            BigDecimal vipDisc = new BigDecimal(amount);
                            returnMap.put(RETURN_VIP_DISC, vipDisc);
                            break;
                        }                        
                    }
                }
            }
            
            return returnMap;
        } catch (ParseException | JSONException thr) {
            returnMap.put(MSG_ID, FAIL);
            returnMap.put(MSG, "error redeemPoints:" + thr.getMessage());
//            System.out.println("error redeemPoints:" + thr.getMessage());
            LOG.error("error redeemPoints", thr);
            return returnMap;
        }
    }
    
//    private static String getString(String input) {
//        if (null == input) {
//            return "";
//        }
//        return input;
//    }
    
    // hardcode, max length is 16
    private static String getSmoothType(final String type) {
        if (type == null) {
            return type;
        }
        if (type.length() <= 16) {
            return type;
        }
        return type.substring(0, 16);
    }
    
    private static List<Map<String, Object>> getEpbVip(final Connection conn, final BigDecimal opentableRecKey,final String vipId, final String name, final String cardNo, final String vipPhone, final String email) {
        final List<Map<String, Object>> returnMapList = new ArrayList<Map<String, Object>>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if ((vipId == null || vipId.trim().length() == 0)
                    && (name == null || name.trim().length() == 0)
                    && (cardNo == null || cardNo.trim().length() == 0)
                    && (vipPhone == null || vipPhone.trim().length() == 0)
                    && (email == null || email.trim().length() == 0)) {
                return returnMapList;
            }
            
            String sql = "SELECT ORG_ID FROM OPENTABLE WHERE REC_KEY = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, opentableRecKey);
            rs = pstmt.executeQuery();
            ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String epbOrgId = EMPTY;

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(columnName);
                    if ("ORG_ID".equals(columnName.toUpperCase())) {
                        epbOrgId = (String) value;
                    } 
                }
            }
            // free mem
            pstmt.close();
            rs.close();


            // get parameter
            sql = "SELECT VIP_ID, NAME, VIP_PHONE1, EMAIL_ADDR FROM POS_VIP_MAS "
                    + " WHERE CLASS_ID IN (SELECT CLASS_ID FROM POS_VIP_CLASS WHERE REMARK LIKE ? AND (ORG_ID IS NULL OR ORG_ID = '' OR ORG_ID = ?)) "
                    + " AND (ORG_ID IS NULL OR ORG_ID = '' OR ORG_ID = ?)"
                    + (vipId == null || vipId.length() == 0 ? EMPTY : " AND LOWER(VIP_ID) = LOWER('" + vipId + "') ")
                    + (name == null || name.length() == 0 ? EMPTY : " AND LOWER(NAME) LIKE LOWER('%" + name + "%') ")
                    + (cardNo == null || cardNo.length() == 0 ? EMPTY : " AND LOWER(CARD_NO) LIKE LOWER('%" + cardNo + "%') ")
                    + (vipPhone == null || vipPhone.length() == 0 ? EMPTY : " AND VIP_PHONE1 = '" + vipPhone + "' ")
                    + (email == null || email.length() == 0 ? EMPTY : " AND LOWER(EMAIL_ADDR) = LOWER('" + email + "') ");
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, "CULINA%");
            pstmt.setObject(2, epbOrgId);
            pstmt.setObject(3, epbOrgId);
            rs = pstmt.executeQuery();
            metaData = (ResultSetMetaData) rs.getMetaData();
            columnCount = metaData.getColumnCount();
            String epbVipId = EMPTY;
            String epbName = EMPTY;
            String epbMobile = EMPTY;
            String epbEmailAddr = EMPTY;

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(columnName);
                    if ("VIP_ID".equals(columnName.toUpperCase())) {
                        epbVipId = (String) value;
                    } else if ("NAME".equals(columnName.toUpperCase())) {
                        epbName = (String) value;
                    } else if ("VIP_PHONE1".equals(columnName.toUpperCase())) {
                        epbMobile = (String) value;
                    } else if ("EMAIL_ADDR".equals(columnName.toUpperCase())) {
                        epbEmailAddr = (String) value;
                    }
                }
                final Map<String, Object> returnMap = new HashMap<String, Object>();
                returnMap.put(RETURN_CUSTOMER_NUMBER, epbVipId);
                returnMap.put(RETURN_MEMBER_NO, epbVipId);
                returnMap.put(RETURN_NAME, epbName);
                returnMap.put(RETURN_MOBILE_NUMBER, epbMobile);
                returnMap.put(RETURN_EMAIL_ADDRESS, epbEmailAddr);
                returnMapList.add(returnMap);
            }
            // free mem
            pstmt.close();
            rs.close();

            return returnMapList;
        } catch (SQLException thr) {
            LOG.error(thr);
            // DO NOTHING
            return returnMapList;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Throwable thr) {
                // do nothing
                LOG.error(thr);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        try {
//            if (1 == 1) {
//                JSONObject jsonBody = new JSONObject();
//            }
////            if (1 == 1) {
//////                System.out.println("now1:" + DATEFORMAT.format(new Date()));
//////                System.out.println("now2:" + DATEFORMAT2.format(new Date()));
////                System.out.println("now1:" + getDateFormatString(new Date()));
////                System.out.println("now2:" + getDateFormat2String(new Date()));
////                final Date expiryDate = DATEFORMAT3.parse("2019-08-31T23:59:59+08:00");
////                return;
////            }
////            String baseurl = "http://company.memgate.com";
////            String auth = "VGVzdEN1c3RvbWVy, VGVzdFBhc3N3b3Jk";
////            String userId = "testuser";
////            String userPasswrod = "testpassword";
            String baseurl = "https://c21sg.memgate.com";
//            String auth = "RVBCUE9T,OHRTMjBoWDFENDVT";
            String userId = "EPBPOS";
            String userPasswrod = "YfG79vCr5TZ1";
            String auth = AES.Encrypt(userId) + "," + AES.Encrypt(userPasswrod);    
            
            String driver = "oracle.jdbc.driver.OracleDriver";
            String url = "jdbc:oracle:thin:@localhost:1523:XE";
//            String url = "jdbc:oracle:thin:@192.168.1.11:1521:ORCL";
            String user = "EPBSH";
            String pwd = "EPB9209";
//            String pwd = "EPBSH";
            Class.forName(driver);
            System.out.println("driver is ok");

            Connection conn = DriverManager.getConnection(url, user, pwd);
            System.out.println(auth);
//            Map<String, String> returnMapping = getToken(baseurl, userId, userPasswrod, auth);
            String token = "b24a3a48-86c6-45c7-b15d-e4560ceed775";//returnMapping.get(HttpUtil.MSG);
//            getProfile(baseurl, userId, userPasswrod, auth, "123");
//            getProfile(baseurl, auth, token, "01463571");
            searchVip(conn, BigDecimal.ZERO, baseurl, auth, token, EMPTY, EMPTY, EMPTY, "%CHEN%", EMPTY, EMPTY, EMPTY);
//            getVipSummary(baseurl, auth, token, "01523935");
////            final List<PoslineMemberson> poslineMembersonList = new ArrayList<PoslineMemberson>();
////            PoslineMemberson poslineMemberson = new PoslineMemberson();
////            poslineMemberson.setItemCode("PUR01");
////            poslineMemberson.setCurrency("SGD");
////            poslineMemberson.setAmount(new BigDecimal(100));
////            poslineMembersonList.add(poslineMemberson);
////            final List<PospayMemberson> pospayMembersonList = new ArrayList<PospayMemberson>();
////            PospayMemberson pospayMemberson = new PospayMemberson();
////            pospayMemberson.setPaymentType("CASH");
////            pospayMemberson.setCurrency("SGD");
////            pospayMemberson.setAmount(new BigDecimal(100));
////            pospayMembersonList.add(pospayMemberson);
////            
////            updatePoints(baseurl, auth, token, 
////            "C80006235M", new Date(), "LHQS",  "01",  "SCC",  
////            "This is EPB test",  "123", poslineMembersonList, pospayMembersonList);
////            
////            getRedeemPointsConversionRate(baseurl, auth, token, "SGD");
////            
////            redeemPoints(baseurl, auth, token, "C80006235M", 100, "C21 Points", new Date(), "RD001", "1 SGD", "HQ", "034534599");
////            getMemberDiscounts(baseurl, auth, token, "CT9001178M", "HQ");
////            01523935
////            final Map<String, String> returnMap = Epbmemberson.getVip(conn, BigDecimal.ZERO, "01523935", EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY);
//            Map<String, String> returnMap = Epbmemberson.getVip(conn, BigDecimal.ZERO, "01528863", EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY);
//            if (Epbmemberson.RETURN_OK.equals(returnMap.get(Epbmemberson.MSG_ID))) {
//                // printer OK
//                System.out.println("call memberson API OK");
//            } else {
//                // error
//                System.out.println(returnMap.get(Epbmemberson.MSG));
//            }
//            
//            returnMap = Epbmemberson.updateVipSummary(conn, BigDecimal.ZERO, "01528863", "SA FINA HE", "13420944473", "", "");
//            if (Epbmemberson.RETURN_OK.equals(returnMap.get(Epbmemberson.MSG_ID))) {
//                // printer OK
//                System.out.println("call memberson update API OK");
//            } else {
//                // error
//                System.out.println(returnMap.get(Epbmemberson.MSG));
//            }
            
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex);
        }
    }
    
}
