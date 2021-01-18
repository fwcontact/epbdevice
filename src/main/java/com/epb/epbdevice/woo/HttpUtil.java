package com.epb.epbdevice.woo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class HttpUtil {

	private static final Log LOG = LogFactory.getLog(HttpUtil.class);

	/**
	 * * post map 数据
	 *
	 * @param url
	 * @param prarmsMap
	 * @param charset
	 * @return
	 */
	public static String postMap(String url, Map<String, String> prarmsMap, String charset) {
		LOG.info("postMap地址：" + url);
		String content = "";
		CloseableHttpResponse response = null;
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost httppost = new HttpPost(url);
		httppost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
		RequestConfig config = RequestConfig.custom()
				.setConnectionRequestTimeout(10000).setConnectTimeout(10000)
				.setSocketTimeout(10000).build();
		try {
			// 设置Http Post数据
			if (prarmsMap != null) {
				List formparams = new ArrayList();
				Iterator<Map.Entry<String, String>> iter = prarmsMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, String> entry = iter.next();
					String key = entry.getKey();
					String val = entry.getValue();
					formparams.add(new BasicNameValuePair(key, val));
				}
				httppost.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));
			}

			httppost.setConfig(config);
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity);

		} catch (IOException e) {
			LOG.error(e);
		} finally {
			httppost.releaseConnection();
		}
		return content;
	}

	// HTTP post请求
	public static String postForm(String aUrl, String aParam) throws Exception {
		LOG.info("aParam:" + aParam);
		URL url = new URL(aUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		con.setUseCaches(false);

		OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream(), "utf-8");
		out.write(aParam);
		out.close();

		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
		String temp;
		StringBuilder sb = new StringBuilder();
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		return sb.toString();
	}

	public static String signTopRequestNew(Map<String, String> params, String secret, boolean isHmac)
			throws IOException {
		String[] keys = (String[]) params.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		StringBuilder query = new StringBuilder();
		if (!isHmac) {
			query.append(secret);
		}

		String[] bytes = keys;
		int len = keys.length;

		for (int i = 0; i < len; ++i) {
			String key = bytes[i];
			String value = (String) params.get(key);
			if (areNotEmpty(new String[] { key, value })) {
				query.append(key).append(value);
			}
		}

		byte[] var10;
		if (isHmac) {
			var10 = encryptHMAC(query.toString(), secret);
		} else {
			query.append(secret);
			var10 = encryptMD5(query.toString());
		}

		return byte2hex(var10);
	}

	private static byte[] encryptHMAC(String data, String secret) throws IOException {
		Object bytes = null;

		try {
			SecretKeySpec gse = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacMD5");
			Mac msg1 = Mac.getInstance(gse.getAlgorithm());
			msg1.init(gse);
			byte[] bytes1 = msg1.doFinal(data.getBytes("UTF-8"));
			return bytes1;
		} catch (GeneralSecurityException var5) {
			String msg = getStringFromException(var5);
			throw new IOException(msg);
		}
	}

	private static String getStringFromException(Throwable e) {
		String result = "";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bos);
		e.printStackTrace(ps);

		try {
			result = bos.toString("UTF-8");
		} catch (IOException var5) {
			;
		}

		return result;
	}

	private static byte[] encryptMD5(String data) throws IOException {
		return encryptMD5(data.getBytes("UTF-8"));
	}

	private static byte[] encryptMD5(byte[] data) throws IOException {
		Object bytes = null;

		try {
			MessageDigest gse = MessageDigest.getInstance("MD5");
			byte[] bytes1 = gse.digest(data);
			return bytes1;
		} catch (GeneralSecurityException var4) {
			String msg = getStringFromException(var4);
			throw new IOException(msg);
		}
	}

	private static String byte2hex(byte[] bytes) {
		StringBuilder sign = new StringBuilder();

		for (int i = 0; i < bytes.length; ++i) {
			String hex = Integer.toHexString(bytes[i] & 255);
			if (hex.length() == 1) {
				sign.append("0");
			}

			sign.append(hex.toUpperCase());
		}

		return sign.toString();
	}

	private static boolean areNotEmpty(String... values) {
		boolean result = true;
		if (values != null && values.length != 0) {
			String[] arr$ = values;
			int len = values.length;

			for (int i = 0; i < len; ++i) {
				String value = arr$[i];
				result &= !isEmpty(value);
			}
		} else {
			result = false;
		}

		return result;
	}

	private static boolean isEmpty(String value) {
		int strLen;
		if (value != null && (strLen = value.length()) != 0) {
			for (int i = 0; i < strLen; ++i) {
				if (!Character.isWhitespace(value.charAt(i))) {
					return false;
				}
			}

			return true;
		} else {
			return true;
		}
	}
}
