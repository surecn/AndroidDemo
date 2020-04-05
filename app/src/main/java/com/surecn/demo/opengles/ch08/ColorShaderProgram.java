package com.surecn.demo.opengles.ch08;

import android.content.Context;

import com.surecn.demo.R;
import com.surecn.demo.opengles.program.ShaderProgram;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-01-31
 * Time: 17:00
 */
public class ColorShaderProgram extends ShaderProgram {

    // Uniform location
    private final int uMatrixLocation;

    private final int uColorLocation;

    // Attribute location
    private final int aPositionLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.ch08_vertex_shader, R.raw.ch08_fragment_shader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        uColorLocation = glGetUniformLocation(program, U_COLOR);
    }

    public void setUniforms(float [] matrix, float r, float g, float b) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform4f(uColorLocation, r, g, b, 1f);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    
}
