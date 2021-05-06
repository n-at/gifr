#!/bin/bash

linux_amd64_package_url="https://github.com/n-at/ffmpeg-builds/releases/download/v4.4/linux-ffmpeg-release-amd64-static.tar.xz"
linux_arm64_package_url="https://github.com/n-at/ffmpeg-builds/releases/download/v4.4/linux-ffmpeg-release-arm64-static.tar.xz"
windows_amd64_package_url="https://github.com/n-at/ffmpeg-builds/releases/download/v4.4/ffmpeg-4.4-windows-amd64.tar.xz"
macos_amd64_ffmpeg_url="https://github.com/n-at/ffmpeg-builds/releases/download/v4.4/macos-ffmpeg-4.4.zip"
macos_amd64_ffprobe_url="https://github.com/n-at/ffmpeg-builds/releases/download/v4.4/macos-ffprobe-4.4.zip"

ffmpeg_license_url="https://raw.githubusercontent.com/n-at/ffmpeg-builds/main/LICENSE"

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
  build_path="gifr_${build_name}"

  if [ "${build_platform}" = "windows" ]; then
    run_script_source="${build_name}_run.bat"
    run_script_destination="gifr.bat"
    ffmpeg_executable_name="ffmpeg.exe"
    ffprobe_executable_name="ffprobe.exe"
  else
    run_script_source="${build_name}_run.sh"
    run_script_destination="gifr.sh"
    ffmpeg_executable_name="ffmpeg"
    ffprobe_executable_name="ffprobe"
  fi

  if [ -x "${build_path}" ]; then
    rm -r "${build_path}"
  fi
  mkdir -m 0777 "${build_path}"

  download_binary "${ffmpeg_license_url}" "./LICENSE-ffmpeg"

  cp "../target/gifr.jar" "${build_path}/gifr.jar"
  cp "../LICENSE" "${build_path}/LICENSE-gifr"
  cp "./LICENSE-ffmpeg" "${build_path}/LICENSE-ffmpeg"
  cp "../scripts/${run_script_source}" "${build_path}/${run_script_destination}"
  chmod 0777 "${build_path}/${run_script_destination}"

  ffmpeg_path="ffmpeg_${build_name}"
  archive_name="ffmpeg_${build_name}.tar.xz"

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

      download_binary "${download_url}" "${archive_name}"
      unpack_tar "${archive_name}" "${ffmpeg_path}"
    ;;

    "macos")
      mkdir "${ffmpeg_path}"
      cd "${ffmpeg_path}"
      download_binary "${macos_amd64_ffmpeg_url}" "ffmpeg.zip"
      unzip -u "ffmpeg.zip"
      download_binary "${macos_amd64_ffprobe_url}" "ffprobe.zip"
      unzip -u "ffprobe.zip"
      cd ..
    ;;

    "windows")
      download_binary "${windows_amd64_package_url}" "${archive_name}"
      unpack_tar "${archive_name}" "${ffmpeg_path}"
      ffmpeg_path="${ffmpeg_path}/bin"
    ;;
  esac

  cp "${ffmpeg_path}/${ffmpeg_executable_name}" "${build_path}/${ffmpeg_executable_name}"
  cp "${ffmpeg_path}/${ffprobe_executable_name}" "${build_path}/${ffprobe_executable_name}"
  chmod 0777 "${build_path}/${ffmpeg_executable_name}" "${build_path}/${ffprobe_executable_name}"

  if [ "${build_platform}" = "windows" ]; then
    zip -r "${build_path}.zip" "${build_path}"
  else
    tar -cvzf "${build_path}.tar.gz" "${build_path}"
  fi
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

cd "release"

release "linux" "amd64"
release "linux" "arm64"
release "windows" "amd64"
release "macos" "amd64"

###

cd ..

rm -r "./src/main/resources/public/build"
./mvnw clean
