#!/bin/sh
sudo mvn docker:build
# sudo docker login -u admin -p Harbor12345
sudo docker push http://10.228.86.190/chenfan-cloud-registry/chenfan-discovery