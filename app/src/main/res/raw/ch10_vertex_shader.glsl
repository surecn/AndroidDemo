uniform mat4 u_Matrix;
uniform float u_Time;

attribute vec3 a_Position;
attribute vec3 a_Color;
attribute vec3 a_DirectionVector;
attribute float a_ParticleStartTime;

varying vec3 v_Color;
varying float v_ElapsedTime;


void main()
{
    v_Color = a_Color;
    //计算粒子存活年龄
    v_ElapsedTime = u_Time - a_ParticleStartTime;
    //根据粒子年龄更新位置
    vec3 currentPosition = a_Position + (a_DirectionVector * v_ElapsedTime);
    float gravityFactor = v_ElapsedTime * v_ElapsedTime / 8.0;//计算重力加速因子
    currentPosition.y -=gravityFactor;

    gl_Position = u_Matrix * vec4(currentPosition, 1.0);
    gl_PointSize = 25.0;
}