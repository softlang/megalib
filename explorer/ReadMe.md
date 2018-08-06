WORK IN PROGRESS

How to run and install the explorer:

The explorer is a GEF-based Application using primarily ZEST and the GRAPH module of GEF.
If you want to run it, please make sure that you have installed GEF and it´s dependencys
like e(fx)clipse in your eclipse installation.
The application was tested on Eclipse IDE for Eclipse Commiters (Neon)

After checking out the project, you will have to do the following steps:

-Next to the dependcys on the web, the Explorer is also based on the checker module of the megalib.

-So you need to port the checker project to a plugin Project, which can then be imported by the Explorer.

  To do this follow these steps:
  -Import the checker into your workspace as a standard Java Project

  -Compile the Project to .jar file using maven install on the pom.xml

  -Then click on File->New->Project->PlugIn Development->PlugIn from exisiting Jar Archive

  -Name the file checker_plug and select the given checker jar as input.

Setting up the Target:

  -Click on the file: target.target and let it download all dependcys (you can see the progress in the right corner of Eclipse)

  -When alle dependencys are downloaded, click "Set this as target plattform" on the upper right Corner of the window
