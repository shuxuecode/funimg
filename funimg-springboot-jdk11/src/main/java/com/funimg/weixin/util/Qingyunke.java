package com.funimg.weixin.util;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Qingyunke {

    public static String reboot(String str) throws Exception {
        String res = api(str);
        System.out.println(res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        if (jsonObject.getIntValue("result") == 0) {
            return jsonObject.getString("content");
        }
        return ":" + str;
    }

    public static String api(String str) throws Exception {
        String msg = URLEncoder.encode(str, "utf-8");
        String getURL = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + msg;
        URL getUrl = new URL(getURL);
        HttpURLConnection connection = (HttpURLConnection) getUrl
                .openConnection();
        connection.connect();
        // 取得输入流，并使用Reader读取
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), "utf-8"));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        // 断开连接
        connection.disconnect();
        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            String res = reboot("你好");
            System.out.println(res);
            JSONObject jsonObject = JSONObject.parseObject(res);
            jsonObject.getIntValue("result");
            jsonObject.getString("content");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
