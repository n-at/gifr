#!/bin/bash

ffmpeg_license_url="https://raw.githubusercontent.com/n-at/ffmpeg-builds/master/LICENSE"

ffmpeg_linux_amd64="https://github.com/n-at/ffmpeg-builds/raw/master/ffmpeg-4.4-linux-amd64/ffmpeg"
ffprobe_linux_amd64="https://github.com/n-at/ffmpeg-builds/raw/master/ffmpeg-4.4-linux-amd64/ffprobe"

ffmpeg_linux_arm64="https://github.com/n-at/ffmpeg-builds/raw/master/ffmpeg-4.4-linux-arm64/ffmpeg"
ffprobe_linux_arm64="https://github.com/n-at/ffmpeg-builds/raw/master/ffmpeg-4.4-linux-arm64/ffprobe"

ffmpeg_mac_amd64="https://github.com/n-at/ffmpeg-builds/raw/master/ffmpeg-4.4-mac/ffmpeg"
ffprobe_mac_amd64="https://github.com/n-at/ffmpeg-builds/raw/master/ffmpeg-4.4-mac/ffprobe"

ffmpeg_windows_amd64="https://github.com/n-at/ffmpeg-builds/blob/master/ffmpeg-4.4-windows/bin/ffmpeg.exe"
ffprobe_windows_amd64="https://github.com/n-at/ffmpeg-builds/blob/master/ffmpeg-4.4-windows/bin/ffprobe.exe"

###############################################################################

function download_binary {
  url="${1}"
  destination="${2}"

  if [ -x "${destination}" ]; then
    return
  fi

  wget -O "${destination}" "${url}"
  chmod 0777 "${destination}"
}

function make_release {
  name="${1}"
  ffmpeg_url="${2}"
  ffmpeg_destination="${3}"
  ffprobe_url="${4}"
  ffprobe_destination="${5}"
  run_script_name="${6}"
  run_script_destination="${7}"
  configuration_name="${8}"
  configuration_destination="${9}"

  if [ ! -x "${name}" ]; then
    mkdir -m 0777 "${name}"
  fi

  cp "../target/gifr.jar" "${name}/gifr.jar"
  cp "../LICENSE" "${name}/LICENSE-gifr"
  cp "../scripts/${configuration_name}" "${name}/${configuration_destination}"
  cp "../scripts/${run_script_name}" "${name}/${run_script_destination}"
  chmod 0777 "${name}/${run_script_destination}"

  download_binary "${ffmpeg_license_url}" "${name}/LICENSE-ffmpeg"
  download_binary "${ffmpeg_url}" "${name}/${ffmpeg_destination}"
  download_binary "${ffprobe_url}" "${name}/${ffprobe_destination}"

  chmod 0777 "${name}/${ffmpeg_destination}" "${name}/${ffprobe_destination}"
}

###############################################################################

cd ..

###

npm install
npm run build
./mvnw clean package

###

if [ ! -x release ]; then
  mkdir -m 0777 release
fi

cd release

make_release "linux-amd64" \
             "${ffmpeg_linux_amd64}" "ffmpeg" \
             "${ffprobe_linux_amd64}" "ffprobe" \
             "linux-run.sh" "gifr.sh" \
             "linux-application.yml" "application.yml"

make_release "linux-arm64" \
             "${ffmpeg_linux_arm64}" "ffmpeg" \
             "${ffprobe_linux_arm64}" "ffprobe" \
             "linux-run.sh" "gifr.sh" \
             "linux-application.yml" "application.yml"

make_release "mac-amd64" \
             "${ffmpeg_mac_amd64}" "ffmpeg" \
             "${ffprobe_mac_amd64}" "ffprobe" \
             "mac-run.sh" "gifr.sh" \
             "mac-application.yml" "application.yml"

make_release "windows-amd64" \
             "${ffmpeg_windows_amd64}" "ffmpeg.exe" \
             "${ffprobe_windows_amd64}" "ffprobe.exe" \
             "windows-run.bat" "gifr.bat" \
             "windows-application.yml" "application.yml"

###

cd ..

rm -r ./src/main/resources/public/build
./mvnw clean
