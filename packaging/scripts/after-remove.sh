#!/bin/bash

set -e

INSTALL_DIR=/home/icecp/tools/"<%= version %>"-"<%= iteration %>"
CLI_SCRIPT=${INSTALL_DIR}/bin/icecp-cli
CLI_SOFTLINK=/usr/local/bin/icecp-cli

after_remove()
{
  echo "Performing post-removal steps for <%= name %> version=<%= version %> iteration=<%= iteration %>"

  # Remove soft link and icecp-node directory contents
  if [ -L $CLI_SOFTLINK ] && [ "$(readlink $CLI_SOFTLINK)" = $CLI_SCRIPT ]; then
    rm $CLI_SOFTLINK
    echo "Removed soft link $CLI_SOFTLINK"
  fi

  if [ -d $INSTALL_DIR ]; then
    rm -rf $INSTALL_DIR
    echo "Removed $INSTALL_DIR"
  fi
}

after_remove
