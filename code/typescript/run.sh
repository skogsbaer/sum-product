#!/bin/bash

node_modules/.bin/tsc --strict --skipLibCheck medication.ts || exit 1
node medication.js
