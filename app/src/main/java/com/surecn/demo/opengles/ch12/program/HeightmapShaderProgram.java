package com.surecn.demo.opengles.ch12.program;

import android.content.Context;

import com.surecn.demo.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class HeightmapShaderProgram extends ShaderProgram{

    private final int uMatrixLocation;
    private final int aPositionLocation;

    public HeightmapShaderProgram(Context context) {
        super(context, R.raw.ch12_heightmap_vertex_shader, R.raw.ch12_heightmap_fregment_shader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    public void setUniforms(float[] matrix) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);
    }


    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}
