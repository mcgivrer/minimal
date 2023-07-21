FROM gitpod/workspace-full-vnc
RUN 

FROM gitpod/workspace-full

RUN bash -c "sudo apt-get update && \
            sudo apt-get install -y libgtk-3-dev && \
            sudo rm -rf /var/lib/apt/lists/* && \
            . /home/gitpod/.sdkman/bin/sdkman-init.sh &&\
            curl -s \"https://get.sdkman.io\" | bash && \
            source \"$HOME/.sdkman/bin/sdkman-init.sh\" && \
            sdk update && \ 
            sdk env istall && \
            sdk env use"
