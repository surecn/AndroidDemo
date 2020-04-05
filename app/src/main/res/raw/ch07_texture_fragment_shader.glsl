precision mediump float;

//sampler2D 二维纹理数据的数组
uniform sampler2D u_TextureUnit;
varying vec2 v_TextureCoordinates;

void main() {
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);//被插值的纹理坐标和纹理数据传递给着色器
}
