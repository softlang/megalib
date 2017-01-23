# Naming conventions

If you want to write a technology model with MegaL we advise you to use the following naming conventions. In general, you may not use spaces or underscores in an entity's name and use camel-case instead. 

* Part-hood can be encoded in a name by using '.' as follows: '`<composite>.<part>`', e.g., 'EMF.CodeGenerator'.
* The name of instances of Language, Technology, System, ArchitecturalPattern, DesignPattern should use the original spelling.
* The name of instances of Role, Paradigm, ProgrammingDomain, AbstractProcess and Construct should start with a capital letter unless there exists some original spelling.
* The name of instances of Function, Artifact and System start with a lower case letter.
* The name of instances of Paradigm should end with 'Programming'.
* If the name of an ArchitecturalPattern or DesignPattern relates to its participants, then make use of '-', as in 'Model-View-Controller'.
* The name of an instance of TechnologySpace should relate to a language, technology, paradigm or programming domain and ends with 'Ware', e.g., 'JavaWare', 'EclipseWare', 'ObjectOrientedWare' or 'MetaWare'.
* Any entity that is only related to an abstract use case starts with '?', e.g. 'model'.
  * An artifact's name starts by relating to a language, if this helps with distinguishing multiple artifacts, e.g., '?ecoreJavaModel' or '?ecoreXMIModel'.
  * An abstract artifact's name should not start with 'a' (as the indefinete article) or 'my'.
* Any function's name may relate to language, if it helps with distinguishing functions, such as 'parseXML'.
* If a language does not have any name, 
 * you can relate to the implementing technology and the language's superset '`<technology><superset>`', e.g., 'ANTLRJava'.
 * you can relate to the superset and use 'Custom', e.g. 'CustomXMI'.