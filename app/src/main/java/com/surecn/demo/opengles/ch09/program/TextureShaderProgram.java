package com.surecn.demo.opengles.ch09.program;

import android.content.Context;

import com.surecn.demo.R;
import com.surecn.demo.opengles.ch09.program.ShaderProgram;

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
public class TextureShaderProgram extends ShaderProgram {

    // Uniform location
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

    // Attribute location
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.ch08_texture_vertex_shader, R.raw.ch08_texture_fragment_shader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float [] matrix, int textureId) {
        //将矩阵matrix传递给uniform
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        //激活纹理单元0
        glActiveTexture(GL_TEXTURE0);

        //将纹理textureId绑定纹理单元0
        glBindTexture(GL_TEXTURE_2D, textureId);

        //将纹理单元0传递给着色器中的u_TextureUnit
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public  int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }

}
