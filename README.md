# Algorithms-for-Model-Checking

## Assignment 1:
1. Compile the java project to a fat jar using `mvn clean compile assembly:single` while in the `mc` folder
1. Move the created fat jar `mc-1.0-jar-with-dependencies.jar` from the `mc/target` folder up to the `Assignment 1` folder (so the LTS files and formulas are at the correct relative position)
1. CD into the `Assignment 1` folder.
1. Run the far jar using `java -jar mc-1.0-jar-with-dependencies.jar`

    or supply the filename arguments using the format `java -jar mc-1.0-jar-with-dependencies.jar <filename>.aut <filename>.mcf <true/false>`

    (For example: `java -jar mc-1.0-jar-with-dependencies.jar boardgame/robots_50.aut boardgame/player_2_can_win.mcf true`)