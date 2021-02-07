<template>
    <FileSystemNavigator/>
    <VideoFileInfo/>
    <VideoPlayer/>
</template>

<script>
    import FileSystemNavigator from "./navigator/FileSystemNavigator.vue";
    import VideoFileInfo from "./video-file-info/VideoFileInfo.vue";
    import VideoPlayer from "./player/VideoPlayer.vue";

    export default {
        components: {
            FileSystemNavigator,
            VideoFileInfo,
            VideoPlayer,
        },
    };
</script>

<style>

</style>
