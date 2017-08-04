#version 330 core

// Input vertex data, different for all executions of this shader.
layout(location = 0) in vec3 vertexPosition_modelspace;
layout(location = 1) in vec3 vertexColor;

// output to geometry shader (and later to fragment shader)
out vec3 vPassColor;

void main(){

	// must specify the position for rasterization
    gl_Position.xyz = vertexPosition_modelspace;
    gl_Position.w = 1.0;

	// set the vertex color for interpolation during rasterization
	vPassColor = vertexColor;

}
