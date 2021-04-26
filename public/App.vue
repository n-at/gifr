<template>
    <ApplicationHeader/>

    <div :class="{'d-none': !this.$store.state.openFilePanel}">
        <OpenVideoFileTitle/>
        <div class="row">
            <div class="col-6">
                <FileSystemNavigator/>
            </div>
            <div class="col-6">
                <VideoFileInfo/>
            </div>
        </div>
    </div>

    <div :class="{'d-none': this.$store.state.openFilePanel}">
        <VideoPlayerTitle/>
        <VideoPlayer/>
        <div class="row">
            <div class="col-6">
                <Export/>
            </div>
            <div class="col-6">
                <Editor/>
            </div>
        </div>
    </div>
</template>

<script>
    import ApplicationHeader from "./common/Header.vue";
    import VideoPlayerTitle from './common/VideoPlayerTitle.vue';
    import OpenVideoFileTitle from "./common/OpenVideoFileTitle.vue";
    import FileSystemNavigator from "./navigator/FileSystemNavigator.vue";
    import VideoFileInfo from "./video-file-info/VideoFileInfo.vue";
    import VideoPlayer from "./player/VideoPlayer.vue";
    import Export from './export/Export.vue';
    import Editor from './editor/Editor.vue';

    export default {
        components: {
            ApplicationHeader,
            VideoPlayerTitle,
            OpenVideoFileTitle,
            FileSystemNavigator,
            VideoFileInfo,
            VideoPlayer,
            Export,
            Editor,
        },
    };
</script>
