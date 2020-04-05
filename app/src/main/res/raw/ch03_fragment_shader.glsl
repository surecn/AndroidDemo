precision mediump float; //定义数据类型的默认精度    lowp:低     mediump:中    highp: 高

//uniform会让每个定点使用同一个值  vec4 四个分量的向量 在颜色里代表 RGBA
uniform vec4 u_Color;

void main()
{
    //gl_FragColor 为片段设置最终颜色
    gl_FragColor = u_Color;
}