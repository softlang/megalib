The syntax and well-formedness checker
======================================

Allowed grammar for the checker
===============================
General conventions:
--------------------
	All words are written with letters from a-z & A-Z in camelCase
	There exists only one constant Entity-Type called: "Entity", which is the Top-Type of everything
	If you are using functions please see the extra notice below

Examples: 
---------
(for a general overview see the ANTLR grammar Megalib.g4 & the examples located in the TestFiles Folder)

* Entity Declaration: 	

			Artifact < Entity
			SubArtifact < Artifact

* Entity Instances: 	

			JavaFile1: Artifact
			JavaFile2: SubArtifact

* Relation Declartation:

			contains < Artifact # SubArtifact
			contains < Artifact # Artifact

* Relations Instances:	

			JavaFile1 contains JavaFile2
			JavaFile1 contains JavaFile1

* Links:		

			JavaFile1 = yourFirstLink
			JavaFile1 = yourSecondLink


* (Please see the extra notice "Specialities at functions) below:

			JavaFile1 elementOf Java
			JavaFile2 elementOf SubJava

* functionDeclaration:	

			functionA: Java -> SubJava # SubJava
			functionB: SubJava # Java -> Java # SubJava
			functionC: SubJava # Java -> SubJava

* functionInstances:	

			functionA (JavaFile1) |-> (JavaFile2,JavaFile2)
			functionB (JavaFile2, JavaFile1) |-> (JavaFile1, JavaFile2)
			functionC (JavaFile2, JavaFile1) |-> JavaFile2

Specialites at functions:
-------------------------
* When you are using functions, your megamodel should contain the following Entity & relations:


			Language < Entity
			elementOf< anything # Language 
			(at anything you can use any of your Entity-Types)

AND

* every EntityType used in the function Declaration needs to be from the type of Language (or a Subtype of it) and 
every EntityInstance used in the function Instance, needs to be declared as an elementOf Language (or a Subtype of it)
before

Add modules to the checker
==========================
Creating new checks for the megalChecker is very simple, because the ANTLR Listener generates a object-model 
(see MegaModel.java & ObjectModel below), which allows a good access on all entered types.


So you just need to write your checks in the ResultChecker.java as a method and then you can add it up to the right check
function, see:

public void checkEntityDeclaration() 

public void checkEntityInstances()

public void checkRelationDeclaration()

...
Those will be automatically executed by the checker. Because you mostly need to iterate over one whole object body,
we directly added a for-loop to most of the check cases, so that you can place your method call inside of them.

ObjectModel:
==============
Contains the following informations/objects:
* Object for EntityDeclarations: 

			public Map<String, String> entityDeclarations;

Where the Key is the SubEntity, while the Value is the TopEntity

* Object for EntityInstances:

			entityInstances = new HashMap<String,String>();

Where the Key is the Instance and the Value the EntityType

* Object for relationDeclarations:

			relationDeclarations = new HashMap<String, Map<Integer,LinkedList<String>>>(); 

Where the key of the HashMap is the name of the relation, while the Integer Key of the Map (Value of the HashMap) is a hashCode of the saved LinkedList in the Value of the Map, which contains all possible Entity-pairs for the relation

* Object for relationInstances:

			relationInstances = new HashMap<String, Map<Integer,LinkedList<String>>>(); 

Equal to relation Declaration, just that the LinkedList in the Map now contains Entity Instances

* Object for functionDeclarations:

			functionDeclarations = new HashMap<String, Map<Integer,Function>>(); 

Where the key of the HashMap is the name of the function, while the Integer Key of the Map (Value of the HashMap) is a hashCode of the saved Function-Object in the Value of the Map. The function objects contain two LinkedLists "returnType" & "parameterTypes", which contains the Entity Types for the function 

* Object for functionInstances:

			functionInstances = new HashMap<String, Map<Integer,Function>>(); 

Where the key of the HashMap is the name of the function, while the Integer Key of the Map (Value of the HashMap) is a hashCode of the saved Function-Object in the Value of the Map. The function objects contain two LinkedLists "returnType" & "parameterTypes", which contains the Entity Instances for the function 

* Object for links:

			links = new HashMap<String,LinkedList<String>>(); 

Where the key of the HashMap is the name of the object, which gets a link and the LinkedList are all links







