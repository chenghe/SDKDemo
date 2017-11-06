package com.zsmarter.hechengbin.sdkdemo.securityplugin.util;


import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSA {

	public static final String KEY_ALGORTHM = "RSA";//
    public static final String CIPHER_KEY = "RSA/ECB/PKCS1Padding";

	public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

	public static final String PUBLIC_KEY = "RSAPublicKey";// 公钥
	public static final String PRIVATE_KEY = "RSAPrivateKey";// 私钥
    /** *//**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    /** *//**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 初始化密钥
     * @return
     * @throws Exception
     */
    public static Map<String,Object> initKey()throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORTHM);
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
         
        //公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        //私钥
        RSAPrivateKey privateKey =  (RSAPrivateKey) keyPair.getPrivate();
         
        Map<String,Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
         
        return keyMap;
    }

    /**
     * 取得公钥，并转化为String类型
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)throws Exception{
        Key key = (Key) keyMap.get(PUBLIC_KEY);  
        return Coder.encryptBASE64(key.getEncoded());
    }
 
    /**
     * 取得私钥，并转化为String类型
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception{
        Key key = (Key) keyMap.get(PRIVATE_KEY);  
        return Coder.encryptBASE64(key.getEncoded());     
    }

    /**
     * 用私钥加密
     * @param data   加密数据
     * @param key    密钥
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data,String key)throws Exception{
        //解密密钥
        byte[] keyBytes = Coder.decryptBASE64(key);
        //取私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        //对数据加密
        Cipher cipher = Cipher.getInstance(CIPHER_KEY);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }
//
//    /** *//**
//     * <p>
//     * 私钥加密
//     * </p>
//     *
//     * @param data 源数据
//     * @param privateKey 私钥(BASE64编码)
//     * @return
//     * @throws Exception
//     */
//    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
//            throws Exception {
//        byte[] keyBytes = Base64Utils.decode(privateKey);
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
//        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//        cipher.init(Cipher.ENCRYPT_MODE, privateK);
//        int inputLen = data.length;
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        int offSet = 0;
//        byte[] cache;
//        int i = 0;
//        // 对数据分段加密
//        while (inputLen - offSet > 0) {
//            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
//                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
//            } else {
//                cache = cipher.doFinal(data, offSet, inputLen - offSet);
//            }
//            out.write(cache, 0, cache.length);
//            i++;
//            offSet = i * MAX_ENCRYPT_BLOCK;
//        }
//        byte[] encryptedData = out.toByteArray();
//        out.close();
//        return encryptedData;
//    }

    /**
     * 用私钥解密 * @param data    加密数据
     * @param key    密钥
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data,String key)throws Exception{
        //对私钥解密
        byte[] keyBytes = Coder.decryptBASE64(key);

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        //对数据解密
        Cipher cipher = Cipher.getInstance(CIPHER_KEY);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }
//    /** *//**
//     * <P>
//     * 私钥解密
//     * </p>
//     *
//     * @param encryptedData 已加密数据
//     * @param privateKey 私钥(BASE64编码)
//     * @return
//     * @throws Exception
//     */
//    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
//            throws Exception {
//        byte[] keyBytes = Base64Utils.decode(privateKey);
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
//        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//        cipher.init(Cipher.DECRYPT_MODE, privateK);
//        int inputLen = encryptedData.length;
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        int offSet = 0;
//        byte[] cache;
//        int i = 0;
//        // 对数据分段解密
//        while (inputLen - offSet > 0) {
//            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
//                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
//            } else {
//                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
//            }
//            out.write(cache, 0, cache.length);
//            i++;
//            offSet = i * MAX_DECRYPT_BLOCK;
//        }
//        byte[] decryptedData = out.toByteArray();
//        out.close();
//        return decryptedData;
//    }
    /**
     * 用公钥加密
     * @param data   加密数据
     * @param key    密钥
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data,String key)throws Exception{
        //对公钥解密
        byte[] keyBytes = Coder.decryptBASE64(key);
        //取公钥
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        //对数据解密
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(CIPHER_KEY);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

//    /** *//**
//     * <p>
//     * 公钥加密
//     * </p>
//     *
//     * @param data 源数据
//     * @param publicKey 公钥(BASE64编码)
//     * @return
//     * @throws Exception
//     */
//    public static byte[] encryptByPublicKey(byte[] data, String publicKey)
//            throws Exception {
//        byte[] keyBytes = Base64Utils.decode(publicKey);
//        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
//        Key publicK = keyFactory.generatePublic(x509KeySpec);
//        // 对数据加密
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//        cipher.init(Cipher.ENCRYPT_MODE, publicK);
//        int inputLen = data.length;
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        int offSet = 0;
//        byte[] cache;
//        int i = 0;
//        // 对数据分段加密
//        while (inputLen - offSet > 0) {
//            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
//                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
//            } else {
//                cache = cipher.doFinal(data, offSet, inputLen - offSet);
//            }
//            out.write(cache, 0, cache.length);
//            i++;
//            offSet = i * MAX_ENCRYPT_BLOCK;
//        }
//        byte[] encryptedData = out.toByteArray();
//        out.close();
//        return encryptedData;
//    }

//    /**
//     * 用公钥解密
//     * @param data   加密数据
//     * @param key    密钥
//     * @return
//     * @throws Exception
//     */
//    public static byte[] decryptByPublicKey(byte[] data,String key)throws Exception{
//        //对私钥解密
//        byte[] keyBytes = Coder.decryptBASE64(key);
//        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
//        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
//
//        //对数据解密
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//        cipher.init(Cipher.DECRYPT_MODE, publicKey);
//
//        return cipher.doFinal(data);
//    }
    /** *//**
     * <p>
     * 公钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(CIPHER_KEY);
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }


    /**
     * 用私钥对信息生成数字签名
     * @param data   //加密数据
     * @param privateKey //私钥
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data,String privateKey)throws Exception{
        //解密私钥
        byte[] keyBytes = Coder.decryptBASE64(privateKey);
        //构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        //指定加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        //取私钥匙对象
        PrivateKey privateKey2 = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        //用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey2);
        signature.update(data);
         
        return Coder.encryptBASE64(signature.sign());
    }
    /**
     * 校验数字签名
     * @param data   加密数据
     * @param publicKey  公钥
     * @param sign   数字签名
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data,String publicKey,String sign)throws Exception{
        //解密公钥
        byte[] keyBytes = Coder.decryptBASE64(publicKey);
        //构造X509EncodedKeySpec对象
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        //指定加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        //取公钥匙对象
        PublicKey publicKey2 = keyFactory.generatePublic(x509EncodedKeySpec);
         
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey2);
        signature.update(data);
        //验证签名是否正常
        return signature.verify(Coder.decryptBASE64(sign));
         
    }

    public static String bytes2Hex(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] res = new char[src.length * 2]; // 每个byte对应两个字符
        final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >> 4 & 0x0f]; // 先存byte的高4位
            res[j++] = hexDigits[src[i] & 0x0f]; // 再存byte的低4位
        }

        return new String(res);
    }

    public static String bytesToString(byte[] encrytpByte) {
        String result = "";
        for (Byte bytes : encrytpByte) {
            result += bytes.toString() + " ";
        }
        return result;
    }

    public static byte[] StringyoByte(String tmp1) {
        String[] strArr = tmp1.split(" ");
        int len = strArr.length;
        byte[] clone = new byte[len];
        for (int i = 0; i < len; i++) {
            clone[i] = Byte.parseByte(strArr[i]);
        }
        return clone;
    }

    public static void main(String[] args) {
		RSA rsa = new RSA();
		try {
			Map<String,Object> map = rsa.initKey();
			String publicKey = rsa.getPublicKey(map);
			System.out.println(publicKey);
			String privateKey = rsa.getPrivateKey(map);
			System.out.println(privateKey);
			BASE64Encoder base64Encoder = new BASE64Encoder();
//			String sign = RSA.sign("123456789".getBytes(Globals.defaultCharset), privateKey);
//
//			boolean flag = RSA.verify("123456789".getBytes(Globals.defaultCharset), publicKey, sign);
//
//			System.out.println(flag);
            String data="hello";
            byte[] signedData=encryptByPrivateKey(data.getBytes(Globals.defaultCharset),privateKey);
            String signedString=rsa.bytesToString(signedData);
            System.out.println(signedString);
            byte[] signedData2=rsa.StringyoByte(signedString);
//            System.out.println(signedString);
            byte[] unsignedDATA=decryptByPublicKey(signedData2,publicKey);
//            byte[] unsignedDATA=decryptByPublicKey(signedData,publicKey);
            System.out.println(new String(unsignedDATA));

        } catch (Exception e) {
			e.printStackTrace();
		}
	}

}
