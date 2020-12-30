/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.woo;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author sim_liang
 */
public class EpbApi {
    //
    // fields
    //
    public static final String RETURN_CODE = "code";
    public static final String RETURN_DESC = "desc";
    public static final String RETURN_DATA = "data";
    public static final String RETURN_OK = "000";
    public static final String RETURN_FAIL_001 = "001";  // invalid coupon NO
    public static final String RETURN_FAIL_002 = "002";  // invalid date range
    public static final String RETURN_FAIL_003 = "003";  // invalid status flag
    public static final String RETURN_FAIL_004 = "004";  // invalid vip ID
    public static final String RETURN_FAIL_998 = "Failed to exec Oracle procedure";  // oracle procedure error
    public static final String RETURN_FAIL_999 = "999";  // Unhandle exception
    
    private static final Log LOG = LogFactory.getLog(EpbApi.class);
    private static final String OK = "OK";
    private static final String EMPTY = "";
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    public static final java.text.SimpleDateFormat DATEFORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Epb Coupon verification
     *
     * @param conn      Connection
     * @param orgID     String
     * @param vipID     String
     * @param vipPhone1 String
     * @param shopId    String
     * @param docDate   Date
     * @param couponNo  String
     * @return          String, json format
     * if return code is 000, it is meaning check OK, desc is EMPTY
     * else desc is error message
     * for example
     * {"code":"000",
    "desc":"",
    "data":{
        "expiryDate":"2020-12-31",
        "csId":"cs001",
        "svId":"s001",
        "md5Code":"md5001",
        "svtypeId":"sv001",
        "startDate":"2020-10-01",
        "svAmt":100
    }}
     */ 
    public synchronized String checkEpbCoupon(final Connection conn, final String orgID, final String vipID, final String vipPhone1, final String shopId, final Date docDate, final String couponNo) {        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
//            String sql = "SELECT SV_ID, MD5_CODE, SVTYPE_ID, CS_ID, START_DATE, START_TIME, EXPIRY_DATE, EXPIRY_TIME, SV_AMT, TOTAL_AMT, DISCOUNT, PHONE FROM SVMAS WHERE SV_ID = ? AND (ORG_ID IS NULL OR ORG_ID = ?)";
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT SV_ID, MD5_CODE, STATUS_FLG, SVTYPE_ID, CS_ID, START_DATE, START_TIME, EXPIRY_DATE, EXPIRY_TIME, SV_AMT, TOTAL_AMT, DISCOUNT, PHONE ");
            sb.append(" FROM SVMAS WHERE SV_ID = '");
            sb.append(couponNo);
            sb.append("' AND (ORG_ID IS NULL OR ORG_ID = '");
            sb.append(orgID);
            sb.append("')");
            pstmt = conn.prepareStatement(sb.toString());
            rs = pstmt.executeQuery();
            ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String svId = null;
            String md5Code = null;
            String svtypeId = null;
            String csId = null;
            Date startDate = null;
            String startTime = null;
            Date expiryDate = null;
            String expiryTime = null;
            BigDecimal totalAmt = null;
            BigDecimal discount = null;
            BigDecimal svAmt = null;
            String phone = null;
            String statusFlg = null;
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(columnName);
                    if (null != columnName.toUpperCase()) switch (columnName.toUpperCase()) {
                        case "SV_ID":
                            svId = (String) value;
                            break;
                        case "MD5_CODE":
                            md5Code = (String) value;
                            break;
                        case "STATUS_FLG":
                            statusFlg = value == null ? EMPTY : value + EMPTY;
                            break;
                        case "SVTYPE_ID":
                            svtypeId = (String) value;
                            break;
                        case "CS_ID":
                            csId = (String) value;
                            break;
                        case "START_DATE":
                            startDate = value == null ? null : (Date) value;
                            break;
                        case "START_TIME":
                            startTime = (String) value;
                            break;
                        case "EXPIRY_DATE":
                            expiryDate = value == null ? null : (Date) value;
                            break;
                        case "EXPIRY_TIME":
                            expiryTime = (String) value;
                            break;
                        case "SV_AMT":
                            svAmt = value == null ? ZERO : new BigDecimal(value + EMPTY);
                            break;
                        case "TOTAL_AMT":
                            totalAmt = value == null ? ZERO : new BigDecimal(value + EMPTY);
                            break;
                        case "DISCOUNT":
                            discount = value == null ? ZERO : new BigDecimal(value + EMPTY);
                            break;
                        case "PHONE":
                            phone = (String) value;
                            break;
                        default:
                            break;
                    } 
                }
            }
            if (svId == null || svId.isEmpty()) {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put(RETURN_CODE, RETURN_FAIL_001);
                jsonBody.put(RETURN_DESC, "Invalid Coupon NO");
                return jsonBody.toString();
            }
            if (startDate != null) {
                if (docDate.before(startDate)) {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put(RETURN_CODE, RETURN_FAIL_002);
                    jsonBody.put(RETURN_DESC, "Start date is " + (DATEFORMAT.format(startDate)));
                    return jsonBody.toString();                
                }
            }
            if (expiryDate != null) {
                if (docDate.after(expiryDate)) {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put(RETURN_CODE, RETURN_FAIL_002);
                    jsonBody.put(RETURN_DESC, "Expire date is " + (DATEFORMAT.format(expiryDate)));
                    return jsonBody.toString();                
                }
            }
            if (!"B".equals(statusFlg)) {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put(RETURN_CODE, RETURN_FAIL_003);
                jsonBody.put(RETURN_DESC, "Invalid status flag");
                return jsonBody.toString();
            }
            if (vipID == null || vipID.length() == 0) {
                if (csId != null && csId.length() != 0) {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put(RETURN_CODE, RETURN_FAIL_004);
                    jsonBody.put(RETURN_DESC, "Vip ID does not match");
                    return jsonBody.toString();
                }
            }
            if (vipID != null && vipID.length() != 0
                    && csId != null && csId.length() != 0
                    && !vipID.equals(csId)) {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put(RETURN_CODE, RETURN_FAIL_004);
                jsonBody.put(RETURN_DESC, "Vip ID does not match");
                return jsonBody.toString();
            }
            JSONObject jsonBody = new JSONObject();
            jsonBody.put(RETURN_CODE, RETURN_OK);
            jsonBody.put(RETURN_DESC, "");
            JSONObject dataJsonBody = new JSONObject();
            dataJsonBody.put("svId", svId);
            if (md5Code != null && md5Code.length() != 0) {
                dataJsonBody.put("md5Code", md5Code);
            }
            if (svtypeId != null && svtypeId.length() != 0) {
                dataJsonBody.put("svtypeId", svtypeId);
            }
            if (csId != null && csId.length() != 0) {
                dataJsonBody.put("csId", csId);
            }
            if (startDate != null) {
                dataJsonBody.put("startDate", DATEFORMAT.format(startDate));
            }
            if (startTime != null && startTime.length() != 0) {
                dataJsonBody.put("startTime", startTime);
            }
            if (expiryDate != null) {
                dataJsonBody.put("expiryDate", DATEFORMAT.format(expiryDate));
            }
            if (expiryTime != null && expiryTime.length() != 0) {
                dataJsonBody.put("expiryTime", expiryTime);
            }
            if (totalAmt != null) {
                dataJsonBody.put("totalAmt", totalAmt);
            }
            if (discount != null) {
                dataJsonBody.put("discount", discount);
            }
            if (svAmt != null) {
                dataJsonBody.put("svAmt", svAmt);
            }
            if (phone != null && phone.length() != 0) {
                dataJsonBody.put("phone", phone);
            }
            if (statusFlg != null && statusFlg.length() != 0) {
                dataJsonBody.put("statusFlg", statusFlg);
            }
            jsonBody.put(RETURN_DATA, dataJsonBody);
            return jsonBody.toString();
        } catch (SQLException | JSONException ex) {
            LOG.error("Failed to checkEpbCoupon", ex);
            return "{\"code\":\"999\",\"desc\":\"Unhandle exception\"}";
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LOG.error("Failed to checkEpbCoupon", ex);
            }
        }
    }
    
    /**
     * Epb Coupon query
     *
     * @param conn      Connection
     * @param couponNo  String
     * @return          String, json format
     * if return code is 000, it is meaning check OK, desc is EMPTY
     * else desc is error message
     * for example
     * {"code":"000",
    "desc":"",
    "data":{
        "expiryDate":"2020-12-31",
        "csId":"cs001",
        "svId":"s001",
        "md5Code":"md5001",
        "svtypeId":"sv001",
        "startDate":"2020-10-01",
        "svAmt":100
    }}
     */ 
    public synchronized String queryEpbCoupon(final Connection conn, final String couponNo) {        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
//            String sql = "SELECT SV_ID, MD5_CODE, SVTYPE_ID, CS_ID, START_DATE, START_TIME, EXPIRY_DATE, EXPIRY_TIME, SV_AMT, TOTAL_AMT, DISCOUNT, PHONE FROM SVMAS WHERE SV_ID = ? AND (ORG_ID IS NULL OR ORG_ID = ?)";
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ORG_ID, SV_ID, MD5_CODE, STATUS_FLG, SVTYPE_ID, CS_ID, START_DATE, START_TIME, EXPIRY_DATE, EXPIRY_TIME, SV_AMT, TOTAL_AMT, DISCOUNT, PHONE, USED_DATE, USED_USER_ID, USED_SHOP_ID, REMARK ");
            sb.append(" FROM SVMAS WHERE SV_ID = '");
            sb.append(couponNo);
            sb.append("'");
            pstmt = conn.prepareStatement(sb.toString());
            rs = pstmt.executeQuery();
            ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String orgId = null;
            String svId = null;
            String md5Code = null;
            String svtypeId = null;
            String csId = null;
            Date startDate = null;
            String startTime = null;
            Date expiryDate = null;
            String expiryTime = null;
            BigDecimal totalAmt = null;
            BigDecimal discount = null;
            BigDecimal svAmt = null;
            String phone = null;
            String statusFlg = null;
            Date usedDate = null;
            String usedUserId = null;
            String usedShopId = null;
            String remark = null;
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(columnName);
                    if (null != columnName.toUpperCase()) switch (columnName.toUpperCase()) {
                        case "ORG_ID":
                            orgId = (String) value;
                            break;
                        case "SV_ID":
                            svId = (String) value;
                            break;
                        case "MD5_CODE":
                            md5Code = (String) value;
                            break;
                        case "STATUS_FLG":
                            statusFlg = value == null ? EMPTY : value + EMPTY;
                            break;
                        case "SVTYPE_ID":
                            svtypeId = (String) value;
                            break;
                        case "CS_ID":
                            csId = (String) value;
                            break;
                        case "START_DATE":
                            startDate = value == null ? null : (Date) value;
                            break;
                        case "START_TIME":
                            startTime = (String) value;
                            break;
                        case "EXPIRY_DATE":
                            expiryDate = value == null ? null : (Date) value;
                            break;
                        case "EXPIRY_TIME":
                            expiryTime = (String) value;
                            break;
                        case "SV_AMT":
                            svAmt = value == null ? ZERO : new BigDecimal(value + EMPTY);
                            break;
                        case "TOTAL_AMT":
                            totalAmt = value == null ? ZERO : new BigDecimal(value + EMPTY);
                            break;
                        case "DISCOUNT":
                            discount = value == null ? ZERO : new BigDecimal(value + EMPTY);
                            break;
                        case "PHONE":
                            phone = (String) value;
                            break;
                        case "USED_DATE":
                            usedDate = value == null ? null : (Date) value;
                            break;
                        case "USED_USER_ID":
                            usedUserId = (String) value;
                            break;
                        case "USED_SHOP_ID":
                            usedShopId = (String) value;
                            break;
                        case "REMARK":
                            remark = (String) value;
                            break;    
                        default:
                            break;
                    } 
                }
            }
            if (svId == null || svId.isEmpty()) {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put(RETURN_CODE, RETURN_FAIL_001);
                jsonBody.put(RETURN_DESC, "Invalid Coupon NO");
                return jsonBody.toString();
            }
            JSONObject jsonBody = new JSONObject();
            jsonBody.put(RETURN_CODE, RETURN_OK);
            jsonBody.put(RETURN_DESC, "");
            JSONObject dataJsonBody = new JSONObject();
            dataJsonBody.put("svId", svId);
            dataJsonBody.put("orgId", orgId == null ? EMPTY : orgId);
            if (md5Code != null && md5Code.length() != 0) {
                dataJsonBody.put("md5Code", md5Code);
            }
            if (svtypeId != null && svtypeId.length() != 0) {
                dataJsonBody.put("svtypeId", svtypeId);
            }
            if (csId != null && csId.length() != 0) {
                dataJsonBody.put("csId", csId);
            }
            if (startDate != null) {
                dataJsonBody.put("startDate", DATEFORMAT.format(startDate));
            }
            if (startTime != null && startTime.length() != 0) {
                dataJsonBody.put("startTime", startTime);
            }
            if (expiryDate != null) {
                dataJsonBody.put("expiryDate", DATEFORMAT.format(expiryDate));
            }
            if (expiryTime != null && expiryTime.length() != 0) {
                dataJsonBody.put("expiryTime", expiryTime);
            }
            if (totalAmt != null) {
                dataJsonBody.put("totalAmt", totalAmt);
            }
            if (discount != null) {
                dataJsonBody.put("discount", discount);
            }
            if (svAmt != null) {
                dataJsonBody.put("svAmt", svAmt);
            }
            if (phone != null && phone.length() != 0) {
                dataJsonBody.put("phone", phone);
            }
            if (statusFlg != null && statusFlg.length() != 0) {
                dataJsonBody.put("statusFlg", statusFlg);
            }
            if (usedDate != null) {
                dataJsonBody.put("usedDate", DATEFORMAT.format(usedDate));
            }
            if (usedUserId != null && usedUserId.length() != 0) {
                dataJsonBody.put("usedUserId", usedUserId);
            }
            if (usedShopId != null && usedShopId.length() != 0) {
                dataJsonBody.put("usedShopId", usedShopId);
            }
            if (remark != null && remark.length() != 0) {
                dataJsonBody.put("remark", remark);
            }
            jsonBody.put(RETURN_DATA, dataJsonBody);
            return jsonBody.toString();
        } catch (SQLException | JSONException ex) {
            LOG.error("Failed to queryEpbCoupon", ex);
            return "{\"code\":\"999\",\"desc\":\"Unhandle exception\"}";
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LOG.error("Failed to queryEpbCoupon", ex);
            }
        }
    }
    
    /**
     * Epb Coupon verification
     *
     * @param conn         Connection
     * @param orgID        String
     * @param vipID        String
     * @param vipPhone1    String
     * @param shopId       String
     * @param docId        String
     * @param docDate      Date
     * @param userId       String
     * @param couponNoList String, format is json, for example
     * [{"couponNo":"c001","svAmt":100},{"couponNo":"c002","svAmt":180}]
     * @return             String, json format
     * if return code is 000, it is meaning check OK, desc is EMPTY
     * else desc is error message
     * for example
     * {"code":"000","desc":""}
     */ 
    public synchronized String useEpbCoupon(final Connection conn, final String orgID, final String vipID, final String vipPhone1, final String shopId, final String docId, final Date docDate, final String userId, final String couponNoList) {
        CallableStatement stmt = null;
        try {
            String param;
            String couponNo;
            LOG.info("vipID:" + vipID + ",docId:" + docId);
            LOG.info("couponNoList:" + couponNoList);
            JSONArray dataArray = new JSONArray(couponNoList);
            if (dataArray.length() != 0) {
                int count = dataArray.length();
                for (int i = 0; i < count; i++) {
                    JSONObject dataObject = (JSONObject) dataArray.get(i);
                    if (dataObject != null) {
                        couponNo = dataObject.optString("couponNo");
                        if (couponNo != null && couponNo.length() != 0) {
                            param = "vipID^=^" + vipID
                                    + "^couponNo^=^" + couponNo
                                    + "^docId^=^" + docId
                                    + "^docDate^=^" + DATEFORMAT.format(docDate);
                            stmt = (CallableStatement) conn.prepareCall("call EP_TRANSUTL.common_ws_action(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                            stmt.registerOutParameter(1, java.sql.Types.VARCHAR);
                            stmt.registerOutParameter(2, java.sql.Types.VARCHAR);
                            stmt.setString(3, "eng");
                            stmt.setString(4, "useEpbCoupon");
                            stmt.setString(5, "EPBDEVICE");
                            stmt.setString(6, "0");
                            stmt.setString(7, orgID);
                            stmt.setString(8, shopId);
                            stmt.setString(9, userId);
                            stmt.setString(10, param);
                            stmt.execute();
                            String strRtn = stmt.getString(1);
                            String strMsg = stmt.getString(2);
                            if (!OK.equals(strRtn)) {
                                JSONObject jsonBody = new JSONObject();
                                jsonBody.put(RETURN_CODE, RETURN_FAIL_998);
                                jsonBody.put(RETURN_DESC, strMsg);
                                return jsonBody.toString();
                            }
                        }
                    }
                }
            }
            
            JSONObject jsonBody = new JSONObject();
            jsonBody.put(RETURN_CODE, RETURN_OK);
            jsonBody.put(RETURN_DESC, "");
            return jsonBody.toString();
        } catch (SQLException | JSONException ex) {
            LOG.error("Failed to useEpbCoupon", ex);
            return "{\"code\":\"999\",\"desc\":\"Unhandle exception\"}";
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                LOG.error("Failed to useEpbCoupon", ex);
            }
        }
    }
    
}
