package com.lp.transfer.transferproject.utils;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/8/19 20:20
 */
public class MessageParse {


    /**
     *@Author :PENG
     *@Description : 获取设备 ID
     *@Date : Create in 2:14 PM 2020/7/1
     * Created by zhangpeng on 2020/7/1.
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < 16; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * ASCII码字符串转数字字符串
     *
     * @param // string
     *            ASCII字符串
     * @return 字符串
     */
    public static String AsciiStringToString(String content) {
        String result = "";
        int length = content.length() / 2;
        for (int i = 0; i < length; i++) {
            String c = content.substring(i * 2, i * 2 + 2);
            int a = hexStringToAlgorism(c);
            char b = (char) a;
            String d = String.valueOf(b);
            result += d;
        }
        return result;
    }

    /**
     * 十六进制字符串装十进制
     *
     * @param hex
     *            十六进制字符串
     * @return 十进制数值
     */
    public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }

    /**
     *@Author :PENG
     *@Description : 将高低位进行合并，并返回int类型数据
     *@Date : Create in 2:16 PM 2020/7/1
     * Created by zhangpeng on 2020/7/1.
     */
    public static int merge(byte high, byte low) {

        return (((0x000000ff & high) << 8) & 0x0000ff00) | (0x000000ff & low);

    }

    public static short unsignedByteToShort(byte b) {
        if ((b & 0x80) == 0x80){
            return (short) (128 + (b & 0x7f));
        }else{
            return (short) b;
        }
    }

}