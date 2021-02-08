gifr - gif recorder
===================

Save a fragment of local video file as gif. 
Many formats supported (everything that `ffmpeg` can transcode).

Building
--------

JDK 11, nodejs 15, npm 5 required.

    npm install
    npm run build
    ./mvnw clean package

Everything will be built in one `target/gifr.jar` file.

Running
-------

Tested only on macOS and Linux systems. 
`timeout`, `ffmpeg` and `ffprobe` are required in PATH.

Run with `java`:

    java -jar gifr.jar

Or just (since it is an executable jar):

    ./gifr.jar
