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
### Importing the Project:
* Go the your Eclipse Application and import every folder as follow:
    * Import-> Import from Folder or Archive
* After importing the projects go in the org.softlang.megal project
    * Right click the src/org.softlang.megal/GenerateMegaL.mwe2 file -> RunAs -> MWE2 Workflow. This creates additional files in the src-gen 
    path of the other org.softlang.megal projects
* Check in every project, if the src-gen folder is in the build path as a source folder
    * If not right click the folder -> BuildPath -> Use as source folder
* Now it could happen, that there are still some errors in some files. This could be solved by opening and closing the files in Eclipse.
Sometimes it also works, if you restart your Eclipse Application
### Running the project:
Right click the org.softlang.megal project and run it as an Eclipse Application
    
# Additional Notes:
## Using the visualisation:
* To show the visualisation of a MegaL-file, right click in the editor, when the file is openened -> ShowIn -> MegaL GraphView
* You need to add the path to your GraphViz .exe under Window-> Preferences -> MegaL Preferences, otherwise the Visualisation
can not layout the graph
* You can submit a properties file, which controls the color of the nodes of the graph
