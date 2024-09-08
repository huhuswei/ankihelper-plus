package com.mmjang.ankihelper.util;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.util
 * @ClassName: MathUtil
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/10/8 9:45 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/10/8 9:45 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MathUtil {
    /**
     * 判断一个int整数的位数.
     *
     * @param a 待验证数值.
     * @return 待验证数值 的 位数.
     */
    public static int digits( int a ) {
        if ( a == Integer.MIN_VALUE ) return 10;
        if ( a < 0 ) return digits( -a );
        if ( a < 10) return 1;
        if ( a < 100) return 2;
        if ( a < 1000) return 3;
        if ( a < 10000) return 4;
        if ( a < 100000) return 5;
        if ( a < 1000000) return 6;
        if ( a < 10000000) return 7;
        if ( a < 100000000) return 8;
        if ( a < 1000000000) return 9;
        return 10;
    }}
