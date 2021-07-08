Make sure to have jar files of jackson-annotations, jackson-core and jackson-databind under the folder jackson/


In command line, switch to current directory, then

To compile

	 javac -cp jackson/* DataGenerator.java


To run

	 java --module-path jackson/ --add-modules ALL-MODULE-PATH DataGenerator



The output will be generated in a file named mockScenario.txt in the current directory.