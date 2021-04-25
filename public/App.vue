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
    <div class="row">
        <div class="col-6">
            <Export/>
        </div>
        <div class="col-6">
            <Editor/>
        </div>
    </div>
</template>

<script>
    import ApplicationHeader from "./header/Header.vue";
    import FileSystemNavigator from "./navigator/FileSystemNavigator.vue";
    import VideoFileInfo from "./video-file-info/VideoFileInfo.vue";
    import VideoPlayer from "./player/VideoPlayer.vue";
    import Export from './export/Export.vue';
    import Editor from './editor/Editor.vue';

    export default {
        components: {
            ApplicationHeader,
            FileSystemNavigator,
            VideoFileInfo,
            VideoPlayer,
            Export,
            Editor,
        },
    };
</script>
