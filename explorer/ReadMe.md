WORK IN PROGRESS

How to install the explorer:

1. Download a Eclipse IDE for Eclipse Commiters: https://www.eclipse.org/downloads/packages/
The actual project was tested with Eclipse Photon

2. Install from the Eclipse Release Update site, e.g:
http://download.eclipse.org/releases/photon
The following plugins:
All GEF Modules (can be found under Application Development Frameworks, if above update site used)
e(fx)clipse IDE and Xtext Complete SDK (can be found under General Purpose, if above update site used)


After checking out the project, you will have to do the following steps:

-Next to the dependcys on the web, the Explorer is also based on the visualizer module of the megalib.

-So you need to port the visualizer project to a plugin Project, which can then be imported by the Explorer.

  To do this follow these steps:
  -Import the visualizer into your workspace as a standard Java Project

  -Compile the Project to .jar file using maven install on the pom.xml

  -Then click on File->New->Project->PlugIn Development->PlugIn from exisiting Jar Archive

  -Name the file visualizer_plugin and select the given visualizer jar as input.

How to run the explorer:

There are two different ways to run the explorer
1. On the one side is the stand-alone version, which could be run by: Selecting the explorer.parts.view Class and
run it as a Java Application

2. The other way is as a Extension of a Eclipse Application, therefore you have to do some more steps:
	a) Right click the project, go to run as->run configurations
	b) Right click Eclipse Application in the left bar and create a new configuration
	c) Go to the plugin-tab and change "Launch with" to plugins selected below only
	d) First deselect all plugins
	e) Select the explorer and the visualizer-plugin from the list
	f) Now click "Add requiered Plugins" on the right side
	g) Apply the changes and now run the application
	
	You can find the build-in Explorer now under: Window->Show View->Other-> MegaL