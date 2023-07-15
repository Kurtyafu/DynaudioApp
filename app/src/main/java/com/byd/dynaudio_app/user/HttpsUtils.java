package com.byd.dynaudio_app.user;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class HttpsUtils {
    /**
     * 使用GET方法读取http中的数据
     *
     * @param strUrlPath url地址
     * @return 请求的响应数据
     */
    public static String submitGetData(String strUrlPath, Map<String, String> requestPropertys) throws Exception {
        // 创建URL对象
        URL url = new URL(strUrlPath);
        // 打开连接 获取连接对象
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(6000);

        if (requestPropertys != null) {
            //设置 安卓端flag
            for (Map.Entry<String, String> entry : requestPropertys.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        // 从连接对象中获取网络连接中的输入字节流对象
        InputStream inputStream = connection.getInputStream();
        // 将输入字节流包装成输入字符流对象,并进行字符编码
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        // 创建一个输入缓冲区对象，将字符流对象传入
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // 定义一个字符串变量，用来接收输入缓冲区中的每一行字符串数据
        String line;
        // 创建一个可变字符串对象，用来装载缓冲区对象的数据，使用字符串追加的方式，将响应的所有数据都保存在该对象中
        StringBuilder stringBuilder = new StringBuilder();
        // 使用循环逐行读取输入缓冲区的数据，每次循环读入一行字符串数据赋值给line字符串变量，直到读取的行为空时标识内容读取结束循环
        while ((line = bufferedReader.readLine()) != null) {
            // 将从输入缓冲区读取到的数据追加到可变字符对象中
            stringBuilder.append(line);
        }
        // 依次关闭打开的输入流
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        // 将可变字符串转换成String对象返回
        return stringBuilder.toString();
    }

}
