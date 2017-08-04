@ECHO OFF

WHERE cmake
IF ERRORLEVEL == 1 (
	ECHO ERROR: CMake needs to be installed!
	GOTO end
) 
CLS

IF "%GLM_ROOT%"=="" (
	ECHO WARNING: The necessary libraries for this framework are not found.
	pause
) 
CLS	

ECHO Choose your IDE:
ECHO (1) Eclipse CDT mit MinGW
ECHO (2) Sublime Text 2 mit MinGW
ECHO (3) VisualStudio2013
ECHO (4) VisualStudio2015
ECHO (5) VisualStudio2017
ECHO (6) Use 64 Bit / 32 Bit
ECHO ----------------------

SET "USE64="
SET "USEX64="

:choose
CHOICE /n /c "1234567" /M ":"

IF ERRORLEVEL==6 ( 
	IF "%USE64%"=="" (
		SET "USE64= Win64"
		SET "USEX64=_x64"
		ECHO Using 64 Bit 
	) ELSE (
		SET "USE64="
		SET "USEX64="
		ECHO Using 32 Bit
	) 
	GOTO choose )
IF ERRORLEVEL==5 ( SET FOLDER="BUILD_VisualStudio17%USEX64%" & SET GEN="Visual Studio 15 2017%USE64%" & GOTO create )
IF ERRORLEVEL==4 ( SET FOLDER="BUILD_VisualStudio15%USEX64%" & SET GEN="Visual Studio 14 2015%USE64%" & GOTO create )
IF ERRORLEVEL==3 ( SET FOLDER="BUILD_VisualStudio13%USEX64%" & SET GEN="Visual Studio 12%USE64%" & GOTO create )
IF ERRORLEVEL==2 ( SET FOLDER="BUILD_SublimeText2%USEX64%" & SET GEN="Sublime Text 2 - MinGW Makefiles%USE64%" & GOTO create )
IF ERRORLEVEL==1 ( SET FOLDER="BUILD_Eclipse%USEX64%" & SET GEN="Eclipse CDT4 - MinGW Makefiles%USE64%" & GOTO create )
ECHO Something went wrong... 
GOTO end

:create
mkdir %FOLDER%
cd %FOLDER%
cmake -G%GEN% ../src/
if "!errorlevel!"=="1" cd.. &echo Ein CMake Fehler ist aufgetreten. !FOLDER! wurde nicht erzeugt.

:end
pause
