#!/bin/bash
# this will search for aseprite files with the prefix of "m_" that denotes that this file
# should be exported by layer and tag

aseprite="D:/Program Files (x86)/SteamLibrary/steamapps/common/Aseprite/Aseprite.exe"
fileName=""
excludeFiles=()
if [ -z "$1" ]; then
  for file in ./ase/m_*.aseprite; do
    fileName="${file##*/}"
    fileName="${fileName%.*}"
    fileName="${fileName:2}"
    if [[ ! "${excludeFiles[*]}" =~ ${fileName} ]]; then
      "$aseprite" -b --all-layers "$file" --save-as ./export_tiles/"${fileName}""_{layer}_{tag}"0.png
    fi
  done
else
  file="$1"
  fileName="${file##*/}"
  fileName="${fileName%.*}"
  fileName="${fileName:2}"
  echo "Exporting ${file}"
  if [[ ! "${excludeFiles[*]}" =~ ${fileName} ]]; then
    "$aseprite" -b --all-layers "$file" --save-as ./export_tiles/"${fileName}""_{layer}_{tag}"0.png
  fi
fi
