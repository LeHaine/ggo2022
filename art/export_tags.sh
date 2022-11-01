#!/bin/bash
aseprite="D:/Program Files (x86)/SteamLibrary/steamapps/common/Aseprite/Aseprite.exe"
fileName=""
excludeFiles=("expBar.aseprite" "healthBar.aseprite" "dragonArrow.aseprite")

if [ -z "$1" ]; then
  for file in ./ase/*.aseprite; do
    fileName="${file##*/}"
    fileName="${fileName%.*}"
    prefix="${fileName:0:2}"
    if [ "${prefix}" != m_ ]; then
      if [[ ! "${excludeFiles[*]}" =~ ${fileName} ]]; then
        "$aseprite" -b "$file" --save-as ./export_tiles/"${fileName}""{tag}"0.png
      fi
    fi
  done
  else
    file="$1"
    fileName="${file##*/}"
    fileName="${fileName%.*}"
    prefix="${fileName:0:2}"
    echo "Exporting ${file}"
    if [ "${prefix}" != m_ ]; then
      if [[ ! "${excludeFiles[*]}" =~ ${fileName} ]]; then
        "$aseprite" -b "$file" --save-as ./export_tiles/"${fileName}""{tag}"0.png
      fi
    fi
fi
