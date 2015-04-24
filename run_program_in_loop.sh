#!/bin/bash

 while sleep 900; do
  NOW=$(date +"%Y_%m_%d__%T")
  FILE="logfile_$NOW.txt"
  echo "Running program and logging output to $FILE"
  {
   echo "----------------------------------------------------------------------------------------------------------"
   echo "Running YoutubeToMp3ToDrive at $(date)"
   java -jar out/artifacts/YoutubeToMp3ToDrive_jar/YoutubeToMp3ToDrive.jar
   echo "----------------------------------------------------------------------------------------------------------"
  } > $FILE

 done