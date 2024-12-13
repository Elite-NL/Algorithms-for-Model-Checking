# Algorithms-for-Model-Checking

## Assignment 1:
Note: To install you need at least java version 11 and a recent version of maven (https://maven.apache.org/download.cgi).

1. cd into the `Assignment 1/mc` folder.
1. Compile the java project to a fat jar using `mvn clean compile assembly:single`.
1. Move the newly created fat jar `mc-1.0-jar-with-dependencies.jar` from the `Assignment 1/mc/target` folder up to the `Assignment 1` folder (so the LTS files and formulas are at the correct relative position).
1. cd into the `Assignment 1` folder.
1. Run the far jar using `java -jar mc-1.0-jar-with-dependencies.jar`. (This will prompt the user to type the file names in the terminal.)

    Alternatively you can supply the filename arguments using the format `java -jar mc-1.0-jar-with-dependencies.jar <filename>.aut <filename>.mcf`. (This will evaluate using the Emerson-Lei algorithm)

    Alternatively you can supply the filename arguments **and algorithm preference** using the format `java -jar mc-1.0-jar-with-dependencies.jar <filename>.aut <filename>.mcf <true/false>`, where `true` means the evaluation uses the Emerson-Lei algorithm and `false` means the evaluation uses the naive algorithm.

    For example: `java -jar mc-1.0-jar-with-dependencies.jar boardgame/robots_50.aut boardgame/player_2_can_win.mcf true`.