package com.vilip22.airhockey.utils;

public class MatrixHelper {

    public static void perspectiveM(float[] matrix, float yFovDegrees, float aspect, float n, float f) {
        final float angleRadians = (float) (yFovDegrees * Math.PI / 180.0);
        final float a = (float) (1.0 / Math.tan(angleRadians / 2.0)); // focal length

        matrix[0] = a / aspect;
        matrix[1] = 0f;
        matrix[2] = 0f;
        matrix[3] = 0f;

        matrix[4] = 0f;
        matrix[5] = a;
        matrix[6] = 0f;
        matrix[7] = 0f;

        matrix[8] = 0f;
        matrix[9] = 0f;
        matrix[10] = -((f + n) / (f - n));
        matrix[11] = -1f;

        matrix[12] = 0f;
        matrix[13] = 0f;
        matrix[14] = -((2f * f * n) / (f - n));
        matrix[15] = 0f;
    }

}
