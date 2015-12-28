package com.fortunes.faas.util;

import org.apache.commons.lang.StringUtils;

/**
 * IC卡卡号转换帮助类
 * @author Leo
 * @version 2011-4-27
 */
public class CardHelper {
	
	/**
	 * 卡号转换成16进制
	 * @param cardNo
	 * @return
	 */
	public static String String2RevHex(String cardNo){
		int cardInt;
		try {
			cardInt = Integer.parseInt(cardNo);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("卡号格式不正确，请查证后再输入");
		}
		String dd = Integer.toHexString(cardInt);
		String ret = StringUtils.leftPad(dd, 10, "0");
		
		String[] array = new String[5];
		int k = 0;
		for(int i=0;i<5;i++){
			array[i] = ret.substring(k, k+2);
			k +=2;
		}
		String cardHex = "";
		for(int j=4;j>-1;j--){
			cardHex +=array[j];
		}
		return cardHex;
	}
	/**
	 * 门襟库卡号转换成实际IC卡号
	 * @param cardNo
	 * @return
	 */
	public static String cardNo2CardNo(String cardNo){
		String cardStr = cardNo2Hex(cardNo);
		int card = Integer.parseInt(cardStr,16);
		return card+"";
	}
	public static String cardNo2Hex(String cardNo){
		String front = cardNo.substring(0, 3);
		String back = cardNo.substring(3);
		int cardFront,cardBack;
		try{
			cardFront = Integer.parseInt(front);
			cardBack = Integer.parseInt(back);
		}catch(NumberFormatException e){
			throw new NumberFormatException("卡号格式不正确，请查证后再输入");
		}
		String cardFrontHex = Integer.toHexString(cardFront);
		String cardFrontHexRet = StringUtils.leftPad(cardFrontHex, 2, "0");
		
		String cardBackHex = Integer.toHexString(cardBack);
		String cardBackHexRet = StringUtils.leftPad(cardBackHex, 4, "0");
		return cardFrontHexRet+cardBackHexRet;
	}
	
	public static void main(String[] args) {
		String rs = cardNo2Hex("04644213");
		String rs2 = cardNo2CardNo("04644213");
		System.out.println(rs);
		System.out.println(rs2);
	}
}
