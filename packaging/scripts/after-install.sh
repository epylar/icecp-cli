#!/bin/bash

set -e

CLI_SCRIPT=/home/icecp/tools/"<%= version %>"-"<%= iteration %>"/bin/icecp-cli
CLI_SOFTLINK=/usr/bin/icecp-cli

after_install()
{
  echo "Performing after install steps for <%= name %> VERSION <%= version %> ITERATION <%= iteration %>"

  ln -sfn $CLI_SCRIPT $CLI_SOFTLINK
  echo "Created soft link $CLI_SOFTLINK to $CLI_SCRIPT"
}
after_install
