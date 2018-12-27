# Workbench
## A Eclipse Environment for creating MegaL Models
The workbench is a Eclipse Plugin, based on xtext, which contains a Editor for MegaL with Syntax checking and completion.
It also contains the possibility to visualize given MegaL-Models by using GEF and Graphviz

## Projects:
* org.softlang.megal -> Xtext Project
* org.softlang.megal.ui -> Xtext Project
* org.softlang.megal.ide -> Xtext Project
* org.softlang.megal.ide.tests -> Xtext Project
* org.softlang.megal.tets -> Xtext Project
* org.softlang.java -> Gef & Preferences Plugin for Visualisation of MegaModel

## Setting up the project
### Requierements:
Please take care that you have installed the following programs:
* Eclipse IDE for Java and DSL Developers (tested with Eclipse 2018.09)
* Graphviz 

And the following Plugins in your Eclipse for DSL Developers:
* GEF
* e(fx)clipse

Note: Both plugins can be downloaded from Eclipse Update-Sites. For example you could use:
* http://download.eclipse.org/releases/2018-09
* On this Update-Site you can find GEF under Application Development Framework (install all GEF SDKs)
* e(fx)clipse can be found under General Purpose Tools

If the com.google.inject plugin is missing you can also install it via a Updatesite eg.:
* https://download.eclipse.org/tools/orbit/downloads/drops/R20181128170323/repository/
* Install the Google Guice package to use com.google.inject

### Importing the Project:
* Go to Eclipse.
     * Import Projects from File System or Archive
     * Set the Workbench folder as the directory
     * Deselect the workbench folder itself from the list below.
     * Finish importing
     * Wait for Eclipse to build and update the workspace.
### Running the project:
Right click the org.softlang.megal project and run it as an Eclipse Application
    
# Additional Notes:
## Using the visualisation:
* To show the visualisation of a MegaL-file, right click in the editor, when the file is openened -> ShowIn -> MegaL GraphView
* You need to add the path to your GraphViz .exe under Window-> Preferences -> MegaL Preferences, otherwise the Visualisation
can not layout the graph
* You can submit a properties file, which controls the color of the nodes of the graph
