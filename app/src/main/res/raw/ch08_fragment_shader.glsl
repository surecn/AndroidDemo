precision mediump float; //定义数据类型的默认精度    lowp:低     mediump:中    highp: 高

uniform vec4 u_Color;//varying 支持变化颜色

void main()
{
    //gl_FragColor 为片段设置最终颜色
    gl_FragColor = u_Color;
}