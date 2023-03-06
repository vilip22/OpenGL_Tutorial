attribute vec4 a_Position;
uniform float u_PointSize;
attribute vec4 a_Color;

varying vec4 v_Color;

void main() {
    v_Color = a_Color;
    gl_PointSize = u_PointSize;
    gl_Position = a_Position;
}