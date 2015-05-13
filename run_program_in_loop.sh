#!/bin/bash

 while sleep 900; do
  DAY=$(date +"%Y_%m_%d")
  FILE="logfile_$DAY.txt"
  echo "Running program and logging output to $FILE"
  {
   echo "----------------------------------------------------------------------------------------------------------"
   echo "Running YoutubeToMp3ToDrive at $(date)"
   java -jar out/artifacts/YoutubeToMp3ToDrive_jar/YoutubeToMp3ToDrive.jar
   echo "----------------------------------------------------------------------------------------------------------"
  } > $FILE

 done