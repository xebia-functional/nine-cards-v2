#!/bin/sh

openssl aes-256-cbc -K $encrypted_e4893f5c86d2_key -iv $encrypted_e4893f5c86d2_iv -in travis-deploy-key.enc -out travis-deploy-key -d;
chmod 600 travis-deploy-key;
cp travis-deploy-key ~/.ssh/id_rsa;
