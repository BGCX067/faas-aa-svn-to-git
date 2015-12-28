package net.fortunes.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Neo.Liao
 *
 */
public class Random extends java.util.Random{
	public static java.util.Random R = new java.util.Random();
	private static char[] letter = {
		'a','b','c','d','e',
		'f','g','h','i','j',
		'k','l','m','n','o',
		'p','q','r','s','t',
		'u','v','w','x','y','z'
	};
	
	/**
	 * 随机得到一个字母(大写)
	 * @return 一个随机的字母(大写)
	 */
	public static char nextCapitalLetter(){
		return Character.toUpperCase(letter[R.nextInt(26)]);
	}
	
	/**
	 * 随机得到一个字母(小写)
	 * @return 一个随机的字母(小写)
	 */
	public static char nextLowerLetter(){
		return letter[R.nextInt(26)];
	}
	
	/**
	 * 随机得到一个字母
	 * @return 一个随机的字母
	 */
	public static char nextLetter(){
		return R.nextBoolean()?
				letter[R.nextInt(26)]:Character.toUpperCase(letter[R.nextInt(26)]);
	}
	
	/**
	 * 随机得到一个数字0-9
	 * @return 数字0-9
	 */
	public static int nextNumber(){
		return R.nextInt(10);
	}
	
	
	/**
	 * 随机得到一个数字
	 * @param min 最小值
	 * @param max 最大值
	 * @return 数字
	 */
	public static int nextNumber(int min,int max){
		int val = R.nextInt(max);
		if(val < min){
			return val+min;
		}else{
			return val;
		}
	}
	
	/**
	 * 随机得到一个数字
	 * @param digit 数字位数
	 * @return 数字
	 */
	public static int nextNumber(String digit){
		String numberString = "";
		for(int i = 0;i < Integer.valueOf(digit);i++){
			numberString +=	R.nextInt(10);		
		}
		return Integer.parseInt(numberString);
	}
	
	/**
	 * 随机得到一个数字字符串
	 * @param digit 数字位数
	 * @return 数字字符串
	 */
	public static String nextNumberString(String digit){
		String numberString = "";
		for(int i = 0;i < Integer.valueOf(digit);i++){
			numberString +=	R.nextInt(10);		
		}
		return numberString;
	}
	
	
	/**
	 * 从给定的一个字符串数组中随机得到一个字符串
	 * @param stringArray 字符串数组
	 * @return 字符串
	 */
	public static String nextString(String[] stringArray){
		return stringArray[R.nextInt(stringArray.length)];
	}
	
	/**
	 * 随机生成时间
	 * @return 日期时间对象
	 */
	public static Date nextTime(){
		Calendar now = Calendar.getInstance();
		now.add(Calendar.YEAR, R.nextBoolean() ? R.nextInt(3):-R.nextInt(3));
		now.add(Calendar.MONTH, R.nextBoolean() ? R.nextInt(10):-R.nextInt(10));
		now.add(Calendar.DATE, R.nextBoolean() ? R.nextInt(30):-R.nextInt(30));
		now.add(Calendar.HOUR, R.nextBoolean() ? R.nextInt(8):-R.nextInt(8));
		now.add(Calendar.MINUTE, R.nextBoolean() ? R.nextInt(50):-R.nextInt(50));
		now.add(Calendar.SECOND, R.nextBoolean() ? R.nextInt(50):-R.nextInt(50));
		return now.getTime();
	}
	
	/**
	 * 随机生成车牌
	 * @return 车牌
	 */
	public static String nextPlate(){
		String[] prefix = {"粤","湘","桂","闽","浙","皖","黑","辽","吉","京","津","陕","琼"};
		return Random.nextString(prefix)+
			Random.nextCapitalLetter()+"-"+
			Random.nextNumberString("6");
	}
	
	public static void main(String[] args) {
		System.err.println(Random.nextLetter());
		System.err.println(nextNumber());		
		System.err.println(nextNumber(0,5));	
	}
}
