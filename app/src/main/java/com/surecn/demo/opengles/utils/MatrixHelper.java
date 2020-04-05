package com.surecn.demo.opengles.utils;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-01-30
 * Time: 16:03
 */
public class MatrixHelper {

    /**
     * 创建投影矩阵
     * @param m 矩阵数组
     * @param yFovInDegrees 视野角度
     * @param aspect 屏幕的宽高比
     * @param n 到近处平面的距离
     * @param f 到远处平面的距离
     */
    public static void perspectiveM(float [] m, float yFovInDegrees, float aspect, float n, float f) {
        final float angleInRadians = (float)(yFovInDegrees * Math.PI / 180.0);
        final float a = (float)(1.0 / Math.tan(angleInRadians / 2.0));//计算焦距
        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n) / (f - n));
        m[15] = 0f;
    }
}
