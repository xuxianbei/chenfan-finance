#!/bin/sh
sudo mvn docker:build
# sudo docker login -u admin -p Harbor12345
sudo docker push http://10.228.86.190/chenfan-cloud-registry/chenfan-discovery
大神你好，涉及到公司代码，请将该项目删除或隐藏
