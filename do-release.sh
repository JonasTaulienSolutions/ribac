#!/usr/bin/env bash
source helper-functions.sh

   db_start     \
&& image_create \
&& image_push   \
&& db_stop