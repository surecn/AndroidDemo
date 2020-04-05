precision mediump float; //定义数据类型的默认精度    lowp:低     mediump:中    highp: 高

varying vec4 v_Color;//varying 支持变化颜色

void main()
{
    //gl_FragColor 为片段设置最终颜色
    gl_FragColor = v_Color;
}