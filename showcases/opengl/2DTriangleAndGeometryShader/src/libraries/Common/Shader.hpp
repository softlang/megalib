#ifndef __COMMON_SHADER_HPP
#define __COMMON_SHADER_HPP

#include <stdio.h>
#include <string>
#include <vector>
#include <iostream>
#include <fstream>
#include <algorithm>

#include <stdlib.h>
#include <string.h>

#include <GL/glew.h>

// load a shader program from the shader source files
GLuint loadShaders(const char * vertex_file_path, const char * fragment_file_path);

// more advanced with a geometry shader
GLuint loadShaders(const char * vertex_file_path, const char * geometry_file_path, const char * fragment_file_path);

#endif /* __COMMON_SHADER_H */