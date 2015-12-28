package net.fortunes.util;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.apache.commons.lang.StringUtils;

public class PinYin {
	public static String toPinYinString(char c) {
		String[] pyChars = null;
		try {
			pyChars = PinyinHelper.toHanyuPinyinStringArray(c);
			if(pyChars != null && pyChars.length >= 1){
				return pyChars[0].charAt(0)+"";
			}else if(StringUtils.isAlpha(c+"") || StringUtils.isNumeric(c+"")){
				return c+"";
			}else{
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
    }

    /**
     * 取得汉字拼音缩写
     *
     * @param str 汉字
     * @return 拼音缩写大写
     */
    public static String toPinYinString(String str) {
    	String ret = "";
        for(char c : str.toCharArray()){
        	ret += toPinYinString(c);
        }
        return ret.toUpperCase();
    }
    
    public static void main(String[] args) {
		System.out.println(PinYin.toPinYinString("廖清平23"));
		System.out.println(PinYin.toPinYinString("东莞rr"));
		System.out.println(PinYin.toPinYinString("shzy"));
		System.out.println(PinYin.toPinYinString("123@shzy"));
	}
}
