package com.mmjang.ankihelper.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.Cipher;

public class ActivationCodeUtils2 {

    private static final int ACTIVATION_CODE_LENGTH = 16;
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    static {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    // 生成密钥对
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024); // 指定密钥长度为1024位
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // 获取公钥
    public static PublicKey getPublic(KeyPair keyPair) {
        return keyPair.getPublic();
    }

    // 获取私钥
    public static PrivateKey getPrivate(KeyPair keyPair) {
        return keyPair.getPrivate();
    }

    // 使用公钥生成激活码
    public static String generateActivationCode(PublicKey publicKey) {
        try {
            // 在此处添加需要加密的原始数据
            String rawData = "Hello, world!";

            // 使用 Cipher 类的 RSA 加密模式来加密数据
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedData = cipher.doFinal(rawData.getBytes());

            // 将加密后的数据进行 Base64 编码，得到最终的激活码
            return new String(Base64.getUrlEncoder().encode(encryptedData));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 使用私钥验证激活码
    public static boolean verifyActivationCode(String code, PrivateKey privateKey) {
        try {
            // 对激活码进行 Base64 解码，还原出加密后的数据
            byte[] input = Base64.getUrlDecoder().decode(code.getBytes());

            // 使用 Cipher 类的 RSA 解密模式来解密数据
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedData = cipher.doFinal(input);

            // 在此处检查原始数据是否有效，并返回验证结果
            String plaintext = new String(decryptedData);
            return "Hello, world!".equals(plaintext);
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        // 生成密钥对
        KeyPair keyPair = ActivationCodeUtils2.generateKeyPair();
        PublicKey publicKey = ActivationCodeUtils2.getPublic(keyPair);
        PrivateKey privateKey = ActivationCodeUtils2.getPrivate(keyPair);

        // 使用公钥生成激活码
        String code = ActivationCodeUtils2.generateActivationCode(publicKey);
        System.out.println("生成的激活码为：" + code);

        // 使用私钥验证激活码
        boolean success = ActivationCodeUtils2.verifyActivationCode(code, privateKey);
        if (success) {
            System.out.println("激活成功！");
        } else {
            System.out.println("激活失败！");
        }
    }
}