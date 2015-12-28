package net.fortunes.util;

import java.io.IOException;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 一个工具类,用来处理日期等操作
 * @author Neo.Liao
 *
 */
public class Tools {
	
	/**
	 * 将日期字符串以特定格式(yyyy-MM-dd或者yyyy-MM-dd HH:mm:ss)转化为日期
	 * @param dateString 日期字符串
	 * @return 日期对象
	 */
	public static Date string2Date(String dateString)  {
		DateFormat df;
		try {
			if(dateString.trim().length() == 10)
				df = new SimpleDateFormat("yyyy-MM-dd");
			else
				df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (!StringUtils.isEmpty(dateString))
				return df.parse(dateString);
			else
				return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getChineseDay(int day){
		String retStr = "";
		switch (day) {
		case 1:
			return "星期日";
		case 2:
			return "星期一";
		case 3:
			return "星期二";
		case 4:
			return "星期三";
		case 5:
			return "星期四";
		case 6:
			return "星期五";
		case 7:
			return "星期六";
		
		default:
			break;
		}
		return retStr;
	}
	
	
	/**
	 * 将日期以以特定格式((yyyy-MM-dd HH:mm:ss,yyyy-MM-dd或者HH:mm:ss)转化为字符串
	 * @param date 日期对象
	 * @return 日期字符串
	 */
	public static String date2String(Date date){
		if(date != null){
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateString = df.format(date);
			if(dateString.startsWith("1970-01-01")){
				return dateString.substring(11);
			}
			else if(dateString.endsWith("00:00:00")){
				return dateString.substring(0, 10);
			}else{
				return dateString;
			}
		}else{
			return "";
		}
	}
	
	/**
	 * 将日期以以特定格式yyyy-MM-dd
	 * @param date 日期对象
	 * @return 日期字符串
	 */
	public static String dateToString(Date date){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}
	
	/**
	 * 把日期格式化成yyyy-MM
	 * @param date
	 * @return yyyy-MM
	 */
	public static String date2Year(Date date){
		DateFormat df = new SimpleDateFormat("yyyy");
		return df.format(date);
	}
	
	/**
	 * 把日期格式化成yyyy-MM
	 * @param date
	 * @return yyyy-MM
	 */
	public static String date2YearMonth(Date date){
		DateFormat df = new SimpleDateFormat("yyyy-MM");
		return df.format(date);
	}
	
	
	/**
	 * 得到当天日期特定格式(yyyy-MM-dd)字符串
	 * @return 日期字符串
	 */
	public static String getDateString(){
		Date date= new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);

	}
	
	/**
	 * 格式化成(yyyy-MM-dd)字符串
	 * @return 日期字符串
	 */
	public static String date2Date(Date date){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);

	}
	
	/**
	 * 在控制台打印调试消息
	 * @param msg 要打印的消息
	 */
	public static void println(String msg){
		System.out.println("\n");
		System.err.println("======== "+msg+" =======");
		System.out.println("\n");
	}
	
	
	/**
	 * 将中文汉字转化为首字母字符
	 * @param chinese 中文汉字
	 * @return 汉字首字母字符
	 */
	public static String chinese2PinYinShort(String chinese){
		return PinYin.toPinYinString(chinese);
	}
	
	/**
	 * MD5 加密
	 * @param password 明文密码
	 * @return 加密后的密码字符串
	 */
	public static String encodePassword(String password){
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(password.getBytes());
			byte[] temp = digest.digest();
			String returnString = "";
			for(int i=0;i<temp.length;i++){
				String plainText = Integer.toHexString(temp[i] & 0xEF);
				if (plainText.length() < 2) {
					plainText = "0" + plainText;
				}
				returnString += plainText;
			}
			return returnString;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * @return 一个uuid字符串
	 */
	public static String uuid(){
		return UUID.randomUUID().toString();
	}

	public static byte[] decodeByBase64(String string) {
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			return decoder.decodeBuffer(string);
		} catch (IOException e) {
			return null;
		}
	}
	
	public static String encodeToBase64(byte[] bytes) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(bytes);
	}
	
	public static String toPercentString(double number){
		return new DecimalFormat("%").format(number);
	}
	/**
     * byte数组转int
     * @param b The byte array
     * @return The integer
     */
    public static int byteArrayToInt(byte[] b) {
    	return (b[0] << 24)
	        + ((b[1] & 0xFF) << 16)
	        + ((b[2] & 0xFF) << 8)
	        + (b[3] & 0xFF);
    }

    public static Date stringToDate(String str) throws ParseException {
    	Date date;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			date = df.parse(str);
		} catch (ParseException e) {
			throw e;
		}
    	return date;
    }
    
    public static String Time2String(Date date) throws ParseException {
    	String fTime = null;
    	DateFormat df = new SimpleDateFormat("HH:mm:ss");
		fTime = df.format(date);
    	return fTime;
    }
    
    /**
     * int转byte数组
     * @param value
     * @return
     */
    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
}
	
	public static void main(String[] args) throws IOException {
//		byte[] bs = {0,0,1,1};
//		System.out.println(Tools.byteArrayToInt(bs));
//		byte[] ob = Tools.intToByteArray(257);
//		System.out.println(ob);
//		System.out.println(ByteOrder.nativeOrder());
//		Double b=2.5;
//		int c = b.intValue();
//		System.out.println(c);
		Date d = new Date();
		System.out.println(DatetoStringByChinese(d));
		
		}
	
	public static int getAge(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year1 = cal.get(Calendar.YEAR);
		cal.setTime(new Date());
		int year2 = cal.get(Calendar.YEAR);
		int age = year2 - year1;
		return age;
	}
	
	public static String getValuesNotZore(Double value){
//		Double aa = Double.parseDouble(value);
		int temp = value.intValue();
		if(value==temp){
			return Integer.toString(temp);
			
		}else{
			return value.toString();
		}
	}
	
	public static String DatetoStringByChinese(Date date){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String dateString  = df.format(date);
		String[] dates = dateString.split("-");
		return dates[0]+"年"+dates[1]+"月"+dates[2]+"日";
	}
	
	/**
	 * 将日期以以特定格式((yyyy-MM-dd HH:mm:ss)转化为(HH:mm:ss)格式字符串
	 * @param date 日期对象
	 * @return 日期字符串
	 */
	public static String dateTimeString(Date date){
		if(date != null){
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateString = df.format(date);
				return dateString.substring(11);
		  
		}else{
			return "";
		}
	}
}
