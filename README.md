Make sure to have jar files of jackson-annotations, jackson-core and jackson-databind under the folder jackson/

Directory structure

current Directory/
	- jackson/
		- jackson-annotations.jar
		- jackson-core.jar
		- jackson-databind.jar
	- DataGenerator.java

In command line, switch to current Directory/, then

To compile

	 javac -cp jackson/* DataGenerator.java


To run

	 java --module-path jackson/ --add-modules ALL-MODULE-PATH DataGenerator



The output will be generated in a file named mockScenario.txt in the current Directory.