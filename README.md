# WavTree (simple tool)

File browser for audio files. Plays a file if you click on it. Currently aware of WAV,MP3,OGG

Files can directly be dragged from the list into your DAW or any other app that is able to DnD.

The program also demonstrates the use of a JarClassLoader: www.jdotsoft.com/JarClassLoader.php
The jar contains a lib folder that contains some other jars which are dynamically loaded.

You can write a config file named wavvy_settings.txt that looks like 
this:

--------------------------- snip --------------------------

root :: C:\Users\Administrator\Desktop\audio software\
audacity :: C:\Program Files (x86)\Audacity\audacity.exe  
store :: C:\Users\Administrator\Desktop\song2

--------------------------- snip --------------------------

root is the root of tree. If entry doesn't exist then C:\ ist used

audacity is the path were audacity.exe lives. If that entry doesn't exist
then audacity integration is not available. 

store is the directory where wave files are copied, if you select "store" from the context menu.
If this entry doesn't exist, the default is the same directory as the 
executable JAR file.

Please note that the root entry ends in a backslash while the store 
entry does not.

All keywords (before the :: delimiter) are lowercase.