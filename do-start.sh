#!/usr/bin/env bash
source helper-functions.sh

   db_start     \
&& image_create \
&& ribac_start