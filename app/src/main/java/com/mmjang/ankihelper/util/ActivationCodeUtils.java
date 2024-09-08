package com.mmjang.ankihelper.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivationCodeUtils {
    private static final String USERNAME = "hello world";

    /**
     * 生成本地注册码
     *
     * @param deviceId 设备ID
     * @return 本地注册码
     */
    public static String generateLocalRegistrationCode(String username, String deviceId) {
        if(username== null || username.equals(""))
            return null;
        // 在设备ID和密钥后面拼接字符串
        String textToHash = deviceId + username;

        try {
            // 使用SHA-256算法计算哈希值
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(textToHash.getBytes());

            // 将哈希值转化为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String interleaveStrings(String a, String b, char pre) {
        if (a.length() < b.length()) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        int i = 0;
        int x = pre;
        while (i < b.length()) {
            // 从 a 中添加字符
            result.append((char)(a.charAt(i)^x--));
            // 从 b 中添加字符
            result.append((char)(b.charAt(i)^x--));
            i++;
        }
        // 将剩余的字符从 a 中添加到结果中
        while(i < b.length()) {
            result.append((char)(a.charAt(i)^x--));
            i++;
        }
        return pre + result.toString();
    }

    public static String[] splitInterleavedString(String interleavedString, int bLength, char pre) {
        StringBuilder aBuilder = new StringBuilder();
        StringBuilder bBuilder = new StringBuilder();

        int i = 0;
        int x = pre;
        while (i + 1 < interleavedString.length()) {
            if (bBuilder.length() < 8) {
                // 将剩余字符添加到字符串 a 中
                aBuilder.append((char)(interleavedString.charAt(i++)^x--));
                bBuilder.append((char)(interleavedString.charAt(i++)^x--));
            } else {
                aBuilder.append((char)(interleavedString.charAt(i++)^x--));
            }
        }

        String a = aBuilder.toString();
        String b = bBuilder.toString();
        return new String[]{ a, b };
    }


    /**
     * 根据本地注册码生成激活码
     *
     * @param localRegistrationCode 本地注册码
     * @return 激活码
     */
    public static String generateActivationCode(String username, String localRegistrationCode, String dateStr, char pre) {
        if(username == null || username.equals(""))
            return null;
        if(dateStr == null || !isValidDate(dateStr))
            return null;

        // 对本地注册码进行再次哈希
        String textToHash = localRegistrationCode + username;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(textToHash.getBytes());

            // 取哈希值前8个字节作为激活码
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                sb.append(String.format("%02X", hash[i]));
            }
            return interleaveStrings(sb.toString(), dateStr, pre);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证激活码是否正确
     *
     * @param deviceId      设备ID
     * @param activationCode 激活码
     * @return true表示激活码正确，false表示激活码错误
     */
    public static boolean verifyActivationCode(String username, String deviceId, String activationCode) {
        if(username == null || username.equals(""))
            return false;
        // 生成本地注册码
        String localRegistrationCode = generateLocalRegistrationCode(username, deviceId);
        if (localRegistrationCode == null) {
            return false;
        }

        // 检验日期是否正确
        Date curDate = new Date();
        String[] data = splitInterleavedString(activationCode.substring(1), 8, activationCode.charAt(0));
        Date date = getDate(data[1]);
        if(date == null) {
            return false;
        }
        // 验证激活码是否正确
        int result = date.compareTo(curDate);
        if (result >= 0) {
            String expectedActivationCode = generateActivationCode(username, localRegistrationCode, data[1], activationCode.charAt(0));
            if (expectedActivationCode == null) {
                return false;
            }
            return expectedActivationCode.equalsIgnoreCase(activationCode);
        } else {
            return false;
        }
    }

    public static boolean isValidDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setLenient(false);
        try {
            // 尝试将字符串解析为日期
            Date date = dateFormat.parse(dateStr);
            // 检查解析后的日期是否与原始字符串相等
            return dateStr.equals(dateFormat.format(date));
        } catch (Exception e) {
            // 解析失败，说明不是有效的日期
            return false;
        }
    }

    public static Date getDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setLenient(false);
        try {
            // 尝试将字符串解析为日期
            Date date = dateFormat.parse(dateStr);
            // 检查解析后的日期是否与原始字符串相等
            if(dateStr.equals(dateFormat.format(date)))
                return date;
            else
                return null;
        } catch (Exception e) {
            // 解析失败，说明不是有效的日期
            return null;
        }
    }

//    public static void main(String[] args) {
//        String deviceId = "c55ceb13c6abd7b";
////        LocalDate date = LocalDate.now().plusDays(30);
////        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
////        String dateStr = date.format(formatter);
//        String dateStr = "20230404";
//        // 生成本地注册码
//        String localRegistrationCode = ActivationCodeUtils.generateLocalRegistrationCode(USERNAME, deviceId);
//        System.out.println("Expiration date: " + dateStr);
//        System.out.println("Local registration code: " + localRegistrationCode);
//
//        // 根据本地注册码生成激活码
//        final int pre = 0x26ff + (int) (Math.random() *(0x27ff - 0x26ff + 1));
//        String activationCode = ActivationCodeUtils.generateActivationCode(USERNAME, localRegistrationCode, dateStr, (char) pre);
//        System.out.println("Activation code: " + activationCode);
//        System.out.println("Split date: " + splitInterleavedString(activationCode.substring(1), 8, activationCode.charAt(0))[1]);
//
//        // 验证激活码是否正确
//        boolean isActivated = ActivationCodeUtils.verifyActivationCode(USERNAME, deviceId, activationCode);
//        if (isActivated) {
//            System.out.println("Activation succeeded.");
//        } else {
//            System.out.println("Activation failed.");
//        }
//    }

}


