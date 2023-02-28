package com.vilip22.airhockey;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

import com.vilip22.airhockey.utils.LoggerConfig;
import com.vilip22.airhockey.utils.ShaderHelper;
import com.vilip22.airhockey.utils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private final Context context;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer nativeVertexData;
    private int uColorLocation, aPositionLocation, uPointSizeLocation;
    private static final int POSITION_COMPONENT_COUNT = 2;

    public AirHockeyRenderer(Context context) {
        this.context = context;

        // Defining Java dalvik vertex data
        float[] dalvikVertexData = {
                -.5f, -.5f, .5f, .5f, -.5f, .5f, // first triangle
                -.5f, -.5f, .5f, -.5f, .5f, .5f, // second triangle
                -.5f, .0f, .5f, .0f, // middle line
                .0f, -.25f, // first mallet
                .0f, .25f, // second mallet
                .0f, .0f, // puck
                -.5f, -.5f, .5f, -.5f, // bottom line
                .5f, -.5f, .5f, .5f, // right line
                .5f, .5f, -.5f, .5f, // top line
                -.5f, .5f, -.5f, -.5f, // left line
        };

        // Copying dalvik vertex data to native vertex data
        nativeVertexData = ByteBuffer.allocateDirect(
                dalvikVertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        nativeVertexData.put(dalvikVertexData);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(.8f, 0.8f, 0.8f, 1.0f);

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(
                context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(
                context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        int program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) ShaderHelper.validateProgram(program);
        glUseProgram(program);

        uColorLocation = glGetUniformLocation(program, "u_Color");
        aPositionLocation = glGetAttribLocation(program, "a_Position");
        uPointSizeLocation = glGetUniformLocation(program, "u_PointSize");

        nativeVertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
                GL_FLOAT, false, 0, nativeVertexData);
        glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        // Table
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);

        // Middle separator
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);

        // Mallets
        glUniform1f(uPointSizeLocation, 50.0f);
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);

        // Puck
        glUniform1f(uPointSizeLocation, 10.0f);
        glDrawArrays(GL_POINTS, 10, 1);

        // Border
        glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 11, 8);
    }

}
