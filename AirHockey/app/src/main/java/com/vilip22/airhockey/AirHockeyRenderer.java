package com.vilip22.airhockey;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

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
    private final float[] projectionMatrix = new float[16];
    private int uPointSizeLocation, uMatrixLocation;
    private int aColorLocation, aPositionLocation;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    public AirHockeyRenderer(Context context) {
        this.context = context;

        // Defining Java dalvik vertex data
        float[] dalvikVertexData = {
                // Triangle fan - order of coordinates: x, y, R, G, B
                .0f, .0f, .75f, .75f, .75f,
                -.5f, -.8f, 1f, 1f, 1f,
                .5f, -.8f, .5f, .5f, .5f,
                .5f, .8f, .5f, .5f, .5f,
                -.5f, .8f, 1f, 1f, 1f,
                -.5f, -.8f, 1f, 1f, 1f,

                // Border:
                -.5f, -.8f, .0f, .0f, .0f, .5f, -.8f, .0f, .0f, .0f, // bottom line
                .5f, -.8f, .0f, .0f, .0f, .5f, .8f, .0f, .0f, .0f, // right line
                .5f, .8f, .0f, .0f, .0f, -.5f, .8f, .0f, .0f, .0f,// top line
                -.5f, .8f, .0f, .0f, .0f, -.5f, -.8f, .0f, .0f, .0f, // left line
                -.5f, .0f, 1f, .0f, .0f, .5f, .0f, 1f, .0f, .0f, // middle line

                .0f, -.4f, .0f, .0f, 1f, // first mallet
                .0f, .4f, 1f, .0f, .0f, // second mallet
                .0f, .0f, 1f, .0f, .0f // puck
        };

        // Copying dalvik vertex data to native vertex data
        nativeVertexData = ByteBuffer.allocateDirect(
                dalvikVertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        nativeVertexData.put(dalvikVertexData);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(1f, 1f, 1f, 1f);

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(
                context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(
                context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        int program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) ShaderHelper.validateProgram(program);
        glUseProgram(program);

        aColorLocation = glGetAttribLocation(program, "a_Color");
        aPositionLocation = glGetAttribLocation(program, "a_Position");
        uPointSizeLocation = glGetUniformLocation(program, "u_PointSize");
        uMatrixLocation = glGetUniformLocation(program, "u_Matrix");

        nativeVertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE, nativeVertexData);
        glEnableVertexAttribArray(aPositionLocation);

        nativeVertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE, nativeVertexData);
        glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);

        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height)
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        else
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        // Table
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        // Border
        glDrawArrays(GL_LINES, 6, 10);

        // Mallets
        glUniform1f(uPointSizeLocation, 50f);
        glDrawArrays(GL_POINTS, 16, 1);
        glDrawArrays(GL_POINTS, 17, 1);

        // Puck
        glUniform1f(uPointSizeLocation, 10f);
        glDrawArrays(GL_POINTS, 18, 1);

    }

}
