#!/bin/bash

#  RTC Build
#
#  Created by qixinbing on 19/3/6.
#  Copyright (c) 2019 RongCloud. All rights reserved.

# 该脚本原则上由 Jenkins 编译触发，如果想本地编译，请通过 pre_build.sh 触发本脚本

#sh scripts/build.sh

trap exit ERR

BUILD_PATH=`pwd`
OUTPUT_PATH="${BUILD_PATH}/output"
BRANCH=${Branch}
CUR_TIME=$(date "+%Y%m%d%H%M")
APP_VERSION=${APP_Version}

echo "Build Path:"$BUILD_PATH

#拉取源码，参数 1 为 git 仓库目录,2 为 git 分支
function pull_sourcecode() {
  path=$1
  branch=$2
  cd ${path}
  git fetch
  git reset --hard
  git checkout ${branch}
  git pull origin ${branch}
}

##复制 SDK 或者源码到指定目录并压缩，参数1：原始目录，参数2：目标目录，参数3：压缩包名称
#function copy_sdk(){
#  src_path=$1
#  target_path=$2
#  zip_file_name=$3
#
#  mkdir $target_path
#  cp -af $src_path/* $target_path
#  zip -r $OUTPUT_PATH/${zip_file_name}.zip $target_path
#  rm -rf $target_path
#}
#
#
#pull_sourcecode ./ $RTC_BRANCH
#
#sed -i -e 's/3612cc23a8/3b03bae451/g' app/src/main/java/cn/rongcloud/rtc/RongRTCApplication.java


chmod +x gradlew
./gradlew clean -i
./gradlew sealmic:assemble


rm -rf $OUTPUT_PATH
mkdir -p $OUTPUT_PATH

cp -r  sealmic/build/outputs/apk/debug/sealmic-debug.apk ${OUTPUT_PATH}/SealMic_v${APP_VERSION}_${CUR_TIME}.apk
cp -r  sealmic/build/outputs/apk/release/sealmic-release.apk ${OUTPUT_PATH}/SealMic_v${APP_VERSION}_${CUR_TIME}_release.apk
