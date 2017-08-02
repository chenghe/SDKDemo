package com.cgnb;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encoder {
	
	private static final String[] hexDigits = { "0", "1", "2", "3", "4", "5",
		   "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	/**
	 * 生成报文自校验串
	 * @param message 报文原文
	 * @param tokenId 会话ID等
	 * @return
	 */
	public static final String encoding(String message, String tokenId){
		return encoding(encoding(message, "SHA", tokenId, "UTF-8"), "SHA", tokenId, "UTF-8");
	}
	
	/**
	 * 检查报文是否与自校验串匹配
	 * @param message 报文原文
	 * @param tokenId 会话ID等，需要与生成校验串时一致
	 * @param encodetxt 报文自校验串
	 * @return
	 */
	public static final boolean isValid(String message, String tokenId, String encodetxt){
		if(encodetxt.equals(encoding(message, tokenId))){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取加密串
	 * @param message 原始信息
	 * @param algorithm 算法(SHA,MD5)
	 * @param salt 盐
	 * @param encoding 字符集编码
	 * @return
	 */
	private static final String encoding(String message, String algorithm, String salt, String encoding){
		String result = null;
		if(null==encoding||"".equals(encoding)){
			encoding = "UTF-8";
		}
		try {
			MessageDigest mDigest = MessageDigest.getInstance(algorithm);
			String msgAndSalt = putSalt(message, salt);
			byte[] bytes = mDigest.digest(msgAndSalt.getBytes(encoding));
			
			String tmp = byteArray2HexStr(bytes);
			int half = tmp.length()/2;
			result = tmp.substring(half) + tmp.substring(0, half);
			char add = 0;
			int i = 0;
			while(add <= 5 && i < result.length()){
				add = result.charAt(i++);
			}
			result += result.substring(0, add % 9);
		} catch (NoSuchAlgorithmException e) {
			//e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 字节数组转为16进制的字符串
	 * @param bytes 原始字节数组
	 * @return
	 */
	private static final String byteArray2HexStr(byte[] bytes){
		if(null==bytes){
			return null;
		}
		StringBuffer sBuffer = new StringBuffer();
		for(int i=0;i<bytes.length;i++){
			sBuffer.append(byte2Hex(bytes[i]));
		}
		return sBuffer.reverse().toString();
	}
	
	/**
	 * 加盐
	 * @param message 原始信息
	 * @param salt 盐
	 * @return
	 */
	private static final String putSalt(String message, String salt){
		if(null==message){
			message = "";
		}
		if(null==salt || "".equals(salt)){
			return message;
		}
		return message +"{"+salt +"}";
	}
	
	/*
	private static final String byte2Hex(byte bt){
		int a = bt >= 0 ? bt : bt + 256 ;
		return hexDigits[a/16] + hexDigits[a%16];
	}*/
	
	/**
	 * 字节转为16进制
	 * @param bt 原始字节
	 * @return
	 */
	private static final String byte2Hex(byte bt){
		return hexDigits[bt >>> 4 & 0xf] + hexDigits[bt & 0xf];
	}
	
//	public static void main(String[] args){
		
//		System.out.println(encoding( "{\"tokenId\":\"6F4557B1-A721-4CBC-9DC5-A83A8CF1054F\",\"channelType\":\"MBL\"}", "SHA", null, "UTF-8"));
		
		
	//	String str = "{\n"+
	//		"    json = \"{\\\"tokenId\\\":\\\"\\\",\\\"channelType\\\":\\\"MBL\\\"}\";\n" +
	//		"}";
		//
	//	System.out.println(str);
		
	//	System.out.println(encoding(str, null));
		
//		String message = "{\"PayerAcNo\": \"623072011105000786123\", \"PayerAcName\":\"张三丰\", \"Amount\":\"124.78\", \"PayeeAcNo\":\"6755009912345688\", \"PayeeAcName\":\"李四\"}";
//		String res = encoding(message, "AB1221240FCDA2398123ACFB12B1BAB123");
//		System.out.println(res.length() + "=>>" + res);
//		System.out.println(isValid(message, "AB1221240FCDA2398123ACFB12B1BAB123","d8ebd7b3aca13de7531489b987eb3783d"));
//	}
	
}
