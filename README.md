# Rogue prototype

Roguelike game inspired by WH 40K series, main character is marine surrounded by numerous enemies. Your goal is to reach last dungeon level and destroy the Boss.

## How to Launch

Now, it is development builds only

```
mvn package

java cp rogue-prototype-1.0.0-SNAPSHOT.jar -Djava.library.path=<path to LWJGL libs> -Dorg.lwjgl.librarypath=<paht to native libs>

```