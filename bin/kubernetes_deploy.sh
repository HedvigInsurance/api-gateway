#!/usr/bin/env bash

export GOPATH="${TRAVIS_BUILD_DIR}/Godeps/_workspace:$GOPATH"
export PATH="${TRAVIS_BUILD_DIR}/Godeps/_workspace/bin:$PATH"

aws s3 cp s3://dev-com-hedvig-cluster-ett-data/kube ~/.kube --recursive

curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl
chmod +x ./kubectl

go get -u -v github.com/heptio/authenticator/cmd/heptio-authenticator-aws

./kubectl set image deployment/api-gateway api-gateway=$REMOTE_IMAGE_URL:$TRAVIS_BUILD_NUMBER
