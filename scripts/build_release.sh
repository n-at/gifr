#!/bin/bash

linux_amd64_package_url="https://johnvansickle.com/ffmpeg/releases/ffmpeg-release-amd64-static.tar.xz"
linux_arm64_package_url="https://johnvansickle.com/ffmpeg/releases/ffmpeg-release-arm64-static.tar.xz"
windows_amd64_package_url="https://github.com/BtbN/FFmpeg-Builds/releases/download/autobuild-2021-05-05-12-34/ffmpeg-n4.4-10-g75c3969292-win64-gpl-4.4.zip"
macos_amd64_ffmpeg_url="https://evermeet.cx/ffmpeg/ffmpeg-4.4.zip"
macos_amd64_ffprobe_url="https://evermeet.cx/ffmpeg/ffprobe-4.4.zip"

###############################################################################

function download_binary {
  url="${1}"
  destination="${2}"

  if [ -e "${destination}" ]; then
    return
  fi

  wget -O "${destination}" "${url}"
}


function unpack_tar {
  archive_name="${1}"
  output_name="${2}"

  if [ ! -e "${output_name}" ]; then
    mkdir "${output_name}"
    tar -xv --strip-components=1 -C "${output_name}" -f "${archive_name}"
  fi
}

function release {
  build_platform="${1}"
  build_arch="${2}"

  build_name="${build_platform}_${build_arch}"

  if [ "${build_platform}" = "windows" ]; then
    run_script_name="${build_name}_run.bat"
    run_script="gifr.bat"
    ffmpeg_executable_name="ffmpeg.exe"
    ffprobe_executable_name="ffprobe.exe"
  else
    run_script_name="${build_name}_run.sh"
    run_script="gifr.sh"
    ffmpeg_executable_name="ffmpeg"
    ffprobe_executable_name="ffprobe"
  fi

  if [ ! -x "${build_name}" ]; then
    mkdir -m 0777 "${build_name}"
  fi

  cp "../target/gifr.jar" "${build_name}/gifr.jar"
  cp "../LICENSE" "${build_name}/LICENSE-gifr"
  cp "../scripts/LICENSE-ffmpeg" "${build_name}/LICENSE-ffmpeg"
  cp "../scripts/application.yml" "${build_name}/application.yml"
  cp "../scripts/${run_script_name}" "${build_name}/${run_script}"
  chmod 0777 "${build_name}/${run_script}"

  ffmpeg_path="ffmpeg_${build_name}"

  case "${build_platform}" in
    "linux")
      case "${build_arch}" in
        "amd64")
          download_url="${linux_amd64_package_url}"
        ;;
        "arm64")
          download_url="${linux_arm64_package_url}"
        ;;
      esac

      archive_name="ffmpeg_${build_name}.tar.xz"
      download_binary "${download_url}" "${archive_name}"
      unpack_tar "${archive_name}" "${ffmpeg_path}"
    ;;

    "macos")
      mkdir "${ffmpeg_path}"
      cd "${ffmpeg_path}"
      download_binary "${macos_amd64_ffmpeg_url}" "ffmpeg.zip"
      unzip "ffmpeg.zip"
      download_binary "${macos_amd64_ffprobe_url}" "ffprobe.zip"
      unzip "ffprobe.zip"
      cd ..
    ;;

    "windows")
      download_url="${windows_amd64_package_url}"
      archive_name="ffmpeg_${build_name}.tar.gz"
      download_binary "${download_url}" "${archive_name}"
      unpack_tar "${archive_name}" "${ffmpeg_path}"
      ffmpeg_path="${ffmpeg_path}/bin"
    ;;
  esac

  cp "${ffmpeg_path}/${ffmpeg_executable_name}" "${build_name}/${ffmpeg_executable_name}"
  cp "${ffmpeg_path}/${ffprobe_executable_name}" "${build_name}/${ffprobe_executable_name}"
  chmod 0777 "${build_name}/${ffmpeg_executable_name}" "${build_name}/${ffprobe_executable_name}"

  tar -cvzf "${build_name}.tar.gz" "${build_name}"
}

###############################################################################

cd ..

npm install
npm run build
./mvnw clean package

###

if [ ! -e "release" ]; then
  mkdir -m 0777 "release"
fi

cd "release" || exit 1

release "linux" "amd64"
release "linux" "arm64"
release "windows" "amd64"
release "macos" "amd64"

###

cd ..

rm -r "./src/main/resources/public/build"
./mvnw clean
