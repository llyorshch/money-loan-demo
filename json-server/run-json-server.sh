#!/bin/bash

function checkCommand {
if ! [ -x "$(command -v $1)" ]; then
  echo "Error: $1 is not installed." >&2
  exit 1
else
  echo "$1 is installed."
fi
}

checkCommand "json-server"

json-server --watch db.json 
