#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd "${DIR}"

xattr -dr com.apple.quarantine ./ffmpeg
xattr -dr com.apple.quarantine ./ffprobe

java -Xmx64m -jar gifr.jar
