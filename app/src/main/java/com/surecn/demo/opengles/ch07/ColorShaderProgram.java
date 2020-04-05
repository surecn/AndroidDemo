package com.surecn.demo.opengles.ch07;

import android.content.Context;

import com.surecn.demo.R;
import com.surecn.demo.opengles.program.ShaderProgram;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-01-31
 * Time: 17:00
 */
public class ColorShaderProgram extends ShaderProgram {

    // Uniform location
    private final int uMatrixLocation;

    // Attribute location
    private final int aPositionLocation;
    private final int aColorLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.ch07_vertex_shader, R.raw.ch07_fragment_shader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
    }

    public void setUniforms(float [] matrix) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public  int getColorAttributeLocation() {
        return aColorLocation;
    }
    
}
