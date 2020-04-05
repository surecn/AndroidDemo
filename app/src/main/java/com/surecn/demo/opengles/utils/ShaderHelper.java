package com.surecn.demo.opengles.utils;

import android.os.Build;

import com.surecn.demo.BuildConfig;
import com.surecn.demo.utils.log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-13
 * Time: 19:05
 */
public class ShaderHelper {

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {

        final int shaderObjectId = glCreateShader(type);//创建着色器对象
        if (shaderObjectId == 0) {
            log.e("Could not create new shader.");
            return 0;
        }
        glShaderSource(shaderObjectId, shaderCode);//绑定着色器源代码
        glCompileShader(shaderObjectId);//编译着色器源代码
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);//取出编译结果
        log.v("Results of compiling source:\n"+ shaderCode + ":" + glGetShaderInfoLog(shaderObjectId));
        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectId);
            log.e("Compilation of shader failed");
            return 0;
        }

        return shaderObjectId;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programObjectId = glCreateProgram();//新建程序对象
        if (programObjectId == 0) {
            log.e("Could not create new program");
            return 0;
        }
        glAttachShader(programObjectId, vertexShaderId);//附加顶点着色器
        glAttachShader(programObjectId, fragmentShaderId);//附加片段着色器
        glLinkProgram(programObjectId);//链接

        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
        log.v("Results of linking program:" + glGetProgramInfoLog(programObjectId));
        if (linkStatus[0] == 0) {
            glDeleteShader(programObjectId);
            log.e("Linking of program failed.");
            return 0;
        }
        return programObjectId;
    }

    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);//验证程序

        final int[] validateStautus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStautus, 0);
        log.v("Results of validating program:" + validateStautus[0] + "  nLog:" + glGetProgramInfoLog(programObjectId));
        return validateStautus[0] != 0;
    }

    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource) {
        int program;

        // Compile the shaders.
        int vertexShader = compileVertexShader(vertexShaderSource);
        int framentShader = compileFragmentShader(fragmentShaderSource);

        // Link them into a shader program.
        program = linkProgram(vertexShader, framentShader);

        if (BuildConfig.DEBUG) {
            validateProgram(program);
        }

        return program;
    }


}
