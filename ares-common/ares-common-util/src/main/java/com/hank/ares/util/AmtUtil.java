package com.hank.ares.util;

import java.math.BigDecimal;

public class AmtUtil {
    /**
     * 四舍五入取两位小数
     *
     * @param value
     * @return
     */
    public static double retain2Decimals(double value) {
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
