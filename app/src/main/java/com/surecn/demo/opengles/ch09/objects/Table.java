package com.surecn.demo.opengles.ch09.objects;

import com.surecn.demo.opengles.ch09.program.TextureShaderProgram;
import com.surecn.demo.opengles.data.VertexArray;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.surecn.demo.opengles.Constants.BYTES_PER_FLOAT;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-01-31
 * Time: 16:23
 */
public class Table {

    private static final int POSITION_COMPONENT_COUNT = 2;

    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;

    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;


    //s,t 是纹理的坐标，纹理坐标圆点在左下角    顶点坐标圆点在中心
    private static final float[] VERTEX_DATA = {
            //Order of coordinates:x,y,s,t
            //Triangle Fan
            0f,     0f,     0.5f,       0.5f,
            -0.5f,   -0.8f,  0f,         0.9f,
            0.5f,   -0.8f,  1f,         0.9f,
            0.5f,   0.8f,   1f,         0.1f,
            -0.5f,  0.8f,   0f,         0.1f,
            -0.5f,  -0.8f,  0f,         0.9f
    };

    private final VertexArray vertexArray;

    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureShaderProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                textureShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        );
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureShaderProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE
        );
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }

}
