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
     * For every folder in the workbench folder ->Import it as 'Projects from File System or Archive', 
     so that you have at the end the following projects:
        * org.softlang.java
        * org.softlang.megal
        * org.softlang.megal.ide
        * org.softlang.megal.tests
        * org.softlang.megal.ui
        * org.softlang.megal.ui.tests
     * Open the org.softlang.megal project, go to src/org.softlang.megal and right-click the file
     GenerateMegal.mwe2 and select 'RunAs' -> 'MWE2Workflow'
     * Unexpand all projects and select all projects except org.softlang.java and right-click them, 
     then go to 'Configure' -> 'Convert to Xtext Project'
     * In the next step open the .megal projects and check if the src-gen folders are added to the build-path.
     To check that, right click the folder, go to BuildPath. If you are now able to select Use as source folder 
     click at it, otherwise the folder is already added to the build-path.
     * At the end there could be some problems with imports in src or src-gen folders or with the Manifest.mf file, 
     altough the needed resources exist.
     Try to add a line-break to the problem source files anywhere and save it. Then eclipse should refresh everything
     and detect the missing files.
### Running the project:
Right click the org.softlang.megal project and run it as an Eclipse Application
    
# Additional Notes:
## Using the visualisation:
* To show the visualisation of a MegaL-file, right click in the editor, when the file is openened -> ShowIn -> MegaL GraphView
* You need to add the path to your GraphViz .exe under Window-> Preferences -> MegaL Preferences, otherwise the Visualisation
can not layout the graph
* You can submit a properties file, which controls the color of the nodes of the graph
* If you want to view a corresponding legend to the graph, you can use the MegaL Graph View Legend. This can be found under Window>View>Other...>Visualization
