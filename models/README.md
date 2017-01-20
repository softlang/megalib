# Naming conventions

If you want to write a technology model with MegaL we advise you to use the following naming conventions. In general, you may not use spaces in an entity's name and use camel-case instead of underscores ('_'). The following list gives type specific advices.
* Language
** Start with an upper case letter.
** Make use of '.', if you want to relate to technology specific subsets using '<technology>.<superset>'.
** Make use of '.', if you want to relate to a language's superset using '<superset>.<subset>, e.g., '?XML.DSL'.
** Otherwise, do not make use of '.'.
** If the language is defined in an abstract usage scenario, start with '?'.
* Function
** Start with a lower case letter.
** Never use '.'.
** You may relate to languages in the name, e.g. 'serializeObjectXML'.
* Artifact
** Only if the artifact is presented in an abstract use case, start with '?', e.g., '?model'.
** An artifact's name starts by relating to a language, if this helps with distinguishing multiple artifacts, e.g., '?ecoreJavaModel' or '?ecoreXMIModel'.
** Start with a lower case letter.
** An abstract artifact's name should not start with 'a' (as the indefinete article) or 'my'.
** You may relate to what the artifact is part of by using the pattern '<composite>.<part>' multiple times, e.g. '?djangoApp.model'.
* Role
** Start with capital letter.
** Make use of terms known from the related technology space or programming domain in their original spelling.
* Technology
** If the technology is generated in an abstract use case, start with '?'.
** You may relate to what the technology is part of by using the pattern '<composite>.<part>' multiple times, e.g. 'EMF.EMFPersistence'
* System
** If the system is presented in an abstract use case, start with '?'.
** Start with a lower case letter, e.g. '?webApplication'.
* Paradigm
** Never use '.'.
** Start with an upper case letter.
** End with 'Programming', e.g., 'ObjectOrientedProgramming'.
* ArchitecturalPattern and DesignPattern
** Never use '.'.
** Start with an upper case letter.
** If the name relates to its defined participants, then you can make use of '-', e.g., 'Model-View-Controller'.
* TechnologySpace
** Start with an upper case letter.
** Any name should relate to a language, technology, or programming domain. 
** Never use '.'.
** End with 'Ware', e.g., 'JavaWare', 'EclipseWare' or 'ObjectOrientedWare'.
* ProgrammingDomain, AbstractProcess and Construct
** Never use '.'.
** Start with an upper case letter.