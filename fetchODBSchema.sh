#!/bin/bash

npm ci

./fetchODBSchema.mjs $@
