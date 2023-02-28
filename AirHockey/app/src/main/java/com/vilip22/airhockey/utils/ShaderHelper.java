package com.vilip22.airhockey.utils;

import static android.opengl.GLES20.*;

import android.util.Log;

public class ShaderHelper {

    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    public static int compileShader(int type, String shaderCode) {
        final int shaderObjectID = glCreateShader(type);
        if (shaderObjectID == 0) {
            if (LoggerConfig.ON) Log.w(TAG, "Could not create new shader.");
            return 0;
        }

        glShaderSource(shaderObjectID, shaderCode);
        glCompileShader(shaderObjectID);

        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectID, GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectID);
            if (LoggerConfig.ON) Log.w(TAG, "Compilation of shader failed.");
            return 0;
        }

        return shaderObjectID;
    }

    public static int linkProgram(int vertexShaderID, int fragmentShaderID) {
        final int programObjectID = glCreateProgram();
        if (programObjectID == 0) {
            if (LoggerConfig.ON) Log.w(TAG, "Could not create a new program.");
            return 0;
        }

        glAttachShader(programObjectID, vertexShaderID);
        glAttachShader(programObjectID, fragmentShaderID);
        glLinkProgram(programObjectID);

        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectID, GL_LINK_STATUS, linkStatus, 0);

        if (LoggerConfig.ON) {
            Log.v(TAG, "Results of linking program:\n"
                    + glGetProgramInfoLog(programObjectID));
        }
        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectID);
            if (LoggerConfig.ON) Log.w(TAG, "Linking of program failed.");
            return 0;
        }

        return programObjectID;
    }

    public static boolean validateProgram(int programObjectID) {
        glValidateProgram(programObjectID);
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectID, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.v(TAG, "Results of validating program: "
                + validateStatus[0] + "\nLog:" + glGetProgramInfoLog(programObjectID));
        return validateStatus[0] != 0;
    }

}
