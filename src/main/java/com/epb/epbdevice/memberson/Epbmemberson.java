/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.memberson;

import com.epb.epbdevice.utl.CommonUtility;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    public static final String MSG_ID = "msgId";
    public static final String MSG = "msg";
    public static final String RETURN_OK = "OK";
    public static final String FAIL = "FAIL";    
    public static final String RETURN_CUSTOMER_MAP = "CUSTOMERMAP";
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
    public static final String RETURN_TIER = "Tier";
    public static final String RETURN_MEMBER_NO = "MemberNo";
    public static final String RETURN_STATUS = "Status";
    public static final String RETURN_VALID_FROM = "ValidFrom";
    public static final String RETURN_EXPIRY_DATE = "ExpiryDate";
    public static final String RETURN_BALANCE = "Balance";
    public static final String RETURN_FROM_RATE = "FromRate";
    public static final String RETURN_TO_RATE = "ToRate";
    public static final String RETURN_AMOUNT = "Amount";
    public static final String RETURN_VIP_DISC = "VipDiscount";
    
//    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
////    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//    private static final SimpleDateFormat DATEFORMAT2 = new SimpleDateFormat("yyyy-MM-dd 'T'HH:mm:ssZ");
    private static final SimpleDateFormat DATEFORMAT3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    public static Map<String, String> getVip(final Connection conn,
            final BigDecimal recKey, final String shopId, final String homeCurrId,
            final String customerNumber, final String cardNumber, final String nric, final String name,
            final String mobileNumberCountryCode, final String mobileNumber, final String emailAddress) {
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
                if (null != setId) switch (setId) {
                    case "POSO2OCONT":
                        posO2oCont = setString;
                        break;
                    case "POSO2OURL":
                        posO2oUrl = setString;
                        break;
                    case "POSO2OTOKEN":
                        posO2oAccessToken = setString;
                        break;
                    case "POSO2OVENDOR":
                        posO2oVendor = setString;
                        break;
                    case "POSO2OAPPKEY":
                        posO2oAppKey = setString;
                        break;
                    case "POSO2OSECRET":
                        posO2oAppSecret = setString;
                        break;
                    case "DiscFormat":
                        discFormat = setString;
                        break;
                    default:
                        break;
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
            Map<String, Object> retMap = Epbmemberson.getRedeemPointsConversionRate(posO2oUrl, posO2oAuth, posO2oAccessToken, homeCurrId);
            if (!Epbmemberson.RETURN_OK.equals(retMap.get(MSG_ID))) {
                returnMap.put(MSG_ID, (String) retMap.get(MSG_ID));
                returnMap.put(MSG, (String) retMap.get(MSG));
                return returnMap;
            }
//            BigDecimal posO2oRedeemRatio = retMap.containsKey(Epbmemberson.RETURN_TO_RATE) ? (BigDecimal) retMap.get(Epbmemberson.RETURN_TO_RATE) : null;
            BigDecimal fromRate = retMap.containsKey(Epbmemberson.RETURN_FROM_RATE) ? (BigDecimal) retMap.get(Epbmemberson.RETURN_FROM_RATE) : null;
            BigDecimal toRate = retMap.containsKey(Epbmemberson.RETURN_TO_RATE) ? (BigDecimal) retMap.get(Epbmemberson.RETURN_TO_RATE) : null;
            retMap = searchVip(posO2oUrl, posO2oAuth, posO2oAccessToken, customerNumber, cardNumber, nric, name, mobileNumberCountryCode, mobileNumber, emailAddress);
            if (!Epbmemberson.RETURN_OK.equals(retMap.get(MSG_ID))) {
                returnMap.put(MSG_ID, (String) retMap.get(MSG_ID));
                returnMap.put(MSG, (String) retMap.get(MSG));
                return returnMap;
            }
            Map<String, String> customerMap = (Map<String, String>) retMap.get(Epbmemberson.RETURN_CUSTOMER_MAP);
            String retCustomerNumber = customerMap.get(Epbmemberson.RETURN_CUSTOMER_NUMBER);
            String retName = customerMap.get(Epbmemberson.RETURN_NAME);
            String retMobile = customerMap.get(Epbmemberson.RETURN_MOBILE_NUMBER);
            retMap = getVipSummary(posO2oUrl, posO2oAuth, posO2oAccessToken, retCustomerNumber);
            if (!Epbmemberson.RETURN_OK.equals(retMap.get(MSG_ID))) {
                returnMap.put(MSG_ID, (String) retMap.get(MSG_ID));
                returnMap.put(MSG, (String) retMap.get(MSG));
                return returnMap;
            }
            String classId = (String) retMap.get(Epbmemberson.RETURN_TIER);
            BigDecimal cumPts = retMap.get(Epbmemberson.RETURN_BALANCE) == null || EMPTY.equals(retMap.get(Epbmemberson.RETURN_BALANCE))
                    ? BigDecimal.ZERO 
                    : new BigDecimal((String) retMap.get(Epbmemberson.RETURN_BALANCE));
            String memberNo = (String) retMap.get(Epbmemberson.RETURN_MEMBER_NO);
            retMap = getMemberDiscounts(posO2oUrl, posO2oAuth, posO2oAccessToken, memberNo, shopId);
            if (!Epbmemberson.RETURN_OK.equals(retMap.get(MSG_ID))) {
                returnMap.put(MSG_ID, (String) retMap.get(MSG_ID));
                returnMap.put(MSG, (String) retMap.get(MSG));
                return returnMap;
            }
            BigDecimal vipDisc = (BigDecimal) retMap.get(RETURN_VIP_DISC);
            if (vipDisc == null) {
                vipDisc = "B".equals(discFormat) ? new BigDecimal(100) : BigDecimal.ZERO;
            }
            System.out.println("vipDisc:" + vipDisc);
            
            // update vip information
            sql = "UPDATE OPENTABLE SET VIP_ID = ?, VIP_NAME = ?, VIP_PHONE = ?, CLASS_ID = ?, VIP_DISC = ?, CUM_PTS = ?, VIP_PTS_MONEY = ? WHERE REC_KEY = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, retCustomerNumber);
            pstmt.setObject(2, retName);
            pstmt.setObject(3, retMobile);
            pstmt.setObject(4, classId);
            pstmt.setObject(5, vipDisc);
            pstmt.setObject(6, cumPts);
            pstmt.setObject(7, fromRate == null || fromRate.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : cumPts.divide(fromRate).multiply(toRate).setScale(2, RoundingMode.DOWN));
            pstmt.setObject(8, recKey);
            boolean done = pstmt.execute();
            if (!done) {
                returnMap.put(MSG_ID, FAIL);
                returnMap.put(MSG, "error updating VIP information");
                return returnMap;
            }
            
            return returnMap;
        } catch (SQLException thr) {
            String msg = "error getVip:" + thr.getMessage();
            System.out.println(msg);
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
            System.out.println("error getAuth:" + thr.getMessage());
            return null;
        }
    }
    
    private static Map<String, String> getToken(final String baseurl, final String userName, final String userPassword, final String callAuth) {
        final Map<String, String> returnMap = new HashMap<>();
        try {
            String callHttpUrl = baseurl + SLASH + "api/user/authenticate";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("UserName", userName);
            jsonBody.put("Password", userPassword);
            System.out.println(jsonBody.toString());
            final Map<String, String> callMap = HttpUtil.callHttpMethod(callHttpUrl, callAuth, null, HttpUtil.POST_METHOD, jsonBody.toString());
            if (Epbmemberson.RETURN_OK.equals(callMap.get(MSG_ID))) {
                returnMap.put(MSG_ID, RETURN_OK);
                returnMap.put(MSG, callMap.get(MSG));
            } else {
                returnMap.put(MSG_ID, callMap.get(MSG_ID));
                returnMap.put(MSG, callMap.get(MSG));
            }            
            return returnMap;
        } catch (JSONException thr) {
            System.out.println("error getToken:" + thr.getMessage());
            return returnMap;
        }
    }
    
    private static Map<String, String> getProfile(final String baseurl, final String callAuth, final String token, final String vipID) {
        final Map<String, String> returnMap = new HashMap<>();
        try {
//            Map<String, String> callMap = getToken(baseurl, userName, userPassword, callAuth);
//            String token = null;
//            if (HttpUtil.OK.equals(callMap.get(HttpUtil.MSG_ID))) {
//                token = callMap.get(HttpUtil.MSG);
//            } else {
//                returnMap.put(HttpUtil.MSG_ID, callMap.get(HttpUtil.MSG_ID));
//                returnMap.put(HttpUtil.MSG, callMap.get(HttpUtil.MSG));
//                return returnMap;
//            }            
            String callHttpUrl = baseurl + SLASH + "api/profile" + SLASH + vipID;
//            JSONObject jsonBody = new JSONObject();
//            jsonBody.put("UserName", userName);
//            jsonBody.put("Password", userPassword);
//            callMap = HttpUtil.callHttpGetMethod(callHttpUrl, callAuth, token);
            HttpUtil.callHttpGetMethod(callHttpUrl, callAuth, token);
//            if (HttpUtil.OK.equals(callMap.get(HttpUtil.MSG_ID))) {
//                returnMap.put(HttpUtil.MSG_ID, HttpUtil.OK);
//                returnMap.put(HttpUtil.MSG, callMap.get(HttpUtil.MSG));
//            } else {
//                returnMap.put(HttpUtil.MSG_ID, callMap.get(HttpUtil.MSG_ID));
//                returnMap.put(HttpUtil.MSG, callMap.get(HttpUtil.MSG));
//            }     
//            
//            System.out.println("msgId:" + returnMap.get(HttpUtil.MSG_ID));
//            System.out.println("msg:" + returnMap.get(HttpUtil.MSG));
//            HttpUtil.callGetRequest(callHttpUrl, callAuth, token);
            return returnMap;
        } catch (Throwable thr) {
            System.out.println("error getProfile:" + thr.getMessage());
            return returnMap;
        }
    }
    
    // Search Profile:{baseurl}/api/profile/search 
    private static Map<String, Object> searchVip(final String baseurl, final String callAuth, final String token,
            final String customerNumber, final String cardNumber, final String nric, final String name,
            final String mobileNumberCountryCode, final String mobileNumber, final String emailAddress) {
        final Map<String, Object> returnMap = new HashMap<>();
        try {
            String callHttpUrl = baseurl + SLASH + "api/profile/search";
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
                returnMap.put(MSG_ID, RETURN_OK);
                returnMap.put(MSG, callMap.get(MSG));
            } else {
                returnMap.put(MSG_ID, callMap.get(MSG_ID));
                returnMap.put(MSG, callMap.get(MSG));
                return returnMap;
            }

//            JSONObject jsonResult = new JSONObject(callMap.get(HttpUtil.MSG));
//            returnMap.put(RETURN_CUSTOMER_NUMBER, jsonResult.getString(RETURN_CUSTOMER_NUMBER));
//            returnMap.put(RETURN_NAME, jsonResult.getString(RETURN_NAME));
//            returnMap.put(RETURN_MOBILE_NUMBER, jsonResult.getString(RETURN_MOBILE_NUMBER));
            
//            System.out.println("msgId:" + returnMap.get(HttpUtil.MSG_ID));
//            System.out.println("msg:" + returnMap.get(HttpUtil.MSG));
//            HttpUtil.callGetRequest(callHttpUrl, callAuth, token);
            
//            JSONObject jsonResult = new JSONObject(callMap.get(HttpUtil.MSG));
            JSONArray dataArray = new JSONArray(callMap.get(MSG));
            Map<String, String> customerMap;
            String custNo;
            String custName;
            String custMobile;
            String custEmailAddress;
            String custDob;
            String custFirstName;
            String custLastName;
            String custGenderCode;
            String custNationalityCode;
            String custHasActiveMembership;
            if (dataArray.length() > 0) {
                int count = dataArray.length();
                for (int i = 0; i < count; i++) {
                    JSONObject dataObject = (JSONObject) dataArray.get(i);
                    if (dataObject != null) {
                        customerMap = new HashMap<>();
                        custNo = dataObject.getString(RETURN_CUSTOMER_NUMBER);
                        custName = dataObject.getString(RETURN_NAME);
                        custMobile = dataObject.getString(RETURN_MOBILE_NUMBER);
                        custEmailAddress = dataObject.getString(RETURN_EMAIL_ADDRESS);
                        custDob = dataObject.getString(RETURN_DOB);
                        custFirstName = dataObject.getString(RETURN_FIRST_NAME);
                        custLastName = dataObject.getString(RETURN_LAST_NAME);
                        custGenderCode = dataObject.getString(RETURN_GENDER_CODE);
                        custNationalityCode = dataObject.getString(RETURN_NATIONALITY_CODE);
//                        custHasActiveMembership = dataObject.getString(RETURN_HAS_ACTIVE_MEMBERSHIP);                        
                        
                        customerMap.put(RETURN_CUSTOMER_NUMBER, custNo);
                        customerMap.put(RETURN_NAME, custName);
                        customerMap.put(RETURN_MOBILE_NUMBER, custMobile);
                        customerMap.put(RETURN_EMAIL_ADDRESS, custEmailAddress);
                        customerMap.put(RETURN_DOB, custDob);
                        customerMap.put(RETURN_FIRST_NAME, custFirstName);
                        customerMap.put(RETURN_LAST_NAME, custLastName);
                        customerMap.put(RETURN_GENDER_CODE, custGenderCode);
                        customerMap.put(RETURN_NATIONALITY_CODE, custNationalityCode);
//                        customerMap.put(RETURN_HAS_ACTIVE_MEMBERSHIP, custHasActiveMembership);
                        
                        returnMap.put(RETURN_CUSTOMER_MAP, customerMap);
                    }
                }
            } else {
                returnMap.put(MSG_ID, FAIL);
                returnMap.put(MSG, "Invalid VIP");
            }
            
            return returnMap;
        } catch (JSONException thr) {
            returnMap.put(MSG_ID, FAIL);
            returnMap.put(MSG, "error searchVip:" + thr.getMessage());
//            System.out.println("error searchVip:" + thr.getMessage());
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
                        String tier = dataObject.getString(RETURN_TIER);  // vip class
                        String memberNo = dataObject.getString(RETURN_MEMBER_NO);  // vipId
                        String status = dataObject.getString(RETURN_STATUS);  // vip class
                        String expiryDateStr = dataObject.getString(RETURN_EXPIRY_DATE);  // ExpiryDate
                        System.out.println("tier:" + tier);
                        System.out.println("memberNo:" + memberNo);
                        System.out.println("status:" + status);
                        System.out.println("expiryDateStr:" + expiryDateStr);
                    
                        final Date expiryDate = DATEFORMAT3.parse(expiryDateStr);
                        if (expiryDate.before((new Date()))) {
                            System.out.println("expriy");
                        } else {
                            System.out.println("valid");
                        }
                        if (ACTIVE.equals(status)
                                && (expiryDateStr == null || expiryDateStr.length() == 0 || !expiryDate.before((new Date())))) {
                            System.out.println("OK");
                            returnMap.put(RETURN_TIER, tier);
                            returnMap.put(RETURN_MEMBER_NO, memberNo);
                            returnMap.put(RETURN_STATUS, status);
                            returnMap.put(RETURN_EXPIRY_DATE, expiryDateStr);
                            returnMap.put(RETURN_BALANCE, balance);
                            break;
//                            final Date expiryDate = DATEFORMAT.parse(expiryDateStr);
//                            if (expiryDate.before((new Date()))) {
//                                System.out.println("expriy");
//                            } else {
//                                System.out.println("valid");
//                            }
                        }                        
                    }
                }
            } else {
                returnMap.put(MSG_ID, FAIL);
                returnMap.put(MSG, "Invalid VIP");
            }
//            System.out.println("msgId:" + returnMap.get(HttpUtil.MSG_ID));
//            System.out.println("msg:" + returnMap.get(HttpUtil.MSG));
//            HttpUtil.callGetRequest(callHttpUrl, callAuth, token);
            return returnMap;
        } catch (ParseException | JSONException thr) {
            returnMap.put(MSG_ID, FAIL);
            returnMap.put(MSG, "error getVipSummary:" + thr.getMessage());
//            System.out.println("error getVipSummary:" + thr.getMessage());
            return returnMap;
        }
    }    
    
    //2.10 Get Conversion Rate GET type/conversion-rate 
    //2.11 Redeem Points POST /api//transaction/redeem-point     
    public static Map<String, Object> getRedeemPointsConversionRate(final String baseurl, final String callAuth, final String token, final String homeCurrId) {
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
            
            System.out.println("msgId:" + returnMap.get(MSG_ID));
            System.out.println("msg:" + returnMap.get(MSG));
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
//                            System.out.println("toRateB:" + toRateB);
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
            return returnMap;
        }
    }
    
    public static Map<String, Object> getMemberDiscounts(final String baseurl, final String callAuth, final String token, 
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
            
            System.out.println("msgId:" + returnMap.get(MSG_ID));
            System.out.println("msg:" + returnMap.get(MSG));
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
                        if (ACTIVE.equals(status)
                                && (expiryDate == null || !expiryDate.before((new Date())))
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
            return returnMap;
        }
    }
    
    //
    // private
    //
    
    private static String getString(String input) {
        if (null == input) {
            return "";
        }
        return input;
    }
    
    public static void main(String[] args) {
        try {
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
//            String baseurl = "https://c21sguat.memgate.com";
////            String auth = "RVBCUE9T,OHRTMjBoWDFENDVT";
//            String userId = "EPBPOS";
//            String userPasswrod = "8tS20hX1D45S";
//            String auth = AES.Encrypt(userId) + "," + AES.Encrypt(userPasswrod);      
//            System.out.println(auth);
////            Map<String, String> returnMapping = getToken(baseurl, userId, userPasswrod, auth);
//            String token = "f9e96fd1-c6c5-4a36-9e02-6dc355b22d4f";//returnMapping.get(HttpUtil.MSG);
////            getProfile(baseurl, userId, userPasswrod, auth, "123");
////            getProfile(baseurl, auth, token, "01463571");
//            searchVip(baseurl, auth, "1668bec1-7f80-4528-90bf-51eb3b3a4d4b", "01463571", EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY);
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
//            getMemberDiscounts(baseurl, auth, token, "CT9001178M", "HQ");
            String driver = "oracle.jdbc.driver.OracleDriver";
            String url = "jdbc:oracle:thin:@localhost:1523:XE";
            String user = "EPBSH";
            String pwd = "EPB9209";
            Class.forName(driver);
            System.out.println("driver is ok");

            Connection conn = DriverManager.getConnection(url, user, pwd);
            
            
            final Map<String, String> returnMap = Epbmemberson.getVip(conn, BigDecimal.ZERO, "HQ", "SGD", "01523935", EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY);
            if (Epbmemberson.RETURN_OK.equals(returnMap.get(Epbmemberson.MSG_ID))) {
                // printer OK
            } else {
                // error
                System.out.println(returnMap.get(Epbmemberson.MSG));
            }
            
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex);
        }
    }
    
}