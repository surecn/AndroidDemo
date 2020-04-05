/**
attribute:表示只读的顶点数据，只用在顶点着色器中。数据来自当前的顶点状态或者顶点数组。它必须是全局范围声明的，不能再函数内部。一个attribute可以是浮点数类型的标量，向量，或者矩阵。不可以是数组或则结构体
attribute:可以把属性放进着色器中
vec4:表示4个分量的向量 坐标x,y,z,w
*/
attribute vec4 a_Position;
attribute vec4 a_Color;

varying vec4 v_Color;

//着色器的入口点
void main()
{
    v_Color = a_Color;

    //在定点着色器中必须要为gl_Position赋值，OpenGl会把gl_Position中存储的值作为当前定点的最终位置
    gl_Position = a_Position;
    //指定点的大小
    gl_PointSize = 10.0;
}