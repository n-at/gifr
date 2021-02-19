<template>
    <ApplicationHeader/>
    <div class="row">
        <div class="col-6">
            <FileSystemNavigator/>
        </div>
        <div class="col-6">
            <VideoFileInfo/>
        </div>
    </div>
    <VideoPlayer/>
    <Export/>
</template>

<script>
    import ApplicationHeader from "./header/Header.vue";
    import FileSystemNavigator from "./navigator/FileSystemNavigator.vue";
    import VideoFileInfo from "./video-file-info/VideoFileInfo.vue";
    import VideoPlayer from "./player/VideoPlayer.vue";
    import Export from './export/Export.vue';

    export default {
        components: {
            ApplicationHeader,
            FileSystemNavigator,
            VideoFileInfo,
            VideoPlayer,
            Export,
        },
    };
</script>
