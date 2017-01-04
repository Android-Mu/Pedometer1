package com.click369.pedometer.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.click369.pedometer.data.javabean.Weather;
import com.click369.pedometer.ui.fragment.tools.TimeHelper;
/**
 * 下载解析天气的帮助类
 * @author Administrator
 *
 */
public class HttpUtil {
	public static Weather LoadWeather(String cityname) {
	Weather weather = null;
	
	try {
		URL url=new URL("http://api.k780.com:88/?app=weather.future&weaid="+cityname+
				"&&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json");			
		HttpURLConnection connection=(HttpURLConnection)url.openConnection();	
//		Log.i("error", "get之前");
		connection.setRequestMethod("GET");			
		connection.setConnectTimeout(5000);
		connection.connect();
//		Log.i("error", cityname);
		if(connection.getResponseCode()==200){
		InputStream inputStream=connection.getInputStream();
		String weatherjson=getJson(inputStream);
		weather=JieXi(weatherjson);
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	return weather;
}
/**
 * 
 * 解析Json数据获得天气情况
 * @param weatherjson
 * @return
 * @throws Exception 
 */
private static Weather JieXi(String weatherjson) throws Exception {
	Weather  weather = null;
	if(weatherjson.length()>0){
	weather=new Weather();
	JSONObject jsonObject=new JSONObject(weatherjson.toString());
	JSONArray weatherarry=jsonObject.getJSONArray("result");
	weather.setPtime(TimeHelper.gettime());
	weather.setTemp1(weatherarry.getJSONObject(0).getString("temp_low"));
	weather.setTemp2(weatherarry.getJSONObject(0).getString("temp_high"));
	weather.setWeather(weatherarry.getJSONObject(0).getString("weather"));
	}
	return weather;
}
/**
 * 获得json数据的过程
 * @param inputStream
 * @return
 * @throws Exception
 */

private static String getJson(InputStream inputStream) throws Exception {

	byte buffer[]=new byte[1024];
	int temp=0;
	ByteArrayOutputStream out=new ByteArrayOutputStream();
	while((temp=inputStream.read(buffer))!=-1){
		out.write(buffer, 0, temp);
	}
	out.close();
	inputStream.close();
	
	byte data[]=out.toByteArray();
	String json=new String(data,0,data.length);
	return json;
}

}
