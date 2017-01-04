package com.click369.pedometer.ui.fragment.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * 提供时间
 * @author Administrator
 *
 */
public class TimeHelper {
	//日期
	public static String gettime(){
		
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		
	}
	//不带格式时间
	public static String gettimeString(){
	return new SimpleDateFormat("yyyyMMdd").format(new Date());
	}
	//时间戳
	public static String gettimename(){
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}
}
