#!/bin/sh

javac *.java
java LZW_mod - r < $1 > $1.zip
java LZW_mod + r < $1.zip > $1.recovered
diff $1 $1.recovered
