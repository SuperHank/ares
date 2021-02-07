package com.hank.ares.util;

import java.math.BigDecimal;
import java.util.Random;

public class CommonUtil {
    /**
     * 获取指定位数 数字类型随机数
     *
     * @param length 长度
     * @return 随机数
     */
    public static String getRandomNumCode(int length) {
        StringBuilder val = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val.append(String.valueOf(random.nextInt(10)));
        }
        return val.toString();
    }

    /**
     * 获取指定位数 英文随机数
     *
     * @param length 长度
     * @return 随机数
     */
    public static String getRandomStrCode(int length) {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder val = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val.append(str.charAt(random.nextInt(26)));
        }
        return val.toString();
    }

    /**
     * long 除 1000 转 double
     *
     * @param value long
     * @return 随机数
     */
    public static Double getDoubleByLong(Long value) {
        BigDecimal divide = new BigDecimal(value)
                .divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP);

        return divide.doubleValue();
    }

    /**
     * 金额转换
     *
     * @param amt
     * @return
     */
    public static String calcAmt(Long amt) {
        BigDecimal a = new BigDecimal(amt != null ? amt.toString() : "0");
        return a.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP).toString();
    }
}
