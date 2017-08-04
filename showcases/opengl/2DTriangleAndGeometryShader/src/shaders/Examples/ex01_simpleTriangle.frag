#version 330 core

// Input from vertex shader
in vec3 passColor;

// Ouput data
out vec3 color;

void main()
{

	// Output color = interpolated vertex color
	color = passColor;
	//color = vec3(1.f, 0.f, 0.f);

}