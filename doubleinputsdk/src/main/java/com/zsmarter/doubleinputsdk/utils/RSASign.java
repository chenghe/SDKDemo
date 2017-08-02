package com.zsmarter.doubleinputsdk.utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

 
 
public class RSASign {
 
    public static final String KEY_ALGORITHM = "RSA";
    private static final String PUBLIC_KEY = "PublicKey";
    private static final String PRIVATE_KEY = "PrivateKey";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA"; 
 
    /**
     * 生成密钥对
     * 
     * @return
     * @throws Exception
     */
    public static Map<String, Object> generateKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }
 
    /**
     * 签名
     * 
     * @param priKey
     * @param data
     * @return
     * @throws Exception
     */
    public static String sign(byte[] priKey, byte[] data)
            throws Exception {  
        // 构造PKCS8EncodedKeySpec对象  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);  
        // KEY_ALGORITHM 指定的加密算法  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        // 取私钥匙对象  
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);  
        // 用私钥对信息生成数字签名  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initSign(privateKey);  
        signature.update(data); 
        return Base64Utils.encode(signature.sign());  
    } 
    
    /**
     * @param pubKey
     * @param data
     * @param sign
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] pubKey, byte[] data, String sign)  
            throws Exception {  
        // 构造X509EncodedKeySpec对象  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKey);  
        // KEY_ALGORITHM 指定的加密算法  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        // 取公钥匙对象  
        PublicKey publicKey = keyFactory.generatePublic(keySpec);  
         
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initVerify(publicKey);  
        signature.update(data);  
        // 验证签名是否正常  
        return signature.verify(Base64Utils.decode(sign));  
    }  
     
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String src = "/system/";
        //==================生成密钥对==================
//        Map<String, Object> keyMap = RSASign.generateKey();
//        RSAPublicKey pubKey = (RSAPublicKey) keyMap.get(RSASign.PUBLIC_KEY);
//        System.out.println(pubKey);
//        RSAPrivateKey priKey = (RSAPrivateKey) keyMap.get(RSASign.PRIVATE_KEY);
//        System.out.println(priKey);
        //==================私钥签名==================
        String sign = RSASign.sign(Keys.decryptBASE64("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJu62uh36VsXAxtIH1elSPU2Ml1k"+
        		"vmnviIPGLQoNDyk0S5UjsUfnsmujpdDn9GHuUbMeG9ZEqcGD3+iISjhIIYHdaJw6eRyDcq3BlHj8"+
        		"0K7EbTHcUSX9LwZNSQle5IaLvrn9sU0xrksqHnzY3tjXvogutUToJzmLs8O3aeTPhi53AgMBAAEC"+
        		"gYA1W52b31ImcskOtPSAPFK2Kf6xv0360CbPN7zA396c+J0ZGaSphXq9pNGwtXkngNefbqaNEPO1"+
        		"KXEzpFcB170az5lmGov6jbRpJYecaBofUjz85sPLq6dfuUtdxnR1jDv+CjUY6KEZqj1YosOku98E"+
        		"inR2QlERam9hgmCzB9GfCQJBANpXvsuZotJBxiXA0zCXNjtgwEcCpjYa2V32/NGAF3/VL5PMhZsI"+
        		"cvrrbderjJGmYf5pIoTbPvYpjFIPl+URzF0CQQC2lqGgU+WdF5vIvIQ/xlThsiwcZULZC62DrNqV"+
        		"6tLiiI2GEwfMtZLsBvCWMP1F+MUkPE1b83qs4RP6IL7klljjAkEA1laSzu2oH70/1enfp/CY5VtK"+
        		"Bat9HNnojkyazNJwvyW5Sd7ZPWLi9J1OvAwEtypdQlFU+JT9zsrkm94A33dEZQJAEm6yclUF4Q35"+
        		"FBKz5xxGhWJPQa6XSQH8ykYw6uGB2IygfOB+8RODeYBZ2U+owd+TR00vIhE6WSl/ssiLfNgQsQJB"+
        		"AJaMkwJQ0VuO+aFoeLonjsKbcNotuSk01pxfzhACwGD8hHh6U/E3qZpTeuYHWaph+sdKvT6HPAiS"+
        		"i2Svavjtvmc="), src.getBytes()); 
        System.out.println("签名结果： " + sign);
        //==================公钥验证签名==================
        boolean result = RSASign.verify(Keys.decryptBASE64("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbutrod+lbFwMbSB9XpUj1NjJdZL5p74iDxi0K"+
        		"DQ8pNEuVI7FH57Jro6XQ5/Rh7lGzHhvWRKnBg9/oiEo4SCGB3WicOnkcg3KtwZR4/NCuxG0x3FEl"+
        		"/S8GTUkJXuSGi765/bFNMa5LKh582N7Y176ILrVE6Cc5i7PDt2nkz4YudwIDAQAB"), src.getBytes(), sign);
        System.out.println("验证签名结果： " + result);
    }
 
}

