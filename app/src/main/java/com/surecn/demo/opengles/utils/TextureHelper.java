package com.surecn.demo.opengles.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.surecn.demo.utils.log;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-01-31
 * Time: 10:40
 */
public class TextureHelper {

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);//创建纹理对象
        if (textureObjectIds[0] == 0) {
            log.e("Could not generate a new OpenGl texture Object.");
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            log.e("Resource Id " + resourceId + "could not be decoded.");
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);//使用该纹理对象

        //确定放大或缩小时，所用的纹理过滤器
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);//缩小时使用三线性过滤
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);//放大时使用双线性过滤

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);//读入位图并且绑定到纹理对象

        bitmap.recycle();

        glGenerateMipmap(GL_TEXTURE_2D);//生成必要级别的位图

        glBindTexture(GL_TEXTURE_2D, 0);//解绑纹理对象

        return textureObjectIds[0];
    }

}
