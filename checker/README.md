# The Well-Formedness Checker Manual

## How To Use The Checker

* The checker.jar and the Prelude.megal always have to be in the same folder, when you want to use the checker.
* Remember that you need to define a JAVA_HOME in your path. Else you cannot use .jars from command line.
* You use the checker via the command line as follows. Imagine you place the checker.jar and Prelude inside of the models folder. The checker expects a path to the megamodel you want to check after '-f'
	
         cd models
         java -jar "checker.jar" -f "DjangoDBSchema.megal"
	

## Allowed Grammar For The Checker

For a detailled syntax definition see the ANTLR grammar 'Megalib.g4' and the examples located in './models'.

### General conventions:

* All words are written with letters from a-z, A-Z, 0..9, '.' in camelCase. 
* There exists only one constant Entity-Type called: "Entity", which is the Top-Type of everything.
* Abstract instances start with a '?'.
* We hijack the symbol '+' for example in 'File+' to state that something manifests as a set of files.

### Modularization
	
* Any technology model written in MegaL can optionally be organized in multiple modules. In this case every .megal-file that represents a module starts with a declared name.
                        
        module rubyOnRails.ActiveRecord
			
* A module's name encodes information on a folder that contains it. In the example above the folder the file is in has to be called 'rubyOnRails'. Only then, another module can import this module. Import statements come after the module name. The import mechanism works in a similar way as in Java or Haskell etc.
                       
        import rubyOnRails.ActiveRecord

* The modularization further allows a separation of concerns. While one module might hold information about general key facts on a technology another module might concern itself with a concrete system that uses the technology. For example, a general fact for any web application that uses Django is that there exists a model. In a concrete usage scenario this file may be linked to a file in a repository or in a file system. All general key facts for the existing abstract model also hold for the concrete model. Thus it can be substituted and has to be linked.

```
  import django.MVC where {
    model.py substitutes ?model.py
  }
```

### Statements: 

For the statements, it is important that anything has to be declared before it is used. Thus, the order of the statements plays a role in the technology model. At best, it is not necessary to declare new subtypes or relations in a module, since the prelude holds the most general declarations.

* Subtyping : 	

			Artifact < Entity
			ProgrammingLanguage < Language

* Instantiation: 	

			Controller: Artifact<Java,MvcController,File>
			Java: ProgrammingLanguage

* Relation Declaration:

			partOf < Artifact # Artifact
			uses < Artifact # Language

* Relation Instantiation:	

			Controller partOf ControllerPackage
			JavaFile1 contains JavaFile1

* Links (for now only URLs are supported by the checker):		

			JavaFile1 = "yourFirstLink"
			JavaFile1 = "yourSecondLink"

* Function Declaration:	

			functionA: Java -> SubJava # SubJava
			functionB: SubJava # Java -> Java # SubJava
			functionC: SubJava # Java -> SubJava

* Function Application:	

			functionA (JavaFile1) |-> (JavaFile2,JavaFile2)
			functionB (JavaFile2, JavaFile1) |-> (JavaFile1, JavaFile2)
			functionC (JavaFile2, JavaFile1) |-> JavaFile2
			

## Constraints

The following constraints are validated in the checker.

### Creation Time Constraints

The following critical constraints are checked as the internal model is filled by the checker. They all focus on ordering constraints such that anything has to be declared before it is used.

* The right side of a subtyping declaration is a declared type or Entity.
* Multiple inheritance is not enabled.

* The left side of an instantiation is neither a declared type nor Entity.
* The right side of an instantiation is a declared type.
* We do not allow entities to instantiate multiple types.

* A relation declaration refers to types that are already declared.
* A relation declaration does not appear more than once.

* A relation instance does not appear more than once.
* A relation instance has to fit to exactly one declaration.
* The left and right side of a relation instance have to be declared instances.

* A function has only one declaration. Overloading is not enabled.
* The domains and ranges of a function are declared language instances.

* A function application exists only once.
* A function application fits to a declaration such that the input instances are declared elements of the (subset of) domains and the output instances are declared element of the (subset of) ranges.

### Well-Formedness Constraints

The following constraints are checked after the model has been created. These checks validate necessary completeness and try to identify unwanted cycles.

* There is no direct instance of Technology. Only subtypes are instantiated for clarity.
* There is no direct instance of Language. Only subtypes are instantiated for clarity.
* Every instance is linked to a resource, if its name does not start with '?'.
* Every technology uses at least one language.
* Every function is implemented.
* Every function is applied at least once.
* Every artifact is element of a language.
* Every artifact has a role.
* Every artifact has a manifestation.

* There is no cycle in subset/parthood/conformance relationships.
* Every link is a well formed URL.
* Every link has to refer to a web resource, where a connection to it is possible.
