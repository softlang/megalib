#version 330 core

/*
 * This shader receives a triangle as input and outputs 3 triangles
 */

// Input is a triangle
layout(triangles) in;

// Output a triangle strip - multiple triangles with shared vertices and edges
layout(triangle_strip, max_vertices = 9) out;

// Pass variable from vertex shader
in vec3 vPassColor[];
out vec3 passColor;

void main()
{
	/*
	 * left triangle - assign a red color
	 */
	// Vertex 0 - bottom left
    gl_Position = gl_in[0].gl_Position;
	passColor = vPassColor[0];
    EmitVertex();

	// Vertex 2 - top middle
	gl_Position = gl_in[2].gl_Position;
	passColor = vPassColor[0];
    EmitVertex();
	
	// Vertex 3 - new vertex, top left
    gl_Position = vec4(-1.f, 1.f, 0.f, 1.f);
	passColor = vPassColor[0];
    EmitVertex();

	EndPrimitive();
	
	
	/*
	 * middle triangle (as in example 01) - use a green
	 */
	// Vertex 2 - top middle
	gl_Position = gl_in[2].gl_Position;
	passColor = vPassColor[1];
    EmitVertex();

	// Vertex 0 - bottom left
    gl_Position = gl_in[0].gl_Position;
	passColor = vPassColor[1];
    EmitVertex();

	// Vertex 1 - bottom right
	gl_Position = gl_in[1].gl_Position;
	passColor = vPassColor[1];
    EmitVertex();

	EndPrimitive();

	
	/*
	 * right triangle - assign a blue color
	 */
	// Vertex 2 - top middle
    gl_Position = gl_in[2].gl_Position;
	passColor = vPassColor[2];
    EmitVertex();
	
	// Vertex 1 - bottom right
	gl_Position = gl_in[1].gl_Position;
	passColor = vPassColor[2];
    EmitVertex();
	
	// Vertex 4 - new vertex, top right
	gl_Position = vec4(1.f, 1.f, 0.f, 1.f);
	passColor = vPassColor[2];
    EmitVertex();
	
    EndPrimitive();
}