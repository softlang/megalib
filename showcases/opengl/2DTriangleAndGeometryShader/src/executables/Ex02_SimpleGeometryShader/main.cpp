
/******************************************************************************************************
 * Based on Ex01_SimpleTriangle
 * Code in parts from https://github.com/opengl-tutorials/ogl/tree/master/tutorial02_red_triangle
 * and https://open.gl/geometry (for the geometry shader)
 *****************************************************************************************************/


// Include standard headers
#include <stdio.h>

// Include GLEW - OpenGL binding library
#include <GL/glew.h>

// Include GLFW - creates and manages a window
#include <GLFW/glfw3.h>
GLFWwindow* window;

// Include GLM - for math operations
#include <glm/glm.hpp>


// Include helper file to create a shader program
#include "Common/Shader.hpp"


int main(void)
{
	// Initialise GLFW
	if (!glfwInit())
	{
		fprintf(stderr, "Failed to initialize GLFW\n");
		getchar();
		return -1;
	}

	#ifdef __APPLE__
		glfwWindowHint(GLFW_SAMPLES, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // To make MacOS happy; should not be needed
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
	#endif

	// Open a window and create its OpenGL context
	window = glfwCreateWindow(1024, 768, "Example01 - Triangle", NULL, NULL);
	glfwSetWindowPos(window, 500, 100);
	glfwMakeContextCurrent(window);

	// Initialize GLEW
	#ifdef __APPLE__
		glewExperimental = true; // Needed for core profile
	#endif
	if (glewInit() != GLEW_OK) {
		fprintf(stderr, "Failed to initialize GLEW\n");
		getchar();
		glfwTerminate();
		return -1;
	}

	// Ensure we can capture the escape key being pressed below
	glfwSetInputMode(window, GLFW_STICKY_KEYS, GL_TRUE);

	// Dark blue background
	glClearColor(0.0f, 0.0f, 0.4f, 0.0f);

	
	// Create and compile a GLSL shader program from the shader files
	GLuint programID = loadShaders(SHADERS_PATH "/Examples/ex02_simpleTriangle.vert", 
		SHADERS_PATH "/Examples/ex02_simpleGeometryShader.geom", 
		SHADERS_PATH "/Examples/ex02_simpleTriangle.frag");


	// triangle positions - (0, 0) is window center
	const GLfloat g_vertex_buffer_data[] = {
		-1.0f, -1.0f, 0.0f,
		1.0f, -1.0f, 0.0f,
		0.0f,  1.0f, 0.0f,
	};

	// vertex colors
	const GLfloat vertex_color_data[] = {
		1.0f, 0.0f, 0.0f,
		0.0f, 1.0f, 0.0f,
		0.0f, 0.0f, 1.0f,
	};

	
	// create a vertex buffer object (VBO)
	GLuint vertexbuffer;
	glGenBuffers(1, &vertexbuffer);
	glBindBuffer(GL_ARRAY_BUFFER, vertexbuffer);
	glBufferData(GL_ARRAY_BUFFER, sizeof(g_vertex_buffer_data), g_vertex_buffer_data, GL_STATIC_DRAW);

	// and the same for a color buffer
	GLuint colorbuffer;
	glGenBuffers(1, &colorbuffer);
	glBindBuffer(GL_ARRAY_BUFFER, colorbuffer);
	glBufferData(GL_ARRAY_BUFFER, sizeof(vertex_color_data), vertex_color_data, GL_STATIC_DRAW);


	// create a vertex array object (VAO)
	GLuint VertexArrayID;
	glGenVertexArrays(1, &VertexArrayID);
	
	glBindVertexArray(VertexArrayID); // bind

	// set the vbo
	glBindBuffer(GL_ARRAY_BUFFER, vertexbuffer);
	glEnableVertexAttribArray(0);
	glVertexAttribPointer(
		0,                  // attribute 0. No particular reason for 0, but must match the layout in the shader.
		3,                  // size
		GL_FLOAT,           // type
		GL_FALSE,           // normalized?
		0,                  // stride
		(void*)0            // array buffer offset
	);

	// set the color buffer
	glBindBuffer(GL_ARRAY_BUFFER, colorbuffer);
	glEnableVertexAttribArray(1);
	glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 0, 0); // same as above, but attribute location is 1 (specified in the shader code)

	glBindVertexArray(0); // unbind

	glDisableVertexAttribArray(0);
	glDisableVertexAttribArray(1);



	/* Render loop */
	// Check if the ESC key was pressed or the window was closed
	while (glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS &&	glfwWindowShouldClose(window) == 0) 
	{
		// Clear the screen with the color specified by glClearColor()
		glClear(GL_COLOR_BUFFER_BIT);

		// Use the shader
		glUseProgram(programID);

		// bind the VAO
		glBindVertexArray(VertexArrayID);

		// Draw the triangle
		glDrawArrays(GL_TRIANGLES, 0, 3); // 3 indices starting at 0 -> result is 1 triangle

		// unbind the VAO
		glBindVertexArray(0);

		// Swap buffers (shows the framebuffer on screen)
		glfwSwapBuffers(window);
		glfwPollEvents();

	}

	// Cleanup VBO
	glDeleteBuffers(1, &vertexbuffer);
	glDeleteVertexArrays(1, &VertexArrayID);
	glDeleteProgram(programID);

	// Close OpenGL window and terminate GLFW
	glfwTerminate();

	return 0;
}