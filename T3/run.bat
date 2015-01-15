@ECHO OFF
java -cp .\target\z03-0.0.1-SNAPSHOT.jar;.\target\lib\imgscalr-lib-4.2.jar task.z03.ImageResizer %1 %2 %3 %4
PAUSE