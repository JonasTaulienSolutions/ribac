#!/usr/bin/env bash
source helper-functions.sh

   db_start     \
&& image_create \
&& db_stop