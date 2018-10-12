# WavTree (simple tool)

File browser for audio files. Plays a file if you click on it. Currently aware of WAV,MP3,OGG

The program also demonstrates the use of a JarClassLoader: www.jdotsoft.com/JarClassLoader.php
The jar contains a lib folder that contains some other jars which are dynamically loaded.

You ca write a config file that looks like this:

--------------------------- snip --------------------------

root :: C:\Users\Administrator\Desktop\audio software\
audacity :: C:\Program Files (x86)\Audacity\audacity.exe

--------------------------- snip --------------------------

Root is the root of tree. If entry doesn't exist then C:\ ist used
Audacity is the path were audacity.exe lives. If that entry doesn't
exist then audacity integration is not available. 