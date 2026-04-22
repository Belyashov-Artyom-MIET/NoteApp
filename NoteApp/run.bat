@echo off
rem 
if not exist bin\NotesAppGUI.class (
    echo 
    javac -d bin src\NotesAppGUI.java
)

echo
javaw -Dfile.encoding=UTF-8 -cp bin NotesAppGUI