package com.epb.epbdevice.memberson;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpUtil {
    
    public static final String POST_METHOD = "POST";
    public static final String GET_METHOD = "GET";

    private static final Log LOG = LogFactory.getLog(HttpUtil.class);
    private static final int MS_TIMEOUT = 10000;
    private static final String UTF8 = "UTF-8";
    private static final String TIMEOUT = "TIMEOUT";
    @SuppressWarnings("unused")
	private static final String UNKOWN = "Unkown Reason";
    private static final String EMPTY = "";

    public static Map<String, String> callHttpMethod(final String callUrl, final String callAuth, final String token, final String httpMethod, final String requestDataJson) {
        final Map<String, String> returnMapping = new HashMap<String, String>();
        try {
            LOG.info("callUrl:" + callUrl);
            URL url = new URL(callUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod(httpMethod);  // POST/GET
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.setRequestProperty("Accept", "application/json;charset=utf-8");
            connection.setRequestProperty("SvcAuth", callAuth);
            if (token != null && !token.isEmpty()) {
                connection.setRequestProperty("Token", token);
            }
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setReadTimeout(MS_TIMEOUT);
            connection.connect();

            if (requestDataJson != null && requestDataJson.length() != 0) {
                LOG.info("requestDataJson:" + requestDataJson);
                OutputStream out = connection.getOutputStream();
                out.write(requestDataJson.getBytes(UTF8));
                out.flush();
                out.close();
            }
            
      
            if (connection.getResponseCode() == 200) {
                //读取响应
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), UTF8));
                String lines;
                StringBuilder sb = new StringBuilder(EMPTY);
                while ((lines = reader.readLine()) != null) {
                    lines = new String(lines.getBytes(UTF8), UTF8);
                    sb.append(lines);
                }
                String jsonReturn = sb.toString();
                LOG.info("msg:" + sb);
                reader.close();
                // 断开连接
                connection.disconnect();
                returnMapping.put(Epbmemberson.MSG_ID, Epbmemberson.RETURN_OK);
                returnMapping.put(Epbmemberson.MSG, jsonReturn);
                return returnMapping;
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        (InputStream) connection.getErrorStream(), "UTF-8"));//返回从此打开的连接读取的输入流
                String line = "";
                String msg = "";
                while (line != null) {
                    line = in.readLine();
                    if (line != null) {
                        msg = msg + line;
                    }
                }
                in.close();
                LOG.info("Network Error,ResponseCode=" + connection.getResponseCode() + " msg=" + msg);
                returnMapping.put(Epbmemberson.MSG_ID, Epbmemberson.FAIL);
                returnMapping.put(Epbmemberson.MSG, "Network Error,ResponseCode=" + connection.getResponseCode() + " msg=" + msg);
                return returnMapping;
            }
        } catch (java.net.SocketTimeoutException ex) {
            returnMapping.put(Epbmemberson.MSG_ID, TIMEOUT);
            returnMapping.put(Epbmemberson.MSG, "Timeout");
            LOG.error(ex);
            return returnMapping;
        } catch (java.net.UnknownHostException ex) {
            returnMapping.put(Epbmemberson.MSG_ID, TIMEOUT);
            returnMapping.put(Epbmemberson.MSG, "unkowned");
            LOG.error(ex);
            return returnMapping;
        } catch (java.net.SocketException ex) {
            returnMapping.put(Epbmemberson.MSG_ID, TIMEOUT);
            returnMapping.put(Epbmemberson.MSG, "can not connect");
            LOG.error(ex);
            return returnMapping;
        } catch (IOException ex) {
            returnMapping.put(Epbmemberson.MSG_ID, Epbmemberson.FAIL);
            returnMapping.put(Epbmemberson.MSG, ex.toString());
            LOG.error(ex);
            return returnMapping;
        }
    }
   
    public static Map<String, String> callHttpGetMethod(final String callUrl, final String callAuth, final String token) {
        final Map<String, String> returnMapping = new HashMap<String, String>();
        try {
            // 得到请求报文的二进制数据
            //byte[] data = strData.getBytes("UTF-8"); //"UTF-8"
//        java.net.URL url = new java.net.URL(strUrl);
            LOG.info("callUrl:" + callUrl);
//        java.net.URL url = new java.net.URL("https://c21sguat.memgate.com/api/profile/01463571");
            java.net.URL url = new java.net.URL(callUrl);
            //openConnection() 返回一个 URLConnection 对象，它表示到 URL 所引用的远程对象的连接
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();// 打开一个连接
            conn.setRequestMethod(GET_METHOD);// 设置post方式请求
            conn.setConnectTimeout(5 * 1000);// 设置连接超时时间为5秒  JDK1.5以上才有此方法
            conn.setReadTimeout(300 * 1000);// 设置读取超时时间为20秒 JDK1.5以上才有此方法
            // 打算使用 URL 连接进行输出，则将 DoOutput 标志设置为 true
            //conn.setDoOutput(true);
            // 这里只设置内容类型与内容长度的头字段根据传送内容决定
            // 内容类型Content-Type:
            // application/x-www-form-urlencoded、text/xml;charset=GBK
            // 内容长度Content-Length: 38
            //conn.setRequestProperty("Content-Type", "text/xml;charset=GBK");
            conn.setRequestProperty("Content-Type", "application/Json;charset=UTF-8");
//            System.out.println("callAuth:" + callAuth);
//        conn.setRequestProperty("SvcAuth", "RVBCUE9T,OHRTMjBoWDFENDVT");
            conn.setRequestProperty("SvcAuth", callAuth);
//            System.out.println("token:" + token);
            conn.setRequestProperty("Token", token); // it is funny
//        conn.setRequestProperty("Token", token);
            //conn.setRequestProperty("Content-Length", String.valueOf(data.length));
//        OutputStream outStream = conn.getOutputStream();// 返回写入到此连接的输出流
//        // 把二进制数据写入是输出流
//        outStream.write(data);
//        // 内存中的数据刷入
//        outStream.flush();
//        //关闭流
//        outStream.close();

            String msg = "";// 保存调用http服务后的响应信息
            // 如果请求响应码是200，则表示成功
//        if (conn.getResponseCode() == 200) {
            // HTTP服务端返回的编码是UTF-8,故必须设置为UTF-8,保持编码统一,否则会出现中文乱码
//            System.out.println("ResponseCode:" + conn.getResponseCode());

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        (InputStream) conn.getInputStream(), "UTF-8"));//返回从此打开的连接读取的输入流
                String line = "";
                while (line != null) {
                    line = in.readLine();
                    if (line != null) {
                        msg = msg + line;
                    }
                }
                in.close();
//        } else {
//            throw new Exception("Network Error,ResponseCode=" + conn.getResponseCode());
//            //return "Network Error,ResponseCode=" + conn.getResponseCode();
//        }
                conn.disconnect();// 断开连接
                LOG.info("msg:" + msg);
                returnMapping.put(Epbmemberson.MSG_ID, Epbmemberson.RETURN_OK);
                returnMapping.put(Epbmemberson.MSG, msg);
                return returnMapping;
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        (InputStream) conn.getErrorStream(), "UTF-8"));//返回从此打开的连接读取的输入流
                String line = "";
                while (line != null) {
                    line = in.readLine();
                    if (line != null) {
                        msg = msg + line;
                    }
                }
                in.close();
                LOG.info("Network Error,ResponseCode=" + conn.getResponseCode() + " msg=" + msg);
                returnMapping.put(Epbmemberson.MSG_ID, Epbmemberson.FAIL);
                returnMapping.put(Epbmemberson.MSG, "Network Error,ResponseCode=" + conn.getResponseCode() + " msg=" + msg);
                return returnMapping;
            }
        } catch (java.net.SocketTimeoutException ex) {
            returnMapping.put(Epbmemberson.MSG_ID, TIMEOUT);
            returnMapping.put(Epbmemberson.MSG, "Timeout");
            LOG.error(ex);
            return returnMapping;
        } catch (java.net.UnknownHostException ex) {
            returnMapping.put(Epbmemberson.MSG_ID, TIMEOUT);
            returnMapping.put(Epbmemberson.MSG, "unkowned");
            LOG.error(ex);
            return returnMapping;
        } catch (java.net.SocketException ex) {
            returnMapping.put(Epbmemberson.MSG_ID, TIMEOUT);
            returnMapping.put(Epbmemberson.MSG, "can not connect");
            LOG.error(ex);
            return returnMapping;
        } catch (IOException ex) {
            returnMapping.put(Epbmemberson.MSG_ID, Epbmemberson.FAIL);
            returnMapping.put(Epbmemberson.MSG, ex.toString());
            LOG.error(ex);
            return returnMapping;
        }
    }
    
}



