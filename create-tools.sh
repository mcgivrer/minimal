#!/bin/bash

curl -s "https://get.sdkman.io" | bash

sdk env install
sdk env use
echo "Read at work!"

source "$HOME/.sdkman/bin/sdkman-init.sh"
