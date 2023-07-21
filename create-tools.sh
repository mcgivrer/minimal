#!/bin/bash

curl -s "https://get.sdkman.io" | bash

source "$HOME/.sdkman/bin/sdkman-init.sh"

sdk env install
sdk env use
echo "Read at work!"

