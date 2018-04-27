package com.ligf.androidhttplib;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 使用Android自带的HttpUrlConnection和apache的HttpClient进行Http请求，而不是使用网络请求框架
 * Created by garment on 2018/1/21.
 */

public class BaseHttpRequestUtil {

    /**
     * execute the http request in GET method of HttpUrlConnection. Return the String as a result.
     * @param url
     * @param paramMap
     * @return
     */
    public static String executeHttpUrlConnectionGet(String url, HashMap<String, String> paramMap){
        String resultString = null;
        HttpURLConnection connection = null;
        InputStreamReader inputStreamReader = null;
        try {
            String paramsString = null;
            Iterator iterator = paramMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, String> entry = (Map.Entry<String, String>)iterator.next();
                String key = entry.getKey();
                String value = entry.getValue();
                paramsString = paramsString + key + "=" + value + "&";
            }
            URL requestUrl = new URL(url + "?" + paramsString.substring(0, paramsString.length() - 1));
            connection = (HttpURLConnection) requestUrl.openConnection();
            inputStreamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }
            resultString = stringBuffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null){
                connection.disconnect();
            }
            if (inputStreamReader != null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultString;
    }

    /**
     * execute the http request in POST method of HttpUrlConnection, return the string as a result
     * @param url
     * @param paramMap
     * @return
     */
    public static String executeHttpUrlConnectionPost(String url, HashMap<String, String> paramMap ){
        String resultString = null;
        HttpURLConnection connection = null;
        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            String paramsString = null;
            Iterator iterator = paramMap.entrySet().iterator();
            //post the params with key value pair
            while (iterator.hasNext()){
                Map.Entry<String, String> entry = (Map.Entry<String, String>)iterator.next();
                String key = entry.getKey();
                String value = entry.getValue();
                paramsString = paramsString + key + "=" + value + "&";
            }
            //set request head
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //post method must set this to true(default value is true)
            connection.setDoInput(true);
            //post method must set this to true
            connection.setDoOutput(true);
            //get OutputStream to send the params data to the server
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(paramsString.substring(0, paramsString.length() -1).getBytes());
            outputStream.flush();

            if (connection.getResponseCode() == 200){
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int length = 0;
                byte[] buffer = new byte[1024];
                while ((length = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, length);
                }
                inputStream.close();
                byteArrayOutputStream.close();
                resultString = byteArrayOutputStream.toString();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultString;
    }

    /**
     * execute the http request in GET method of HttpClient
     * @param url
     * @param paramsMap
     * @return
     */
    public String executeHttpClientGet(String url, HashMap<String, String> paramsMap){
        String resultString = null;
        BufferedReader bufferedReader = null;
        String paramsString = null;
        Iterator iterator = paramsMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            paramsString = paramsString + key + "=" + value + "&";
        }
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet httpGet = new HttpGet(new URI(url + "?" + paramsString.substring(0, paramsString.length() - 1)));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }
            resultString = stringBuffer.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultString;
    }

    /**
     * execute the http request in POST method of HttpClient
     * @param url
     * @param paramsMap
     * @return
     */
    public String executeHttpClientPost(String url, HashMap<String, String> paramsMap){
        String resultString = null;
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(new URI(url));
            //post the params with key value pair
            List<NameValuePair> paramsList = new ArrayList<>();
            Iterator iterator = paramsMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String,String> entry = (Map.Entry) iterator.next();
                String key = entry.getKey();
                String value = entry.getValue();
                paramsList.add(new BasicNameValuePair(key, value));
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(paramsList);
            httpPost.setEntity(urlEncodedFormEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }
            resultString = stringBuffer.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultString;
    }
}
