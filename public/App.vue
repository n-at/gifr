<template>
    <FileSystemNavigator></FileSystemNavigator>
    <VideoFileInfo></VideoFileInfo>
</template>

<script>
    import FileSystemNavigator from "./navigator/FileSystemNavigator.vue";
    import VideoFileInfo from "./video-file-info/VideoFileInfo.vue";

    export default {
        components: {
            FileSystemNavigator,
            VideoFileInfo,
        },
    };
</script>

<style>

</style>
