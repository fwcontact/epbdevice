/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.woo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.epb.pst.entity.EpDept;
import com.epb.epbdevice.beans.CrmCoupon;
import com.epb.epbdevice.beans.CrmPosVipInfo;
import com.epb.epbdevice.beans.CrmPosline;

@SuppressWarnings({ "deprecation" })
public class EpbzjianApi {
	// system setting
//    public static final String appKey = "test";    // AppKey
//    public static final String appSecret = "test"; // AppSecret
//    public static final String accessToken = "www.zjian.net";
//    public static final String url = "http://open.zjian.net/rest";

	public static final String RETURN_CODE = "code";
	public static final String RETURN_DESC = "desc";
    public static final String RETURN_OK = "200";
//	public static final String RETURN_OK = "000";
//	public static final String RETURN_SALE_AMT = "sale_amt";
	public static final String RETURN_DIS_AMT = "sum_disc_amt";
	public static final String RETURN_COUPON_TYPE = "coupon_type";
	public static final String RETURN_CODE_UNKOWNREASON = "unkown reaseon";
	public static final String RETURN_RETURN_API_RET_EMPTY = "FAIL:API return is empty";
	public static final String PM_ID_ZJIAN = "Daijq";
	public static final java.text.SimpleDateFormat DATETIME_FORMAT = new java.text.SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static final java.text.SimpleDateFormat DATEFORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd");

	private static final Log LOG = LogFactory.getLog(HttpUtil.class);
	private static final String RETURN_DATA = "data";
	private static final String EMPTY = "";
	private static final String STRING_NULL = "null";
	private static final String UTF8 = "UTF-8";
	private static final String ZJ_MALE = "10";
	private static final String ZJ_FEMALE = "20";
	private static final Character MALE = 'M';
	private static final Character FEMALE = 'F';
	private static final String OK = "OK";

	private static String appid;
	private static String appSecret;
	private static String accessToken;
	private static String url;

	// public

	/**
	 * @param zjAppid      String, system setting:POSO2OAPPKEY
	 * @param zjAppSecret   String, system setting:POSO2OSECRET
	 * @param zjAccessToken String, system setting:POSO2OTOKEN
	 * @param zjUrl         String, system setting:POSO2OURL
	 */
	public EpbzjianApi(final String zjAppid, final String zjAppSecret, final String zjAccessToken, final String zjUrl) {
		appid = zjAppid;
		appSecret = zjAppSecret;
		accessToken = zjAccessToken;
		url = zjUrl;
	}

	/**
	 * Search VIP by phone no
	 *
	 * @param vipPhone1 String phone no
	 * @return CrmPosVipInfo include VIP ID,name,class ID,vip phone1,gender,VIP
	 *         points,etc
	 */
	public synchronized CrmPosVipInfo searchVip(String vipPhone1) {
		try {
			if (vipPhone1 == null || vipPhone1.isEmpty() || vipPhone1.length() < 6) {
				return null;
			}

			String sMessage;
//			String timestamp = getTimestamp();
//			String method = "zjian.crm.customer.get";
//			String apiKey = method.replace(ZJIANCRM, EMPTY);
//			String token = getTaken(crmAppKey, crmAppSecret, crmAccessToken, JSON_FORMAT, method, timestamp);
////            JSONObject jsonObject = new JSONObject();
////            jsonObject.put("telephone_no", vipPhone1);
////            String jsonStr = null; //jsonObject.toString();    
////            System.out.println("jsonStr:" + jsonStr);
//			final Map<String, String> mapping = new HashMap<String, String>();
//			mapping.put("telephone_no", vipPhone1);
////            String parameters = getParameter(mapping);
//			String parameters = getPara(mapping);
////            parameters = "&" + "telephone_no" + "=" + URLEncoder.encode(vipPhone1);
////            String fullUrl = getFullUrl(url + apiKey, token, timestamp, apiVersion, appKey, accessToken, method, format, parameters);
//			String fullUrl = getFullUrl(crmUrl + apiKey, token, timestamp, API_VERSION, crmAppKey, crmAccessToken,
//					method, JSON_FORMAT);
//			LOG.info("fullUrl:" + fullUrl);
//			LOG.info("parameters:" + parameters);
////                    + "&v=2.0&app_key=test&access_token=www.zjian.net&method=zjian.crm.dept.post&format=json";
////            String returnStr = sendHttpPost(fullUrl, null);
//			String returnStr = HttpUtil.postMap(fullUrl, mapping, UTF8);
//
//			if (returnStr == null || EMPTY.equals(returnStr)) {
//				sMessage = RETURN_RETURN_API_RET_EMPTY;
//				LOG.info(sMessage);
//				return null;
//			}
//			LOG.info(returnStr);
//			JSONObject jsonResult = new JSONObject(returnStr);
//			String result = getString(jsonResult.optString(RETURN_CODE));
////            String reason = getString(jsonResult.getString(RETURN_DESC));
//
//			if (!RETURN_OK.equals(result)) {
////                sMessage = "FAIL:" + result + ":" + reason;
//				return null;
//			}
//
////            JSONObject dataJson = jsonResult.getJSONObject(RETURN_DATA);
////            JSONObject dataJson = new JSONObject(getString(jsonResult.getString(RETURN_DATA)));
////            JSONObject bodyJson = jsonResult.getJSONObject(RETURN_DATA);
//			JSONObject dataJson;
//			try {
//				dataJson = jsonResult.isNull(RETURN_DATA) ? null
//						: new JSONObject(getString(jsonResult.optString(RETURN_DATA)));
//			} catch (JSONException ex) {
////                LOG.error(ex);
//				dataJson = jsonResult.optJSONObject(RETURN_DATA);
//			}
//			if (dataJson == null) {
//				LOG.info("data is null");
//				return null;
//			}
//			String customerNo = getString(dataJson.optString("customer_no"));
//			String customerName = getString(dataJson.optString("customer_name"));
//			String customerGradeNo = getString(dataJson.optString("customer_grade_no"));
////            try {
////                customerGradeNo = CFunction.getString(dataJson.getString("customer_grade_no"));
////            } catch (Throwable throwable) {
////                customerGradeNo = customerGradeNo == null || customerGradeNo.isEmpty() || STRING_NULL.equals(customerGradeNo) ? "1" : customerGradeNo;
////            }
////            String customerGradeName = CFunction.getString(dataJson.getString("customer_grade_name"));
//			String gender = getString(dataJson.isNull("gender") ? EMPTY : dataJson.get("gender") + EMPTY);
//			String birthday = getString(dataJson.isNull("birthday") ? EMPTY : dataJson.get("birthday") + EMPTY);
//			String telephoneNo = getString(
//					dataJson.isNull("telephone_no") ? EMPTY : dataJson.get("telephone_no") + EMPTY);
//			String belongSiteNo = getString(
//					dataJson.isNull("belong_site_no") ? EMPTY : dataJson.get("belong_site_no") + EMPTY);
//			String belongAgentNo = getString(
//					dataJson.isNull("belong_agent_no") ? EMPTY : dataJson.get("belong_agent_no") + EMPTY);
			
			
			String timestamp = getTimestamp();
            String method = "zjian.crm.customer.get";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("telephone_no", vipPhone1);
            String requestJson = jsonObject.toString();    
            String fullUrl = getFullUrl(url, method, timestamp, requestJson);
            Map<String, String> returnMapping = HttpUtil.callHttpPostMethod(fullUrl, requestJson, UTF8);
            if (!OK.equals(returnMapping.get("msgId"))) {
            	return null;
            }
            String returnStr = returnMapping.get("msg");
            if (returnStr == null || EMPTY.equals(returnStr)) {
				sMessage = RETURN_RETURN_API_RET_EMPTY;
				LOG.info(sMessage);
				return null;
            }
            JSONObject jsonResult = new JSONObject(returnStr);
            String result = jsonResult.optString(RETURN_CODE);
            String reason = jsonResult.optString(RETURN_DESC);

            if (!RETURN_OK.equals(result)) {
                sMessage = "FAIL:" + result + ":" + reason;
                System.out.println(sMessage);
                return null;
            }

            JSONObject dataJson = new JSONObject(getString(jsonResult.optString(RETURN_DATA)));
            String customerNo = getString(dataJson.optString("customer_no"));
            String customerName = getString(dataJson.optString("customer_name"));
            String customerGradeNo = getString(dataJson.optString("customer_grade_no"));
            String gender = getString(dataJson.optString("gender"));
            String birthday = getString(dataJson.optString("birthday"));
            String telephoneNo = getString(dataJson.optString("telephone_no"));
            String belongSiteNo = getString(dataJson.optString("belong_site_no"));
            String belongAgentNo = getString(dataJson.optString("belong_agent_no"));
            String pointCount = getString(dataJson.optString("point_count"));
			
			final CrmPosVipInfo posVipInfo = new CrmPosVipInfo();
			posVipInfo.setVipId(customerNo);
			posVipInfo.setName(customerName);
			posVipInfo.setClassId(customerGradeNo);
			posVipInfo.setGender(getGender(gender));
			posVipInfo.setBirthDate(birthday == null || birthday.length() == 0 || STRING_NULL.equals(birthday) ? null
					: DATEFORMAT.parse(birthday));
			posVipInfo.setVipPhone1(telephoneNo);
			posVipInfo.setLocId(belongSiteNo == null || STRING_NULL.equals(belongSiteNo) ? EMPTY : belongSiteNo);
			posVipInfo.setEmpId(belongAgentNo == null || STRING_NULL.equals(belongAgentNo) ? EMPTY : belongAgentNo);
//			BigDecimal points = getVipPoints(customerNo, telephoneNo);
			posVipInfo.setPoints(pointCount == null || pointCount.length() == 0 
					? BigDecimal.ZERO 
							: new BigDecimal(pointCount));
			return posVipInfo;
		} catch (ParseException | JSONException ex) {
			LOG.error("Failed to searchVip", ex);
			return null;
		}
	}
	
	/**
     * check coupons
     *
     * @param docId String, pos doc ID
     * @param shopId String, shop ID
     * @param vipId String, VIP ID
     * @param sumSaleAmt BigDecimal, redeem amount
     * @param skuList List<SkuList>, the array of pos line
     * @param couponList List<Coupon>, the array of coupon
     * 
     * @return Map<String, String>
     */
    public synchronized Map<String, String> checkCoupon(String docId, String shopId, String vipId, BigDecimal sumSaleAmt,  
            final List<CrmPosline> skuList,
            final String scanningCoupon) {
        String sMessage;
        final Map<String, String> returnMapping = new HashMap<String, String>();
        try {
            if (vipId == null || vipId.isEmpty() || vipId.length() < 6) {
				returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
				returnMapping.put(RETURN_DESC, "Invalid VIP ID");
				return returnMapping;
            }

            String timestamp = getTimestamp();
            String method = "zjian.crm.customer.coupon.check";
            
            BigDecimal totalAmt = BigDecimal.ZERO;
            for (CrmPosline sku : skuList) {
                if ("S".equals(sku.getLineType())) {
                    totalAmt = totalAmt.add(new BigDecimal(sku.getSaleAmt()));  //saleAmt = listPrice*stkQty
                }
            }
            
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("customer_no", vipId);
            jsonObject.put("site_no", shopId);
            jsonObject.put("sum_sale_price", totalAmt);
            jsonObject.put("sum_sale_amt", totalAmt);
            jsonObject.put("trade_no", docId);
            jsonObject.put("pay_serial_no", "pay" + docId);
            // sku list
            JSONArray skuJSONArray = new JSONArray();
            for (CrmPosline sku : skuList) {
                if ("S".equals(sku.getLineType())) {
                    JSONObject json = new JSONObject();
                    json.put("sku_no", sku.getProductNo());
                    json.put("sale_price", new BigDecimal(sku.getSaleAmt()));
                    json.put("sale_amt", new BigDecimal(sku.getSaleAmt()));
                    skuJSONArray.put(json);
                }
            }
            jsonObject.put("sku_list", skuJSONArray);
            // coupon list
            JSONArray couponJSONArray = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("coupon_no", scanningCoupon);
            couponJSONArray.put(json);
            jsonObject.put("coupon_list", couponJSONArray);
            
            String requestJson = jsonObject.toString();
            String fullUrl = getFullUrl(url, method, timestamp, requestJson);
            Map<String, String> mapping = HttpUtil.callHttpPostMethod(fullUrl, requestJson, UTF8);
            if (!OK.equals(mapping.get("msgId"))) {
				returnMapping.put(RETURN_CODE, mapping.get("msgId"));
				returnMapping.put(RETURN_DESC, mapping.get("msg"));
				return returnMapping;
            }
            String returnStr = mapping.get("msg");
            if (returnStr == null || EMPTY.equals(returnStr)) {
                sMessage = RETURN_RETURN_API_RET_EMPTY;
				sMessage = RETURN_RETURN_API_RET_EMPTY;
				LOG.info(sMessage);
				returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
				returnMapping.put(RETURN_DESC, sMessage);
				return returnMapping;
            }
            LOG.info(returnStr);
            JSONObject jsonResult = new JSONObject(returnStr);
            String result = getString(jsonResult.optString(RETURN_CODE));
            String reason = getString(jsonResult.optString(RETURN_DESC));

            if (!RETURN_OK.equals(result)) {
                sMessage = "FAIL:" + result + ":" + reason;
				LOG.info(sMessage);
				returnMapping.put(RETURN_CODE, result);
				returnMapping.put(RETURN_DESC, sMessage);
				return returnMapping;
            }
            JSONObject dataJson = new JSONObject(getString(jsonResult.optString(RETURN_DATA)));
            String sumDiscAmt = getString(dataJson.optString(RETURN_DIS_AMT));
            
            JSONArray couponListJson = new JSONArray(dataJson.opt("coupon_use_list") + EMPTY);
            String couponType = "";
            if (couponListJson != null && couponListJson.length() > 0) {
                int count = couponListJson.length();
                for (int i = 0; i < count; i++) {
                    JSONObject dataObject = (JSONObject) couponListJson.get(i);
                    if (dataObject != null) {
                        if (scanningCoupon.equals(dataObject.optString("coupon_no"))) {
                            couponType = dataObject.optString("source_type") 
//                                    + dataObject.optString("source_docno") 
                                    + new BigInteger(dataObject.opt("active_use_amt") + "");
                        }
                    }
                }                
            }
            
            // return OK
//            returnJSONObject.put("msgId", OK);
//            returnJSONObject.put("msg", EMPTY);
//            JSONObject bodyJSONObject = new JSONObject();
//            bodyJSONObject.put("sumDiscAmt", sumDiscAmt);
//            returnJSONObject.put("body", bodyJSONObject.toString());
//            return returnJSONObject.toString();
			returnMapping.put(RETURN_CODE, result);
			returnMapping.put(RETURN_DESC, reason);
			returnMapping.put(RETURN_DIS_AMT, sumDiscAmt);
			returnMapping.put(RETURN_COUPON_TYPE, couponType);
			return returnMapping;
        } catch (Exception ex) {
            LOG.error("error checkCoupons", ex);
            sMessage = "FAIL:" + ex.toString();
            LOG.info(sMessage);
            try {
            	returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
            	returnMapping.put(RETURN_DESC, sMessage);
                return returnMapping;
            } catch (Throwable thrl) {
                LOG.error("error checkCoupons2", thrl);
                returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
            	returnMapping.put(RETURN_DESC, thrl.getMessage());
                return returnMapping;
            }
        }
    }
	
	/**
     * check coupons
     *
     * @param docId String, pos doc ID
     * @param shopId String, shop ID
     * @param vipId String, VIP ID
     * @param sumSaleAmt BigDecimal, redeem amount
     * @param skuList List<SkuList>, the array of pos line
     * @param couponList List<Coupon>, the array of coupon
     * 
     * @return Map<String, String>
     */
    public synchronized Map<String, String> checkCoupons(String docId, String shopId, String vipId, BigDecimal sumSaleAmt,  
            final List<CrmPosline> skuList,
            final List<CrmCoupon> couponList) {
        String sMessage;
        final Map<String, String> returnMapping = new HashMap<String, String>();
        try {
            if (vipId == null || vipId.isEmpty() || vipId.length() < 6) {
				returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
				returnMapping.put(RETURN_DESC, "Invalid VIP ID");
				return returnMapping;
            }

            String timestamp = getTimestamp();
            String method = "zjian.crm.customer.coupon.check";
            
            BigDecimal totalAmt = BigDecimal.ZERO;
            for (CrmPosline sku : skuList) {
                if ("S".equals(sku.getLineType())) {
                    totalAmt = totalAmt.add(new BigDecimal(sku.getSaleAmt()));  //saleAmt = listPrice*stkQty
                }
            }
            
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("customer_no", vipId);
            jsonObject.put("site_no", shopId);
            jsonObject.put("sum_sale_price", totalAmt);
            jsonObject.put("sum_sale_amt", totalAmt);
            jsonObject.put("trade_no", docId);
            jsonObject.put("pay_serial_no", "pay" + docId);
            // sku list
            JSONArray skuJSONArray = new JSONArray();
            for (CrmPosline sku : skuList) {
                if ("S".equals(sku.getLineType())) {
                    JSONObject json = new JSONObject();
                    json.put("sku_no", sku.getProductNo());
                    json.put("sale_price", new BigDecimal(sku.getSaleAmt()));
                    json.put("sale_amt", new BigDecimal(sku.getSaleAmt()));
                    skuJSONArray.put(json);
                }
            }
            jsonObject.put("sku_list", skuJSONArray);
            // coupon list
            JSONArray couponJSONArray = new JSONArray();
            for (CrmCoupon coupon : couponList) {
                JSONObject json = new JSONObject();
                json.put("coupon_no", coupon.getCouponNo());
                couponJSONArray.put(json);
            }
            jsonObject.put("coupon_list", couponJSONArray);
            
            String requestJson = jsonObject.toString();
            String fullUrl = getFullUrl(url, method, timestamp, requestJson);
            Map<String, String> mapping = HttpUtil.callHttpPostMethod(fullUrl, requestJson, UTF8);
            if (!OK.equals(mapping.get("msgId"))) {
				returnMapping.put(RETURN_CODE, mapping.get("msgId"));
				returnMapping.put(RETURN_DESC, mapping.get("msg"));
				return returnMapping;
            }
            String returnStr = mapping.get("msg");
            if (returnStr == null || EMPTY.equals(returnStr)) {
                sMessage = RETURN_RETURN_API_RET_EMPTY;
				sMessage = RETURN_RETURN_API_RET_EMPTY;
				LOG.info(sMessage);
				returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
				returnMapping.put(RETURN_DESC, sMessage);
				return returnMapping;
            }
            LOG.info(returnStr);
            JSONObject jsonResult = new JSONObject(returnStr);
            String result = getString(jsonResult.optString(RETURN_CODE));
            String reason = getString(jsonResult.optString(RETURN_DESC));

            if (!RETURN_OK.equals(result)) {
                sMessage = "FAIL:" + result + ":" + reason;
				LOG.info(sMessage);
				returnMapping.put(RETURN_CODE, result);
				returnMapping.put(RETURN_DESC, sMessage);
				return returnMapping;
            }
            JSONObject dataJson = new JSONObject(getString(jsonResult.optString(RETURN_DATA)));
            String sumDiscAmt = getString(dataJson.optString(RETURN_DIS_AMT));
            
            // return OK
//            returnJSONObject.put("msgId", OK);
//            returnJSONObject.put("msg", EMPTY);
//            JSONObject bodyJSONObject = new JSONObject();
//            bodyJSONObject.put("sumDiscAmt", sumDiscAmt);
//            returnJSONObject.put("body", bodyJSONObject.toString());
//            return returnJSONObject.toString();
			returnMapping.put(RETURN_CODE, result);
			returnMapping.put(RETURN_DESC, reason);
			returnMapping.put(RETURN_DIS_AMT, sumDiscAmt);
			return returnMapping;
        } catch (Exception ex) {
            LOG.error("error checkCoupons", ex);
            sMessage = "FAIL:" + ex.toString();
            LOG.info(sMessage);
            try {
            	returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
            	returnMapping.put(RETURN_DESC, sMessage);
                return returnMapping;
            } catch (Throwable thrl) {
                LOG.error("error checkCoupons2", thrl);
                returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
            	returnMapping.put(RETURN_DESC, thrl.getMessage());
                return returnMapping;
            }
        }
    }
    
    /**
     * use coupons
     *
     * @param docId String, pos doc ID
     * @param shopId String, shop ID
     * @param vipId String, VIP ID
     * @param sumSaleAmt BigDecimal, redeem amount
     * @param skuList List<SkuList>, the array of pos line
     * @param couponList List<Coupon>, the array of coupon
     * 
     * @return Map<String, String>
     */
    public synchronized Map<String, String> useCoupons(String docId, String shopId, String vipId, BigDecimal sumSaleAmt,  
            final List<CrmPosline> skuList,
            final List<CrmCoupon> couponList) {
        String sMessage;
        final Map<String, String> returnMapping = new HashMap<String, String>();
        try {
            if (vipId == null || vipId.isEmpty() || vipId.length() < 6) {
            	returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
				returnMapping.put(RETURN_DESC, "Invalid VIP ID");
				return returnMapping;
            }

            String timestamp = getTimestamp();
            String method = "zjian.crm.customer.coupons.use";
            
            BigDecimal totalAmt = BigDecimal.ZERO;
            for (CrmPosline sku : skuList) {
                if ("S".equals(sku.getLineType())) {
                    totalAmt = totalAmt.add(new BigDecimal(sku.getSaleAmt()));
                }
            }
            
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("customer_no", vipId);
            jsonObject.put("site_no", shopId);
            jsonObject.put("sum_sale_price", totalAmt);
            jsonObject.put("sum_sale_amt", totalAmt);
            jsonObject.put("trade_no", docId);
            jsonObject.put("pay_serial_no", "pay" + docId);
            // sku list
            JSONArray skuJSONArray = new JSONArray();
            for (CrmPosline sku : skuList) {
                if ("S".equals(sku.getLineType())) {
                    JSONObject json = new JSONObject();
                    json.put("sku_no", sku.getProductNo());
//                    json.put("sale_price", sumSaleAmt.abs());
//                    json.put("sale_amt", sumSaleAmt.abs());
                    json.put("sale_price", new BigDecimal(sku.getSaleAmt()));
                    json.put("sale_amt", new BigDecimal(sku.getSaleAmt()));
                    skuJSONArray.put(json);
                }
            }
            jsonObject.put("sku_list", skuJSONArray);
            // coupon list
            JSONArray couponJSONArray = new JSONArray();
            for (CrmCoupon coupon : couponList) {
                JSONObject json = new JSONObject();
                json.put("coupon_no", coupon.getCouponNo());
                couponJSONArray.put(json);
            }
            jsonObject.put("coupon_list", couponJSONArray);
            
            String requestJson = jsonObject.toString();
            String fullUrl = getFullUrl(url, method, timestamp, requestJson);
            Map<String, String> mapping = HttpUtil.callHttpPostMethod(fullUrl, requestJson, UTF8);
            if (!OK.equals(mapping.get("msgId"))) {
            	returnMapping.put(RETURN_CODE, mapping.get("msgId"));
				returnMapping.put(RETURN_DESC, mapping.get("msg"));
				return returnMapping;
            }
            String returnStr = mapping.get("msg");
            if (returnStr == null || EMPTY.equals(returnStr)) {
                sMessage = RETURN_RETURN_API_RET_EMPTY;
                LOG.info(sMessage);
                returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
				returnMapping.put(RETURN_DESC, sMessage);
				return returnMapping;
            }
            LOG.info(returnStr);
            JSONObject jsonResult = new JSONObject(returnStr);
            String result = getString(jsonResult.optString(RETURN_CODE));
            String reason = getString(jsonResult.optString(RETURN_DESC));

            if (!RETURN_OK.equals(result)) {
                sMessage = "FAIL:" + result + ":" + reason;
                System.out.println(sMessage);
                returnMapping.put(RETURN_CODE, result);
				returnMapping.put(RETURN_DESC, sMessage);
				return returnMapping;
            }
            
            // return OK
            returnMapping.put(RETURN_CODE, result);
			returnMapping.put(RETURN_DESC, reason);
            return returnMapping;
        } catch (Exception ex) {
            LOG.error("error useCoupons", ex);
            sMessage = "FAIL:" + ex.toString();
            try {
            	returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
				returnMapping.put(RETURN_DESC, sMessage);
				return returnMapping;
            } catch (Throwable thrl) {
                LOG.error("error useCoupons2", thrl);
                returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
				returnMapping.put(RETURN_DESC, thrl.getMessage());
				return returnMapping;
            }
        }
    }
    
    
    
    /**
     * use coupons
     *
     * @param docId String, pos doc ID
     * @param shopId String, shop ID
     * @param vipId String, VIP ID
     * @param sumSaleAmt BigDecimal, redeem amount
     * @param skuList List<SkuList>, the array of pos line
     * @param couponList List<Coupon>, the array of coupon
     * 
     * @return String, it is json format,  e.g.{"msgId":"OK", "msg":""}
     */
    public synchronized Map<String, String> cancelCoupons(String docId, String shopId, String vipId,
            final List<CrmCoupon> couponList) {
        String sMessage;
        final Map<String, String> returnMapping = new HashMap<String, String>();
        try {
            if (vipId == null || vipId.isEmpty() || vipId.length() < 6) {
            	returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
				returnMapping.put(RETURN_DESC, "Invalid VIP ID");
				return returnMapping;
            }

            String timestamp = getTimestamp();
            String method = "zjian.crm.customer.coupon.cancel";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("customer_no", vipId);
            jsonObject.put("site_no", shopId);
            jsonObject.put("trade_no", docId);
            jsonObject.put("pay_serial_no", "pay" + docId);
            // coupon list
            JSONArray couponJSONArray = new JSONArray();
            for (CrmCoupon coupon : couponList) {
                JSONObject json = new JSONObject();
                json.put("coupon_no", coupon.getCouponNo());
                couponJSONArray.put(json);
            }
            jsonObject.put("coupon_list", couponJSONArray);
            
            String requestJson = jsonObject.toString();
            String fullUrl = getFullUrl(url, method, timestamp, requestJson);
            Map<String, String> mapping = HttpUtil.callHttpPostMethod(fullUrl, requestJson, UTF8);
            if (!OK.equals(mapping.get("msgId"))) {
                returnMapping.put(RETURN_CODE, mapping.get("msgId"));
				returnMapping.put(RETURN_DESC, mapping.get("msg"));
				return returnMapping;
            }
            String returnStr = mapping.get("msg");
            if (returnStr == null || EMPTY.equals(returnStr)) {
                sMessage = RETURN_RETURN_API_RET_EMPTY;
                LOG.info(sMessage);
                returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
				returnMapping.put(RETURN_DESC, sMessage);
				return returnMapping;
            }
            LOG.info(returnStr);
            JSONObject jsonResult = new JSONObject(returnStr);
            String result = getString(jsonResult.optString(RETURN_CODE));
            String reason = getString(jsonResult.optString(RETURN_DESC));

            if (!RETURN_OK.equals(result)) {
                sMessage = "FAIL:" + result + ":" + reason;
                LOG.info(sMessage);
                returnMapping.put(RETURN_CODE, result);
				returnMapping.put(RETURN_DESC, sMessage);
				return returnMapping;
            }
            
            // return OK
            returnMapping.put(RETURN_CODE, result);
			returnMapping.put(RETURN_DESC, reason);
			return returnMapping;
        } catch (Exception ex) {
            LOG.error("error cancelCoupons", ex);
            sMessage = "FAIL:" + ex.toString();
            try {
                returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
    			returnMapping.put(RETURN_DESC, sMessage);
    			return returnMapping;
            } catch (Throwable thrl) {
                LOG.error("error cancelCoupons2", thrl);
                returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
    			returnMapping.put(RETURN_DESC, thrl.getMessage());
    			return returnMapping;
            }
        }
    }
	

//	/**
//	 * Search VIP by dyn code
//	 *
//	 * @param dynCode String dynamic code
//	 * @return CrmPosVipInfo include VIP ID,name,class ID,vip phone1,gender,VIP
//	 *         points,etc
//	 */
//	public synchronized CrmPosVipInfo searchVipByDynCode(String dynCode) {
//		try {
//			if (dynCode == null || dynCode.isEmpty() || dynCode.length() < 6) {
//				return null;
//			}
//
//			String sMessage;
//			final Map<String, String> mapping = new HashMap<String, String>();
//			mapping.put("dyno", dynCode);
////            String parameters = getParameter(mapping);
//			String parameters = getPara(mapping);
////            parameters = "&" + "telephone_no" + "=" + URLEncoder.encode(vipPhone1);
////            String fullUrl = getFullUrl(url + apiKey, token, timestamp, apiVersion, appKey, accessToken, method, format, parameters);
//			String fullUrl = "http://wx.shanghaiwoo.com/w/client/getCustomerByDyno";
//			LOG.info("fullUrl:" + fullUrl);
//			LOG.info("parameters:" + parameters);
////                    + "&v=2.0&app_key=test&access_token=www.zjian.net&method=zjian.crm.dept.post&format=json";
////            String returnStr = sendHttpPost(fullUrl, null);
//			String returnStr = HttpUtil.postMap(fullUrl, mapping, UTF8);
//
//			if (returnStr == null || EMPTY.equals(returnStr)) {
//				sMessage = RETURN_RETURN_API_RET_EMPTY;
//				LOG.info(sMessage);
//				return null;
//			}
//			LOG.info(returnStr);
////            returnStr = "{\"code\":\"000\",\"desc\":null,\"data\":{\"customer_no\":\"702250\",\"customer_name\":\"123\",\"grade_no\":\"30\",\"grade_name\":\"银卡会员\",\"point\":50,\"gender\":\"女\",\"birthday\":\"1993-05-05 \",\"telephone_no\":\"15110228610\",\"belong_site_no\":\"036\",\"belong_agent_no\":null}}";
//			JSONObject jsonResult = new JSONObject(returnStr);
//			String result = getString(jsonResult.optString(RETURN_CODE));
////            String reason = getString(jsonResult.getString(RETURN_DESC));
//
//			if (!RETURN_OK.equals(result)) {
////                sMessage = "FAIL:" + result + ":" + reason;
////                System.out.println(sMessage);
//				return null;
//			}
//
//			JSONObject dataJson = jsonResult.getJSONObject(RETURN_DATA);
////            JSONObject dataJson = new JSONObject(getString(jsonResult.getString(RETURN_DATA)));
//////            JSONObject bodyJson = jsonResult.getJSONObject(RETURN_DATA);
//			String customerNo = getString(dataJson.optString("customer_no"));
//			String customerName = getString(dataJson.optString("customer_name"));
//			String customerGradeNo = getString(dataJson.optString("grade_no"));
//			String gender = getString(dataJson.optString("gender"));
//			String birthday = getString(dataJson.optString("birthday"));
//			String telephoneNo = getString(dataJson.optString("telephone_no"));
//			String belongSiteNo = getString(dataJson.optString("belong_site_no"));
//			String belongAgentNo = getString(dataJson.optString("belong_agent_no"));
//			final CrmPosVipInfo posVipInfo = new CrmPosVipInfo();
//			posVipInfo.setVipId(customerNo);
//			posVipInfo.setName(customerName);
//			posVipInfo.setClassId(customerGradeNo);
//			posVipInfo.setGender(getGender(gender));
//			posVipInfo.setBirthDate(birthday == null || birthday.length() == 0 || STRING_NULL.equals(birthday) ? null
//					: DATEFORMAT.parse(birthday));
//			posVipInfo.setVipPhone1(telephoneNo);
//			posVipInfo.setLocId(belongSiteNo == null || STRING_NULL.equals(belongSiteNo) ? EMPTY : belongSiteNo);
//			posVipInfo.setEmpId(belongAgentNo == null || STRING_NULL.equals(belongAgentNo) ? EMPTY : belongAgentNo);
//			BigDecimal points = getVipPoints(customerNo, telephoneNo);
//			posVipInfo.setPoints(points == null ? BigDecimal.ZERO : points);
//			return posVipInfo;
//		} catch (ParseException | JSONException ex) {
//			LOG.error("Failed to searchVipByDynCode", ex);
//			return null;
//		}
//	}
//
//	/**
//	 * Search VIP points
//	 *
//	 * @param vipID     String, VIP ID
//	 * @param vipPhone1 String, phone no
//	 * @return vip points
//	 */
//	public synchronized BigDecimal getVipPoints(final String vipID, final String vipPhone1) {
//		try {
//			if (vipPhone1 == null || vipPhone1.isEmpty()) {
//				return null;
//			}
//
//			String sMessage;
//			String timestamp = getTimestamp();
//			String method = "zjian.crm.customer.account.get";
//			String apiKey = method.replace(ZJIANCRM, EMPTY);
//			String token = getTaken(crmAppKey, crmAppSecret, crmAccessToken, JSON_FORMAT, method, timestamp);
////            JSONObject jsonObject = new JSONObject();
//////            jsonObject.put("customer_no", vipID);
////            jsonObject.put("telephone_no", vipPhone1);
////            jsonObject.put("account_type", "10");  // 10:point; 20:prepaid card amount
//////            jsonObject.put("password", password);
////            String jsonStr = jsonObject.toString();    
////            System.out.println("jsonStr:" + jsonStr);
//
//			final Map<String, String> mapping = new HashMap<String, String>();
//			mapping.put("customer_no", vipID);
//			mapping.put("telephone_no", vipPhone1);
//			mapping.put("account_type", "10");
////            mapping.put("password", password);
//			String parameters = getPara(mapping);
//
////            String fullUrl = getFullUrl(url + apiKey, token, timestamp, apiVersion, appKey, accessToken, method, format, parameters);   
//			String fullUrl = getFullUrl(crmUrl + apiKey, token, timestamp, API_VERSION, crmAppKey, crmAccessToken,
//					method, JSON_FORMAT);
//			LOG.info("fullUrl:" + fullUrl);
//			LOG.info("parameters:" + parameters);
////                    + "&v=2.0&app_key=test&access_token=www.zjian.net&method=zjian.crm.dept.post&format=json";
////            String returnStr = sendHttpPost(fullUrl, null);
//			String returnStr = HttpUtil.postMap(fullUrl, mapping, UTF8);
//
//			if (returnStr == null || EMPTY.equals(returnStr)) {
//				sMessage = RETURN_RETURN_API_RET_EMPTY;
//				LOG.info(sMessage);
//				return null;
//			}
//
//			LOG.info(returnStr);
//			JSONObject jsonResult = new JSONObject(returnStr);
//			String result = getString(jsonResult.optString(RETURN_CODE));
////            String reason = getString(jsonResult.getString(RETURN_DESC));
//
//			if (!RETURN_OK.equals(result)) {
////                sMessage = "FAIL:" + result + ":" + reason;
////                System.out.println(sMessage);
//				return null;
//			}
//
//			JSONObject dataJson;
//			try {
//				dataJson = jsonResult.isNull(RETURN_DATA) ? null
//						: new JSONObject(getString(jsonResult.optString(RETURN_DATA)));
//			} catch (JSONException ex) {
////                LOG.error(ex);
//				dataJson = jsonResult.optJSONObject(RETURN_DATA);
//			}
//			if (dataJson == null) {
//				LOG.info("data is null");
//				return null;
//			}
////            String customerNo = CFunction.getString(bodyJson.getString("customer_no"));
////            String customerName = CFunction.getString(bodyJson.getString("customer_name"));
////            String accountType = CFunction.getString(bodyJson.getString("account_type"));
//			String availableAmt = getString(dataJson.optString("available_amt"));
//			return availableAmt == null || availableAmt.length() == 0
//					? BigDecimal.ZERO
//					: new BigDecimal(availableAmt);
//		} catch (JSONException ex) {
//			LOG.error("Failed to getVipPoints", ex);
//			return null;
//		}
//	}
//
//	public synchronized boolean useVipPoints(final String vipID, final String vipPhone1, final BigDecimal useAmt,
//			final String docId, final Date useDate) {
//		try {
//			if (vipPhone1 == null || vipPhone1.isEmpty()) {
//				return false;
//			}
//
//			if (useAmt == null || BigDecimal.ZERO.compareTo(useAmt) == 0) {
//				return false;
//			}
//
//			String sMessage;
//			String timestamp = getTimestamp();
//			String method = "zjian.crm.customer.account.use";
//			String apiKey = method.replace(ZJIANCRM, EMPTY);
//			String token = getTaken(crmAppKey, crmAppSecret, crmAccessToken, JSON_FORMAT, method, timestamp);
////            JSONObject jsonObject = new JSONObject();
//////            jsonObject.put("customer_no", vipID);
////            jsonObject.put("telephone_no", vipPhone1);
////            jsonObject.put("account_type", "10");  // 10:point; 20:prepaid card amount
//////            jsonObject.put("password", password);
////            jsonObject.put("use_amt", useAmt.toString());
////            jsonObject.put("trade_no", docId == null ? "" : docId);
////            jsonObject.put("use_date", useDate == null ? "" : dateFormat.format(useDate));
////            String jsonStr = jsonObject.toString();    
////            System.out.println("jsonStr:" + jsonStr);
//
//			final Map<String, String> mapping = new HashMap<String, String>();
//			mapping.put("customer_no", vipID);
//			mapping.put("telephone_no", vipPhone1);
//			mapping.put("account_type", "10");
////            mapping.put("password", password);
//			mapping.put("use_amt", useAmt.toString());
//			mapping.put("trade_no", docId == null ? "" : docId);
//			mapping.put("use_date", useDate == null ? "" : DATEFORMAT.format(useDate));
//			String parameters = getPara(mapping);
//
////            String fullUrl = getFullUrl(url + apiKey, token, timestamp, apiVersion, appKey, accessToken, method, format, parameters);   
//			String fullUrl = getFullUrl(crmUrl + apiKey, token, timestamp, API_VERSION, crmAppKey, crmAccessToken,
//					method, JSON_FORMAT);
//			LOG.info("fullUrl:" + fullUrl);
//			LOG.info("parameters:" + parameters);
////                    + "&v=2.0&app_key=test&access_token=www.zjian.net&method=zjian.crm.dept.post&format=json";
////            String returnStr = sendHttpPost(fullUrl, null);
//			String returnStr = HttpUtil.postMap(fullUrl, mapping, UTF8);
//
//			if (returnStr == null || EMPTY.equals(returnStr)) {
//				sMessage = RETURN_RETURN_API_RET_EMPTY;
//				LOG.info(sMessage);
//				return false;
//			}
//			LOG.info(returnStr);
//
//			JSONObject jsonResult = new JSONObject(returnStr);
//			String result = getString(jsonResult.getString(RETURN_CODE));
////            String reason = getString(jsonResult.getString(RETURN_DESC));
//
//			if (!RETURN_OK.equals(result)) {
////                sMessage = "FAIL:" + result + ":" + reason;
////                System.out.println(sMessage);
//				return false;
//			}
//
//			return true;
//		} catch (JSONException ex) {
//			LOG.error("Failed to useVipPoints", ex);
//			return false;
//		}
//	}
//
//
//	/**
//	 * Coupon verification
//	 *
//	 * @param vipID     String
//	 * @param vipPhone1 String
//	 * @param couponNo  String
//	 * @param shopId    String
//	 * @return Map<String, String> if check OK, code = OK, desc is EMPTY, sales_amt
//	 *         is sales amt,dis amt is discount amt else code != OK, desc is error
//	 *         message
//	 */
//	public synchronized Map<String, String> checkCoupon(final String vipID, final String vipPhone1,
//			final String couponNo, final String shopId) {
//		final Map<String, String> returnMapping = new HashMap<String, String>();
//		try {
//			if (vipPhone1 == null || vipPhone1.isEmpty()) {
//				return null;
//			}
//
//			String sMessage;
//			String timestamp = getTimestamp();
//			String method = "zjian.crm.customer.coupon.check";
//			String apiKey = method.replace(ZJIANCRM, EMPTY);
//			String token = getTaken(crmAppKey, crmAppSecret, crmAccessToken, JSON_FORMAT, method, timestamp);
//
//			final Map<String, String> mapping = new HashMap<String, String>();
//			mapping.put("coupon_no", couponNo);
//			mapping.put("customer_no", vipID);
//			mapping.put("telephone_no", vipPhone1);
//			mapping.put("site_no", shopId);
//			String parameters = getPara(mapping);
//
////            String fullUrl = getFullUrl(url + apiKey, token, timestamp, apiVersion, appKey, accessToken, method, format, parameters);
//			String fullUrl = getFullUrl(crmUrl + apiKey, token, timestamp, API_VERSION, crmAppKey, crmAccessToken,
//					method, JSON_FORMAT);
//			LOG.info("fullUrl:" + fullUrl);
//			LOG.info("parameters:" + parameters);
////                    + "&v=2.0&app_key=test&access_token=www.zjian.net&method=zjian.crm.dept.post&format=json";
////            String returnStr = sendHttpPost(fullUrl, null);
//			String returnStr = HttpUtil.postMap(fullUrl, mapping, UTF8);
//
//			if (returnStr == null || EMPTY.equals(returnStr)) {
//				sMessage = RETURN_RETURN_API_RET_EMPTY;
//				LOG.info(sMessage);
//				returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
//				returnMapping.put(RETURN_DESC, sMessage);
//				return returnMapping;
//			}
//
//			LOG.info(returnStr);
//
//			JSONObject jsonResult = new JSONObject(returnStr);
//			String result = getString(jsonResult.optString(RETURN_CODE));
//			String reason = getString(jsonResult.optString(RETURN_DESC));
//
//			if (!RETURN_OK.equals(result)) {
//				returnMapping.put(RETURN_CODE, result);
//				returnMapping.put(RETURN_DESC, reason);
////                sMessage = "FAIL:" + result + ":" + reason;
////                System.out.println(sMessage);
//				return returnMapping;
//			}
//
////            JSONObject dataJson = new JSONObject(getString(jsonResult.getString(RETURN_DATA)));
////            JSONObject dataJson = jsonResult.getJSONObject(RETURN_DATA);
//			JSONObject dataJson;
//			try {
//				dataJson = jsonResult.isNull(RETURN_DATA) ? null
//						: new JSONObject(getString(jsonResult.optString(RETURN_DATA)));
//			} catch (JSONException ex) {
////                LOG.error(ex);
//				dataJson = jsonResult.optJSONObject(RETURN_DATA);
//			}
//			if (dataJson == null) {
//				LOG.info("data is null");
//				return null;
//			}
//			String saleAmt = getString(dataJson.optString(RETURN_SALE_AMT));
//			String disAmt = getString(dataJson.optString(RETURN_DIS_AMT));
//			returnMapping.put(RETURN_CODE, result);
//			returnMapping.put(RETURN_DESC, reason);
////            returnMapping.put(RETURN_DESC, CFunction.getCouponReturnMsg(result));
//			returnMapping.put(RETURN_SALE_AMT, saleAmt);
//			returnMapping.put(RETURN_DIS_AMT, disAmt);
//			return returnMapping;
//
//		} catch (JSONException ex) {
//			String sMessage = "FAIL:" + ex.toString();
//			LOG.error("Failed to checkCoupon", ex);
//			returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
//			returnMapping.put(RETURN_DESC, sMessage);
//			return returnMapping;
//		}
//	}
//
//	/**
//	 * offset coupon
//	 *
//	 * @param docId       String, pos doc ID
//	 * @param vipID       String
//	 * @param vipPhone1   String
//	 * @param shopId      String
//	 * @param saleDate    date, doc Date
//	 * @param saleAmt     Bigdecimal, doc amount
//	 * @param coupons     List<CrmCoupon>, coupon list
//	 * @param crmPoslines List<CrmPosline>, item list
//	 * @return Map<String, String> if check OK, code = OK, desc is EMPTY else code
//	 *         != OK, desc is error message
//	 */
//	public synchronized Map<String, String> useCoupon(final String docId, final String vipID, final String vipPhone1,
//			final String shopId, final Date saleDate, final BigDecimal saleAmt,
//			final List<CrmCoupon> coupons, final List<CrmPosline> crmPoslines) {
//		final Map<String, String> returnMapping = new HashMap<String, String>();
//		try {
//			if (vipPhone1 == null || vipPhone1.isEmpty()) {
//				return null;
//			}
//
//			String sMessage;
//			String timestamp = getTimestamp();
//			String method = "zjian.crm.customer.coupons.use";
//			String apiKey = method.replace(ZJIANCRM, EMPTY);
//			String token = getTaken(crmAppKey, crmAppSecret, crmAccessToken, JSON_FORMAT, method, timestamp);
//
//			final Map<String, String> mapping = new HashMap<String, String>();
//			mapping.put("trade_no", docId);
//			mapping.put("customer_no", vipID);
//			mapping.put("telephone_no", vipPhone1);
//			mapping.put("site_no", shopId);
//			mapping.put("sale_date", DATEFORMAT.format(saleDate));
//			mapping.put("sale_amt", saleAmt + "");
//			JSONObject couponJsonBody = new JSONObject();
//			JSONArray couponJsonArray = new JSONArray();
//			for (CrmCoupon coupon : coupons) {
//				JSONObject json = new JSONObject();
//				json.put("pay_serialno", coupon.getPaySerialno());
//				json.put("coupon_no", coupon.getCouponNo());
//				couponJsonArray.put(json);
//			}
//			couponJsonBody.put("coupon_list", couponJsonArray);
//			mapping.put("coupon_list", couponJsonArray.toString().replace("\\", ""));
//
//			JSONObject poslineJsonBody = new JSONObject();
//			JSONArray poslineJsonArray = new JSONArray();
//			for (CrmPosline posline : crmPoslines) {
//				JSONObject poslineJson = new JSONObject();
//				poslineJson.put("product_no", posline.getProductNo());
//				poslineJson.put("sale_qty", posline.getSaleQty());
//				poslineJson.put("sale_amt", posline.getSaleAmt());
//				poslineJsonArray.put(poslineJson);
//			}
//			poslineJsonBody.put("product_list", poslineJsonArray);
//			mapping.put("product_list", poslineJsonArray.toString().replace("\\", ""));
//
//			String parameters = getPara(mapping);
//
////            String fullUrl = getFullUrl(url + apiKey, token, timestamp, apiVersion, appKey, accessToken, method, format, parameters);
//			String fullUrl = getFullUrl(crmUrl + apiKey, token, timestamp, API_VERSION, crmAppKey, crmAccessToken,
//					method, JSON_FORMAT);
//			LOG.info("fullUrl:" + fullUrl);
//			LOG.info("parameters:" + parameters);
////                    + "&v=2.0&app_key=test&access_token=www.zjian.net&method=zjian.crm.dept.post&format=json";
////            String returnStr = sendHttpPost(fullUrl, null);
//			String returnStr = HttpUtil.postMap(fullUrl, mapping, UTF8);
//
//			if (returnStr == null || EMPTY.equals(returnStr)) {
//				sMessage = RETURN_RETURN_API_RET_EMPTY;
//				LOG.info(sMessage);
//				returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
//				returnMapping.put(RETURN_DESC, sMessage);
//				return returnMapping;
//			}
//
//			LOG.info(returnStr);
//
//			JSONObject jsonResult = new JSONObject(returnStr);
//			String result = getString(jsonResult.optString(RETURN_CODE));
//			String reason = getString(jsonResult.optString(RETURN_DESC));
//
//			if (!RETURN_OK.equals(result)) {
//				returnMapping.put(RETURN_CODE, result);
//				returnMapping.put(RETURN_DESC, reason);
////                sMessage = "FAIL:" + result + ":" + reason;
////                System.out.println(sMessage);
//				return returnMapping;
//			} else {
//				returnMapping.put(RETURN_CODE, result);
//				returnMapping.put(RETURN_DESC, reason);
//			}
//
////            final List<Coupon> returnCoupons = new ArrayList<Coupon>();
////            JSONArray dataArray = new JSONArray(CFunction.getString(jsonResult.getString(RETURN_DATA)));
////            if (dataArray != null && dataArray.length() > 0) {
////                int count = dataArray.length();
////                for (int i = 0; i < count; i++) {
////                    JSONObject dataObject = (JSONObject) dataArray.get(i);
////                    if (dataObject != null) {
////                        final CrmCoupon coupon = new CrmCoupon();
////                        coupon.setCouponId(CFunction.getString(dataObject.getString("coupon_no")));
////                        coupon.setCouponId(CFunction.getString(dataObject.getString("sale_amt")));
////                        coupon.setCouponId(CFunction.getString(dataObject.getString("dis_amt")));
////                        returnCoupons.add(coupon);
////                    }
////                }
////            }            
//			return returnMapping;
//
//		} catch (JSONException ex) {
//			String sMessage = "FAIL:" + ex.toString();
//			LOG.error("Failed to useCoupon", ex);
//			returnMapping.put(RETURN_CODE, RETURN_CODE_UNKOWNREASON);
//			returnMapping.put(RETURN_DESC, sMessage);
//			return returnMapping;
//		}
//	}

	//
	// private
	//

	private static String getFullUrl(String url, String method, String timestamp, String requestJson) {
        String sign = getSign(accessToken, appid, timestamp, appSecret, requestJson);
//        String fullUrl = url + method
//                + "?appid=" + appid
//                + "&access_token=" + accessToken
//                + "&timestamp=" + timestamp
//                + "&sign=" + sign;
//        return fullUrl;
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append(method);
        sb.append("?appid=");
        sb.append(appid);
        sb.append("&access_token=");
        sb.append(accessToken);
        sb.append("&timestamp=");
        sb.append(timestamp);
        sb.append("&sign=");
        sb.append(sign);
        return sb.toString();
    }
    
    private static String getSign(String accessToken, String appid, String timestamp, String secret, String requestJson) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("access_token=");
            sb.append(accessToken);
            sb.append("&appid=");
            sb.append(appid);
            sb.append("&timestamp=");
            sb.append(timestamp);
            sb.append("&");
            sb.append(requestJson);
            LOG.info("request json:" + sb.toString());
            LOG.info("secret:" + secret);
            String comSign = SignUtil.encryptHmacString(sb.toString(), secret);
            LOG.info("comSign:" + comSign);
            return comSign;
        } catch (Throwable thrl) {
            LOG.error("error getSign", thrl);
            return null;
        }
    }
    
    private static String getTimestamp() {
        long timestamp = System.currentTimeMillis();
        return timestamp + "";
    }

	public static String getString(String input) {
		if (null == input) {
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

//	public static String getCouponReturnMsg(final String code) {
//		if (RETURN_OK.equals(code)) {
//			return "成功";
//		} else if ("501".equals(code)) {
//			return "会员券不存在";
//		} else if ("502".equals(code)) {
//			return "日期不在范围内";
//		} else if ("503".equals(code)) {
//			return "此门店不可用";
//		} else if ("599".equals(code)) {
//			return "其他";
//		} else {
//			return null;
//		}
//	}

	//
	// test
	//

	public static void main(String args[]) {
		try {
//			/**
//			 * @param appKey      String, system setting:POSO2OAPPKEY
//			 * @param appSecret   String, system setting:POSO2OSECRET
//			 * @param accessToken String, system setting:POSO2OTOKEN
//			 * @param url         String, system setting:POSO2OURL
//			 */
//			final String appKey = "test"; // AppKey
//			final String appSecret = "test"; // AppSecret
//			final String accessToken = "www.zjian.net";
//			final String url = "http://open.zjian.net/rest";
//			EpbzjianApi zjianApi = new EpbzjianApi(appKey, appSecret, accessToken, url);
//
//			final String phoneNo = "15221933637";
//			CrmPosVipInfo crmPosVipInfo = zjianApi.searchVip(phoneNo);
//			if (crmPosVipInfo == null) {
//				LOG.info("invalid vip ID->" + phoneNo);
//				return;
//			}
//
//			LOG.info("vip ID:" + crmPosVipInfo.getVipId() + ","
//					+ "name:" + crmPosVipInfo.getName() + ","
//					+ "phone NO:" + crmPosVipInfo.getVipPhone1() + ","
//					+ "class ID:" + crmPosVipInfo.getClassId() + ","
//					+ "gender:" + crmPosVipInfo.getGender() + ","
//					+ "points:" + crmPosVipInfo.getPoints());
//			final String couponNo1 = "djq0000001";
//			final String shopId = "shop001";
//			Map<String, String> retMap = zjianApi.checkCoupon(crmPosVipInfo.getVipId(), crmPosVipInfo.getVipPhone1(),
//					couponNo1, shopId);
//			if (retMap == null || !retMap.containsKey(RETURN_CODE)) {
//				LOG.info("Failed to exec checkCoupon");
//				return;
//			}
//			if (!RETURN_OK.equals(retMap.containsKey(RETURN_CODE))) {
//				LOG.info(retMap.containsKey(RETURN_CODE) + ":" + retMap.containsKey(RETURN_DESC));
//				return;
//			}
//			if (RETURN_OK.equals(retMap.containsKey(RETURN_CODE))) {
//				LOG.info("check OK");
//			}
//			String posDocId = "DOC202011210001";
//			final String couponNo2 = "djq0000002";
//			BigDecimal posGrandTotal = new BigDecimal(1000);
//			List<CrmCoupon> coupons = new ArrayList<>();
//			List<CrmPosline> crmPoslines = new ArrayList<>();
//			CrmCoupon coupon = new CrmCoupon();
//			coupon.setPaySerialno(1);
//			coupon.setCouponNo(couponNo1);
//			coupons.add(coupon);
//			coupon = new CrmCoupon();
//			coupon.setPaySerialno(2);
//			coupon.setCouponNo(couponNo2);
//			coupons.add(coupon);
//			CrmPosline crmPosline = new CrmPosline();
//			crmPosline.setProductNo("STK001");
//			crmPosline.setSaleQty("1");
//			crmPosline.setSaleAmt("200");
//			crmPoslines.add(crmPosline);
//			crmPosline = new CrmPosline();
//			crmPosline.setProductNo("STK002");
//			crmPosline.setSaleQty("4");
//			crmPosline.setSaleAmt("800");
//			crmPoslines.add(crmPosline);
//			retMap = zjianApi.useCoupon(posDocId, crmPosVipInfo.getVipId(), crmPosVipInfo.getVipPhone1(), shopId,
//					DATEFORMAT.parse("2020-11-21"), posGrandTotal, coupons, crmPoslines);
//			if (retMap == null || !retMap.containsKey(RETURN_CODE)) {
//				LOG.info("Failed to exec checkCoupon");
//				return;
//			}
//			if (!RETURN_OK.equals(retMap.containsKey(RETURN_CODE))) {
//				LOG.info(retMap.containsKey(RETURN_CODE) + ":" + retMap.containsKey(RETURN_DESC));
//				return;
//			}
//			if (RETURN_OK.equals(retMap.containsKey(RETURN_CODE))) {
//				LOG.info("check OK");
//			}
		} catch (Throwable thrl) {
			LOG.error(thrl);
		}
	}

}
