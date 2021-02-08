<template>
    <FileSystemNavigator/>
    <VideoFileInfo/>
    <VideoPlayer/>
    <Export/>
</template>

<script>
    import FileSystemNavigator from "./navigator/FileSystemNavigator.vue";
    import VideoFileInfo from "./video-file-info/VideoFileInfo.vue";
    import VideoPlayer from "./player/VideoPlayer.vue";
    import Export from './export/Export.vue';

    export default {
        components: {
            FileSystemNavigator,
            VideoFileInfo,
            VideoPlayer,
            Export,
        },
    };
</script>
