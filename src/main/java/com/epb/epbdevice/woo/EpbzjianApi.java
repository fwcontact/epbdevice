/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.woo;

//import com.epb.pst.entity.EpDept;
import com.epb.epbdevice.beans.CrmCoupon;
import com.epb.epbdevice.beans.CrmPosVipInfo;
import com.epb.epbdevice.beans.CrmPosline;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
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


public class EpbzjianApi {
    //system setting
//    public static final String appKey = "test";    // AppKey
//    public static final String appSecret = "test"; // AppSecret
//    public static final String accessToken = "www.zjian.net";
//    public static final String url = "http://open.zjian.net/rest";
    
    public static final String RETURN_CODE = "code";
    public static final String RETURN_DESC = "desc";
    public static final String RETURN_OK = "000";
    public static final String RETURN_SALE_AMT = "sale_amt";
    public static final String RETURN_DIS_AMT = "dis_amt";
    public static final String RETURN_CODE_UNKOWNREASON = "unkown reaseon";
    public static final String RETURN_RETURN_API_RET_EMPTY = "FAIL:API return is empty";
    public static final String PM_ID_ZJIAN = "Daijq";
    public static final java.text.SimpleDateFormat DATETIME_FORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final java.text.SimpleDateFormat DATEFORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd");
    
    private static final Log LOG = LogFactory.getLog(HttpUtil.class);
    private static final String JSON_FORMAT = "json";
    private static final String API_VERSION = "2.0";
    private static final String RETURN_DATA = "data";
    private static final String EMPTY = "";
    private static final String STRING_NULL = "null";
    private static final String UTF8 = "UTF-8";
    private static final String ZJIANCRM = "zjian.crm.";
    private static final String ZJ_MALE = "10";
    private static final String ZJ_FEMALE = "20";
    private static final Character MALE = 'M';
    private static final Character FEMALE = 'F';
    
    private static String crmAppKey;
    private static String crmAppSecret;
    private static String crmAccessToken;
    private static String crmUrl;  
    
    // public    
    
    /**
     * @param appKey String, system setting:POSO2OAPPKEY
     * @param appSecret String, system setting:POSO2OSECRET
     * @param accessToken String, system setting:POSO2OTOKEN
     * @param url String, system setting:POSO2OURL
     */
    public EpbzjianApi(final String appKey, final String appSecret, final String accessToken, final String url) {
        crmAppKey = appKey;
        crmAppSecret = appSecret;
        crmAccessToken = accessToken;
        crmUrl = url;
    }   
    
    /**
     * Search VIP by phone no
     *
     * @param vipPhone1 String phone no
     * @return CrmPosVipInfo include VIP ID,name,class ID,vip phone1,gender,VIP points,etc
     */
    public synchronized CrmPosVipInfo searchVip(String vipPhone1) {        
        try {
            if (vipPhone1 == null || vipPhone1.isEmpty() || vipPhone1.length() < 6) {
                return null;
            }
            
            String sMessage;                
            String timestamp = getTimestamp();
            String method = "zjian.crm.customer.get";
            String apiKey = method.replace(ZJIANCRM, EMPTY);
            String token = getTaken(crmAppKey, crmAppSecret, crmAccessToken, JSON_FORMAT, method, timestamp);
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("telephone_no", vipPhone1);
//            String jsonStr = null; //jsonObject.toString();    
//            System.out.println("jsonStr:" + jsonStr);
            final Map<String, String> mapping = new HashMap<String, String>();
            mapping.put("telephone_no", vipPhone1);
//            String parameters = getParameter(mapping);
            String parameters = getPara(mapping);
//            parameters = "&" + "telephone_no" + "=" + URLEncoder.encode(vipPhone1);
//            String fullUrl = getFullUrl(url + apiKey, token, timestamp, apiVersion, appKey, accessToken, method, format, parameters);
            String fullUrl = getFullUrl(crmUrl + apiKey, token, timestamp, API_VERSION, crmAppKey, crmAccessToken, method, JSON_FORMAT);
            LOG.info("fullUrl:" + fullUrl);
            LOG.info("parameters:" + parameters);
//                    + "&v=2.0&app_key=test&access_token=www.zjian.net&method=zjian.crm.dept.post&format=json";
//            String returnStr = sendHttpPost(fullUrl, null);
            String returnStr = HttpUtil.postMap(fullUrl, mapping, UTF8);

            if (returnStr == null || EMPTY.equals(returnStr)) {
                sMessage = RETURN_RETURN_API_RET_EMPTY;
                LOG.info(sMessage);
                return null;
            }
            LOG.info(returnStr);
            JSONObject jsonResult = new JSONObject(returnStr);
            String result = getString(jsonResult.getString(RETURN_CODE));
//            String reason = getString(jsonResult.getString(RETURN_DESC));

            if (!RETURN_OK.equals(result)) {
//                sMessage = "FAIL:" + result + ":" + reason;
                return null;
            }

            JSONObject dataJson = jsonResult.getJSONObject(RETURN_DATA);
//            JSONObject dataJson = new JSONObject(getString(jsonResult.getString(RETURN_DATA)));
//            JSONObject bodyJson = jsonResult.getJSONObject(RETURN_DATA);
            String customerNo = getString(dataJson.getString("customer_no"));
            String customerName = getString(dataJson.getString("customer_name"));
            String customerGradeNo = getString(dataJson.getString("customer_grade_no"));
//            try {
//                customerGradeNo = CFunction.getString(dataJson.getString("customer_grade_no"));
//            } catch (Throwable throwable) {
//                customerGradeNo = customerGradeNo == null || customerGradeNo.isEmpty() || STRING_NULL.equals(customerGradeNo) ? "1" : customerGradeNo;
//            }
//            String customerGradeName = CFunction.getString(dataJson.getString("customer_grade_name"));
            String gender = getString(dataJson.getString("gender"));
            String birthday = getString(dataJson.getString("birthday"));
            String telephoneNo = getString(dataJson.getString("telephone_no"));
            String belongSiteNo = getString(dataJson.getString("belong_site_no"));
            String belongAgentNo = getString(dataJson.getString("belong_agent_no"));
            final CrmPosVipInfo posVipInfo = new CrmPosVipInfo();
            posVipInfo.setVipId(customerNo);
            posVipInfo.setName(customerName);
            posVipInfo.setClassId(customerGradeNo);
            posVipInfo.setGender(getGender(gender));
            posVipInfo.setBirthDate(birthday == null || birthday.length() == 0 || STRING_NULL.equals(birthday) ? null : DATEFORMAT.parse(birthday));
            posVipInfo.setVipPhone1(telephoneNo);
            posVipInfo.setLocId(belongSiteNo == null || STRING_NULL.equals(belongSiteNo) ? EMPTY : belongSiteNo);
            posVipInfo.setEmpId(belongAgentNo == null || STRING_NULL.equals(belongAgentNo) ? EMPTY : belongAgentNo);
            BigDecimal points = getVipPoints(customerNo, telephoneNo);
            posVipInfo.setPoints(points == null ? BigDecimal.ZERO : points);
            return posVipInfo;
        } catch (ParseException | JSONException ex) {
            LOG.error("Failed to searchVip", ex);
            return null;
        }
    }
      
    /**
     * Search VIP by dyn code
     *
     * @param dynCode String dynamic code
     * @return CrmPosVipInfo include VIP ID,name,class ID,vip phone1,gender,VIP points,etc
     */
    public synchronized CrmPosVipInfo searchVipByDynCode(String dynCode) {        
        try {
            if (dynCode == null || dynCode.isEmpty() || dynCode.length() < 6) {
                return null;
            }
                            
            String sMessage;
            final Map<String, String> mapping = new HashMap<String, String>();
            mapping.put("dyno", dynCode);
//            String parameters = getParameter(mapping);
            String parameters = getPara(mapping);
//            parameters = "&" + "telephone_no" + "=" + URLEncoder.encode(vipPhone1);
//            String fullUrl = getFullUrl(url + apiKey, token, timestamp, apiVersion, appKey, accessToken, method, format, parameters);
            String fullUrl = "http://wx.shanghaiwoo.com/w/client/getCustomerByDyno";
            LOG.info("fullUrl:" + fullUrl);
            LOG.info("parameters:" + parameters);
//                    + "&v=2.0&app_key=test&access_token=www.zjian.net&method=zjian.crm.dept.post&format=json";
//            String returnStr = sendHttpPost(fullUrl, null);
            String returnStr = HttpUtil.postMap(fullUrl, mapping, UTF8);

            if (returnStr == null || EMPTY.equals(returnStr)) {
                sMessage = RETURN_RETURN_API_RET_EMPTY;
                LOG.info(sMessage);
                return null;
            }
            LOG.info(returnStr);
//            returnStr = "{\"code\":\"000\",\"desc\":null,\"data\":{\"customer_no\":\"702250\",\"customer_name\":\"123\",\"grade_no\":\"30\",\"grade_name\":\"银卡会员\",\"point\":50,\"gender\":\"女\",\"birthday\":\"1993-05-05 \",\"telephone_no\":\"15110228610\",\"belong_site_no\":\"036\",\"belong_agent_no\":null}}";
            JSONObject jsonResult = new JSONObject(returnStr);
            String result = getString(jsonResult.getString(RETURN_CODE));
//            String reason = getString(jsonResult.getString(RETURN_DESC));

            if (!RETURN_OK.equals(result)) {
//                sMessage = "FAIL:" + result + ":" + reason;
//                System.out.println(sMessage);
                return null;
            }

            JSONObject dataJson = jsonResult.getJSONObject(RETURN_DATA);
//            JSONObject dataJson = new JSONObject(getString(jsonResult.getString(RETURN_DATA)));
////            JSONObject bodyJson = jsonResult.getJSONObject(RETURN_DATA);
            String customerNo = getString(dataJson.getString("customer_no"));
            String customerName = getString(dataJson.getString("customer_name"));
            String customerGradeNo = getString(dataJson.getString("grade_no"));
            String gender = getString(dataJson.getString("gender"));
            String birthday = getString(dataJson.getString("birthday"));
            String telephoneNo = getString(dataJson.getString("telephone_no"));
            String belongSiteNo = getString(dataJson.getString("belong_site_no"));
            String belongAgentNo = getString(dataJson.getString("belong_agent_no"));
            final CrmPosVipInfo posVipInfo = new CrmPosVipInfo();
            posVipInfo.setVipId(customerNo);
            posVipInfo.setName(customerName);
            posVipInfo.setClassId(customerGradeNo);
            posVipInfo.setGender(getGender(gender));
            posVipInfo.setBirthDate(birthday == null || birthday.length() == 0 || STRING_NULL.equals(birthday) ? null : DATEFORMAT.parse(birthday));
            posVipInfo.setVipPhone1(telephoneNo);
            posVipInfo.setLocId(belongSiteNo == null || STRING_NULL.equals(belongSiteNo) ? EMPTY : belongSiteNo);
            posVipInfo.setEmpId(belongAgentNo == null || STRING_NULL.equals(belongAgentNo) ? EMPTY : belongAgentNo);
            BigDecimal points = getVipPoints(customerNo, telephoneNo);
            posVipInfo.setPoints(points == null ? BigDecimal.ZERO : points);
            return posVipInfo;
        } catch (ParseException | JSONException ex) {
            LOG.error("Failed to searchVipByDynCode", ex);
            return null;
        }
    }
    
    /**
     * Search VIP points
     *
     * @param vipID  String, VIP ID
     * @param vipPhone1 String, phone no
     * @return vip points
     */
    public synchronized BigDecimal getVipPoints(final String vipID, final String vipPhone1) {       
        try {
            if (vipPhone1 == null || vipPhone1.isEmpty()) {
                return null;
            }
                            
            String sMessage;
            String timestamp = getTimestamp();
            String method = "zjian.crm.customer.account.get";
            String apiKey = method.replace(ZJIANCRM, EMPTY);
            String token = getTaken(crmAppKey, crmAppSecret, crmAccessToken, JSON_FORMAT, method, timestamp);
//            JSONObject jsonObject = new JSONObject();
////            jsonObject.put("customer_no", vipID);
//            jsonObject.put("telephone_no", vipPhone1);
//            jsonObject.put("account_type", "10");  // 10:point; 20:prepaid card amount
////            jsonObject.put("password", password);
//            String jsonStr = jsonObject.toString();    
//            System.out.println("jsonStr:" + jsonStr);
            
            final Map<String, String> mapping = new HashMap<String, String>();
            mapping.put("customer_no", vipID);
            mapping.put("telephone_no", vipPhone1);
            mapping.put("account_type", "10");
//            mapping.put("password", password);
            String parameters = getPara(mapping);            

//            String fullUrl = getFullUrl(url + apiKey, token, timestamp, apiVersion, appKey, accessToken, method, format, parameters);   
            String fullUrl = getFullUrl(crmUrl + apiKey, token, timestamp, API_VERSION, crmAppKey, crmAccessToken, method, JSON_FORMAT);
            LOG.info("fullUrl:" + fullUrl);
            LOG.info("parameters:" + parameters);
//                    + "&v=2.0&app_key=test&access_token=www.zjian.net&method=zjian.crm.dept.post&format=json";
//            String returnStr = sendHttpPost(fullUrl, null);
            String returnStr = HttpUtil.postMap(fullUrl, mapping, UTF8);

            if (returnStr == null || EMPTY.equals(returnStr)) {
                sMessage = RETURN_RETURN_API_RET_EMPTY;
                LOG.info(sMessage);
                return null;
            }
            
            LOG.info(returnStr);
            JSONObject jsonResult = new JSONObject(returnStr);
            String result = getString(jsonResult.getString(RETURN_CODE));
//            String reason = getString(jsonResult.getString(RETURN_DESC));

            if (!RETURN_OK.equals(result)) {
//                sMessage = "FAIL:" + result + ":" + reason;
//                System.out.println(sMessage);
                return null;
            }

//            JSONObject dataJson = new JSONObject(getString(jsonResult.getString(RETURN_DATA)));
            JSONObject dataJson = jsonResult.getJSONObject(RETURN_DATA);
//            String customerNo = CFunction.getString(bodyJson.getString("customer_no"));
//            String customerName = CFunction.getString(bodyJson.getString("customer_name"));
//            String accountType = CFunction.getString(bodyJson.getString("account_type"));
            String availableAmt = getString(dataJson.getString("available_amt"));
            return availableAmt == null || availableAmt.length() == 0 
                    ? BigDecimal.ZERO 
                    : new BigDecimal(availableAmt);
        } catch (JSONException ex) {
            LOG.error("Failed to getVipPoints", ex);
            return null;
        }
    }
    
    public synchronized boolean useVipPoints(final String vipID, final String vipPhone1, final BigDecimal useAmt, final String docId, final Date useDate) {       
        try {
            if (vipPhone1 == null || vipPhone1.isEmpty()) {
                return false;
            }
            
            if (useAmt == null || BigDecimal.ZERO.compareTo(useAmt) == 0) {
                return false;
            }
                            
            String sMessage;
            String timestamp = getTimestamp();
            String method = "zjian.crm.customer.account.use";
            String apiKey = method.replace(ZJIANCRM, EMPTY);
            String token = getTaken(crmAppKey, crmAppSecret, crmAccessToken, JSON_FORMAT, method, timestamp);
//            JSONObject jsonObject = new JSONObject();
////            jsonObject.put("customer_no", vipID);
//            jsonObject.put("telephone_no", vipPhone1);
//            jsonObject.put("account_type", "10");  // 10:point; 20:prepaid card amount
////            jsonObject.put("password", password);
//            jsonObject.put("use_amt", useAmt.toString());
//            jsonObject.put("trade_no", docId == null ? "" : docId);
//            jsonObject.put("use_date", useDate == null ? "" : dateFormat.format(useDate));
//            String jsonStr = jsonObject.toString();    
//            System.out.println("jsonStr:" + jsonStr);
            
            final Map<String, String> mapping = new HashMap<String, String>();
            mapping.put("customer_no", vipID);
            mapping.put("telephone_no", vipPhone1);
            mapping.put("account_type", "10");
//            mapping.put("password", password);
            mapping.put("use_amt", useAmt.toString());
            mapping.put("trade_no", docId == null ? "" : docId);
            mapping.put("use_date", useDate == null ? "" : DATEFORMAT.format(useDate));
            String parameters = getPara(mapping);   

//            String fullUrl = getFullUrl(url + apiKey, token, timestamp, apiVersion, appKey, accessToken, method, format, parameters);   
            String fullUrl = getFullUrl(crmUrl + apiKey, token, timestamp, API_VERSION, crmAppKey, crmAccessToken, method, JSON_FORMAT); 
            LOG.info("fullUrl:" + fullUrl);
            LOG.info("parameters:" + parameters);
//                    + "&v=2.0&app_key=test&access_token=www.zjian.net&method=zjian.crm.dept.post&format=json";
//            String returnStr = sendHttpPost(fullUrl, null);
            String returnStr = HttpUtil.postMap(fullUrl, mapping, UTF8);

            if (returnStr == null || EMPTY.equals(returnStr)) {
                sMessage = RETURN_RETURN_API_RET_EMPTY;
                LOG.info(sMessage);
                return false;
            }
            LOG.info(returnStr);
            
            JSONObject jsonResult = new JSONObject(returnStr);
            String result = getString(jsonResult.getString(RETURN_CODE));
//            String reason = getString(jsonResult.getString(RETURN_DESC));

            if (!RETURN_OK.equals(result)) {
//                sMessage = "FAIL:" + result + ":" + reason;
//                System.out.println(sMessage);
                return false;
            }

            return true;
        } catch (JSONException ex) {
            LOG.error("Failed to useVipPoints", ex);
            return false;
        }
    }
    
//    public List<CrmCoupon> searchCoupons(String vipID, String vipPhone1, String pageNo, int pageSize) {
//        try {
//            if (vipPhone1 == null || vipPhone1.isEmpty()) {
//                return null;
//            } 
//            
//            String sMessage;
//            String timestamp = getTimestamp();
//            String method = "zjian.crm.customer.coupons.get";
//            String apiKey = method.replace(ZJIANCRM, EMPTY);
//            String token = getTaken(crmAppKey, crmAppSecret, crmAccessToken, format, method, timestamp);
////            JSONObject jsonObject = new JSONObject();
////            jsonObject.put("telephone_no", vipPhone1);
////            String jsonStr = null; //jsonObject.toString();    
////            System.out.println("jsonStr:" + jsonStr);
//            final Map<String, String> mapping = new HashMap<String, String>();
//            mapping.put("customer_no", vipID);
//            mapping.put("telephone_no", vipPhone1);
//            mapping.put("page_no", pageNo);
//            mapping.put("page_size", pageSize + "");
//            String parameters = getPara(mapping);
////            parameters = "&" + "telephone_no" + "=" + URLEncoder.encode(vipPhone1);
////            String fullUrl = getFullUrl(url + apiKey, token, timestamp, apiVersion, appKey, accessToken, method, format, parameters);
//            String fullUrl = getFullUrl(crmUrl + apiKey, token, timestamp, apiVersion, crmAppKey, crmAccessToken, method, format);
//            LOG.info("fullUrl:" + fullUrl);
//            LOG.info("parameters:" + parameters);
////                    + "&v=2.0&app_key=test&access_token=www.zjian.net&method=zjian.crm.dept.post&format=json";
////            String returnStr = sendHttpPost(fullUrl + parameters, null);
//            String returnStr = HttpUtil.postMap(fullUrl, mapping, UTF8);
//
//            if (returnStr == null || EMPTY.equals(returnStr)) {
//                sMessage = RETURN_RETURN_API_RET_EMPTY;
//                LOG.info(sMessage);
//                return null;
//            }
//            
//            LOG.info(returnStr);
//            
//            JSONObject jsonResult = new JSONObject(returnStr);
//            String result = getString(jsonResult.getString(RETURN_CODE));
////            String reason = getString(jsonResult.getString(RETURN_DESC));
//
//            if (!RETURN_OK.equals(result)) {
////                sMessage = "FAIL:" + result + ":" + reason;
////                System.out.println(sMessage);
//                return null;
//            }
//            
////            JSONObject bodyJson = jsonResult.getJSONObject(RETURN_DATA);
////            String customerNo = CFunction.getString(dataArray.getString("customer_no"));
////            String customerName = CFunction.getString(dataArray.getString("customer_name"));
////            String customerGradeName = CFunction.getString(dataArray.getString("customer_grade_name"));
////            String gender = CFunction.getString(dataArray.getString("gender"));
////            String birthday = CFunction.getString(dataArray.getString("birthday"));
////            String telephone_no = CFunction.getString(dataArray.getString("telephone_no"));
//            
//            final List<CrmCoupon> coupons = new ArrayList<CrmCoupon>();
//            JSONArray dataArray = new JSONArray(getString(jsonResult.getString(RETURN_DATA)));
//            if (dataArray != null && dataArray.length() > 0) {
//                int count = dataArray.length();
//                for (int i = 0; i < count; i++) {
//                    JSONObject dataObject = (JSONObject) dataArray.get(i);
//                    if (dataObject != null) {
//                        final CrmCoupon coupon = new CrmCoupon();
//                        coupon.setCouponId(getString(dataObject.getString("coupon_id")));
//                        coupon.setCouponId(getString(dataObject.getString("coupon_no")));
//                        coupon.setCouponId(getString(dataObject.getString("start_date")));
//                        coupon.setCouponId(getString(dataObject.getString("end_date")));
//                        coupon.setCouponId(getString(dataObject.getString("sale_amt")));
//                        coupon.setCouponId(getString(dataObject.getString("dis_amt")));
//                        coupon.setCouponId(getString(dataObject.getString("product_name")));
//                        coupon.setCouponId(getString(dataObject.getString("remark")));
//                        coupon.setCouponId(getString(dataObject.getString("product_type")));
//                        coupons.add(coupon);
//                    }
//                }
//            }
//            
//            return coupons;
//        } catch (Exception ex) {
//            LOG.error("Failed to searchCoupons", ex);
//            return null;
//        }
//    }
    
    /**
     * Coupon verification
     *
     * @param vipID String
     * @param vipPhone1 String
     * @param couponNo String
     * @param shopId String
     * @return Map<String, String>
     * if check OK, code = OK, desc is EMPTY, sales_amt is sales amt,dis amt is discount amt
     * else code != OK, desc is error message
     */    
    public synchronized Map<String, String> checkCoupon(final String vipID, final String vipPhone1, final String couponNo, final String shopId) {        
        final Map<String, String> returnMapping = new HashMap<String, String>();
        try {
            if (vipPhone1 == null || vipPhone1.isEmpty()) {
                return null;
            }

            String sMessage;
            String timestamp = getTimestamp();
            String method = "zjian.crm.customer.coupon.check";
            String apiKey = method.replace(ZJIANCRM, EMPTY);
            String token = getTaken(crmAppKey, crmAppSecret, crmAccessToken, JSON_FORMAT, method, timestamp);

            final Map<String, String> mapping = new HashMap<String, String>();
            mapping.put("coupon_no", couponNo);
            mapping.put("customer_no", vipID);
            mapping.put("telephone_no", vipPhone1);
            mapping.put("site_no", shopId);
            String parameters = getPara(mapping);

//            String fullUrl = getFullUrl(url + apiKey, token, timestamp, apiVersion, appKey, accessToken, method, format, parameters);
            String fullUrl = getFullUrl(crmUrl + apiKey, token, timestamp, API_VERSION, crmAppKey, crmAccessToken, method, JSON_FORMAT);
            LOG.info("fullUrl:" + fullUrl);
            LOG.info("parameters:" + parameters);
//                    + "&v=2.0&app_key=test&access_token=www.zjian.net&method=zjian.crm.dept.post&format=json";
//            String returnStr = sendHttpPost(fullUrl, null);
            String returnStr = HttpUtil.postMap(fullUrl, mapping, UTF8);

            if (returnStr == null || EMPTY.equals(returnStr)) {
                sMessage = RETURN_RETURN_API_RET_EMPTY;
                LOG.info(sMessage);
                returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
                returnMapping.put(RETURN_DESC, sMessage);
                return returnMapping;
            }
            
            LOG.info(returnStr);
            
            JSONObject jsonResult = new JSONObject(returnStr);
            String result = getString(jsonResult.getString(RETURN_CODE));
            String reason = getString(jsonResult.getString(RETURN_DESC));

            if (!RETURN_OK.equals(result)) {
                returnMapping.put(RETURN_CODE, result);
                returnMapping.put(RETURN_DESC, reason);
//                sMessage = "FAIL:" + result + ":" + reason;
//                System.out.println(sMessage);
                return returnMapping;
            }

//            JSONObject dataJson = new JSONObject(getString(jsonResult.getString(RETURN_DATA)));
            JSONObject dataJson = jsonResult.getJSONObject(RETURN_DATA);
            String saleAmt = getString(dataJson.getString(RETURN_SALE_AMT));
            String disAmt = getString(dataJson.getString(RETURN_DIS_AMT));
            returnMapping.put(RETURN_CODE, result);            
            returnMapping.put(RETURN_DESC, reason);            
//            returnMapping.put(RETURN_DESC, CFunction.getCouponReturnMsg(result));
            returnMapping.put(RETURN_SALE_AMT, saleAmt);
            returnMapping.put(RETURN_DIS_AMT, disAmt);
            return returnMapping;

        } catch (JSONException ex) {
            String sMessage = "FAIL:" + ex.toString();
            LOG.error("Failed to checkCoupon", ex);
            returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
            returnMapping.put(RETURN_DESC, sMessage);
            return returnMapping;
        }
    }
    
    /**
     * offset coupon
     *
     * @param docId String, pos doc ID
     * @param vipID String
     * @param vipPhone1 String
     * @param shopId String
     * @param saleDate date, doc Date
     * @param saleAmt Bigdecimal, doc amount
     * @param coupons List<CrmCoupon>, coupon list
     * @param crmPoslines List<CrmPosline>, item list
     * @return Map<String, String>
     * if check OK, code = OK, desc is EMPTY
     * else code != OK, desc is error message
     */  
    public synchronized  Map<String, String> useCoupon(final String docId, final String vipID, final String vipPhone1, 
            final String shopId, final Date saleDate, final BigDecimal saleAmt, 
            final List<CrmCoupon> coupons, final List<CrmPosline> crmPoslines) {
        final Map<String, String> returnMapping = new HashMap<String, String>();
        try {
            if (vipPhone1 == null || vipPhone1.isEmpty()) {
                return null;
            }
            
            String sMessage;
            String timestamp = getTimestamp();
            String method = "zjian.crm.customer.coupons.use";
            String apiKey = method.replace(ZJIANCRM, EMPTY);
            String token = getTaken(crmAppKey, crmAppSecret, crmAccessToken, JSON_FORMAT, method, timestamp);

            final Map<String, String> mapping = new HashMap<String, String>();
            mapping.put("trade_no", docId);
            mapping.put("customer_no", vipID);
            mapping.put("telephone_no", vipPhone1);
            mapping.put("site_no", shopId);
            mapping.put("sale_date", DATEFORMAT.format(saleDate));
            mapping.put("sale_amt", saleAmt + "");
            JSONObject couponJsonBody = new JSONObject();
            JSONArray couponJsonArray = new JSONArray();
            for (CrmCoupon coupon : coupons) {
                JSONObject json = new JSONObject();
                json.put("pay_serialno", coupon.getPaySerialno());
                json.put("coupon_no", coupon.getCouponNo());
                couponJsonArray.put(json);
            }   
            couponJsonBody.put("coupon_list", couponJsonArray);
            mapping.put("coupon_list", couponJsonArray.toString().replace("\\", ""));            
            
            JSONObject poslineJsonBody = new JSONObject();
            JSONArray poslineJsonArray = new JSONArray();
            for (CrmPosline posline : crmPoslines) {
                JSONObject poslineJson = new JSONObject();
                poslineJson.put("product_no", posline.getProductNo());
                poslineJson.put("sale_qty", posline.getSaleQty());
                poslineJson.put("sale_amt", posline.getSaleAmt());
                poslineJsonArray.put(poslineJson);
            }   
            poslineJsonBody.put("product_list", poslineJsonArray);
            mapping.put("product_list", poslineJsonArray.toString().replace("\\", ""));
            
            String parameters = getPara(mapping);

//            String fullUrl = getFullUrl(url + apiKey, token, timestamp, apiVersion, appKey, accessToken, method, format, parameters);
            String fullUrl = getFullUrl(crmUrl + apiKey, token, timestamp, API_VERSION, crmAppKey, crmAccessToken, method, JSON_FORMAT);
            LOG.info("fullUrl:" + fullUrl);
            LOG.info("parameters:" + parameters);
//                    + "&v=2.0&app_key=test&access_token=www.zjian.net&method=zjian.crm.dept.post&format=json";
//            String returnStr = sendHttpPost(fullUrl, null);
            String returnStr = HttpUtil.postMap(fullUrl, mapping, UTF8);

            if (returnStr == null || EMPTY.equals(returnStr)) {
                sMessage = RETURN_RETURN_API_RET_EMPTY;
                LOG.info(sMessage);
                returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
                returnMapping.put(RETURN_DESC, sMessage);
                return returnMapping;
            }
            
            LOG.info(returnStr);
            
            JSONObject jsonResult = new JSONObject(returnStr);
            String result = getString(jsonResult.getString(RETURN_CODE));
            String reason = getString(jsonResult.getString(RETURN_DESC));

            if (!RETURN_OK.equals(result)) {
                returnMapping.put(RETURN_CODE, result);
                returnMapping.put(RETURN_DESC, reason);
//                sMessage = "FAIL:" + result + ":" + reason;
//                System.out.println(sMessage);
                return returnMapping;
            } else {                
                returnMapping.put(RETURN_CODE, result);
                returnMapping.put(RETURN_DESC, reason);
            }

//            final List<Coupon> returnCoupons = new ArrayList<Coupon>();
//            JSONArray dataArray = new JSONArray(CFunction.getString(jsonResult.getString(RETURN_DATA)));
//            if (dataArray != null && dataArray.length() > 0) {
//                int count = dataArray.length();
//                for (int i = 0; i < count; i++) {
//                    JSONObject dataObject = (JSONObject) dataArray.get(i);
//                    if (dataObject != null) {
//                        final CrmCoupon coupon = new CrmCoupon();
//                        coupon.setCouponId(CFunction.getString(dataObject.getString("coupon_no")));
//                        coupon.setCouponId(CFunction.getString(dataObject.getString("sale_amt")));
//                        coupon.setCouponId(CFunction.getString(dataObject.getString("dis_amt")));
//                        returnCoupons.add(coupon);
//                    }
//                }
//            }            
            return returnMapping;

        } catch (JSONException ex) {
            String sMessage = "FAIL:" + ex.toString();
            LOG.error("Failed to useCoupon", ex);
            returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
            returnMapping.put(RETURN_DESC, sMessage);
            return returnMapping;
        }
    }  
    
    
    //
    // private
    //
    
    
    //MD5 code
    private static String md5Java(String message) {        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes(UTF8));
            //converting byte array to Hexadecimal  String 
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            String digest = sb.toString();
            return digest;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            LOG.error(ex);
            return EMPTY;
        }
    }

    private static String getTimestamp() {
        java.util.Date dCur = new java.util.Date();
//        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dCur.getTime();
        String timestamp = DATETIME_FORMAT.format(dCur);
        LOG.info("timestamp=" + timestamp);
        return timestamp;
    }

    private static String getTaken(String appKey, String appSecret, String accessToken, String format, String method, String timestamp) {
        String s = appSecret 
                + "access_token" + accessToken 
                + "app_key" + appKey 
                + "format" + format 
                + "method" + method 
                + "timestamp" + timestamp
                + appSecret;
        String token = md5Java(s);
        return token.toUpperCase();
    }
    
    private static String getPara(final Map<String, String> mapping) {
        String parameters = EMPTY;
        for (String key : mapping.keySet()) {
            final String value = mapping.get(key);
            parameters = EMPTY.equals(parameters) 
                    ? key + "=" + value 
                    : parameters + "&" + key + "=" + value;
        }
        return parameters;
    }
    
    private static String getFullUrl(String url, String sign, String timestamp, String apiVersion, 
            String appKey, String accessToken, String method, String format) {
        return url + "?" 
                + "sign=" + sign 
                + "&timestamp=" + URLEncoder.encode(timestamp)
                + "&v=" + URLEncoder.encode(apiVersion) 
                + "&app_key=" + (appKey == null || appKey.length() == 0 ? "" : URLEncoder.encode(appKey)) 
                + "&access_token=" + (accessToken == null || accessToken.length() == 0 ? "" : URLEncoder.encode(accessToken)) 
                + "&method=" + URLEncoder.encode(method)
                + "&format=" + URLEncoder.encode(format);
    }
    
    public static String getString(String input){
        if (null == input){
            return "";
        }
        return input;
    }
    
    public static Character getGender(final String genderName) {
      if (ZJ_MALE.equals(genderName)) {
          return MALE;
      } else if (ZJ_FEMALE.equals(genderName)) {
          return FEMALE;
      } else {
          return FEMALE;
      }
    }
    
    public static String getCouponReturnMsg(final String code) {
        if (RETURN_OK.equals(code)) {
            return "成功";
        } else if ("501".equals(code)) {
            return "会员券不存在";
        } else if ("502".equals(code)) {
            return "日期不在范围内";
        } else if ("503".equals(code)) {
            return "此门店不可用";
        } else if ("599".equals(code)) {
            return "其他";
        } else {
            return null;
        }
    }
    
    //
    // test
    //    
    
    public static void main(String args[]) {
        try {
            /**
             * @param appKey String, system setting:POSO2OAPPKEY
             * @param appSecret String, system setting:POSO2OSECRET
             * @param accessToken String, system setting:POSO2OTOKEN
             * @param url String, system setting:POSO2OURL
             */
            final String appKey = "test";    // AppKey
            final String appSecret = "test"; // AppSecret
            final String accessToken = "www.zjian.net";
            final String url = "http://open.zjian.net/rest";
            EpbzjianApi zjianApi = new EpbzjianApi(appKey, appSecret, accessToken, url);
            
            final String phoneNo = "15221933637";
            CrmPosVipInfo crmPosVipInfo = zjianApi.searchVip(phoneNo);
            if (crmPosVipInfo == null) {
                LOG.info("invalid vip ID->" + phoneNo);
                return;
            }
            
            LOG.info("vip ID:" + crmPosVipInfo.getVipId() + ","
                    + "name:" + crmPosVipInfo.getName()+ ","
                    + "phone NO:" + crmPosVipInfo.getVipPhone1()+ ","
                    + "class ID:" + crmPosVipInfo.getClassId()+ ","
                    + "gender:" + crmPosVipInfo.getGender()+ ","
                    + "points:" + crmPosVipInfo.getPoints());
            final String couponNo1 = "djq0000001";
            final String shopId = "shop001";
            Map<String, String> retMap = zjianApi.checkCoupon(crmPosVipInfo.getVipId(), crmPosVipInfo.getVipPhone1(), couponNo1, shopId);
            if (retMap == null || !retMap.containsKey(RETURN_CODE)) {
                LOG.info("Failed to exec checkCoupon");
                return;
            }
            if (!RETURN_OK.equals(retMap.containsKey(RETURN_CODE))) {
                LOG.info(retMap.containsKey(RETURN_CODE) + ":" + retMap.containsKey(RETURN_DESC));
                return;
            }
            if (RETURN_OK.equals(retMap.containsKey(RETURN_CODE))) {
                LOG.info("check OK");
            }
            String posDocId = "DOC202011210001";
            final String couponNo2 = "djq0000002";
            BigDecimal posGrandTotal = new BigDecimal(1000);
            List<CrmCoupon> coupons = new ArrayList<>();
            List<CrmPosline> crmPoslines = new ArrayList<>();
            CrmCoupon coupon = new CrmCoupon();
            coupon.setPaySerialno(1);
            coupon.setCouponNo(couponNo1);
            coupons.add(coupon);
            coupon = new CrmCoupon();
            coupon.setPaySerialno(2);
            coupon.setCouponNo(couponNo2);
            coupons.add(coupon);
            CrmPosline crmPosline = new CrmPosline();
            crmPosline.setProductNo("STK001");
            crmPosline.setSaleQty("1");
            crmPosline.setSaleAmt("200");
            crmPoslines.add(crmPosline);
            crmPosline = new CrmPosline();
            crmPosline.setProductNo("STK002");
            crmPosline.setSaleQty("4");
            crmPosline.setSaleAmt("800");
            crmPoslines.add(crmPosline);
            retMap = zjianApi.useCoupon(posDocId, crmPosVipInfo.getVipId(), crmPosVipInfo.getVipPhone1(), shopId, DATEFORMAT.parse("2020-11-21"), posGrandTotal, coupons, crmPoslines);
            if (retMap == null || !retMap.containsKey(RETURN_CODE)) {
                LOG.info("Failed to exec checkCoupon");
                return;
            }
            if (!RETURN_OK.equals(retMap.containsKey(RETURN_CODE))) {
                LOG.info(retMap.containsKey(RETURN_CODE) + ":" + retMap.containsKey(RETURN_DESC));
                return;
            }
            if (RETURN_OK.equals(retMap.containsKey(RETURN_CODE))) {
                LOG.info("check OK");
            }
        } catch (Throwable thrl) {
            LOG.error(thrl);
        }
    }
    
}

