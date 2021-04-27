<template>
    <div class="d-flex align-items-center mb-3">
        <button type="button" class="btn btn-lg btn-outline-secondary title-button-open" title="Open video file" @click="open">
            <i class="fa fa-bars"></i>
        </button>

        <div class="d-inline-block ml-3">
            <h1 class="title-header">{{title}}</h1>
            <div>
                <small>{{subtitle}}</small>
            </div>
        </div>
    </div>
</template>

<script>
    import Constants from '../store/constants'

    export default {
        computed: {
            title() {
                const videoFileName = this.$store.state.videoPlayer.path;
                if (!videoFileName) {
                    return 'Open video file';
                }
                const pathParts = videoFileName.split('/');
                const fileNameParts = pathParts[pathParts.length-1].split('.');
                fileNameParts.pop();
                return fileNameParts.join('.');
            },
            subtitle() {
                const videoFileName = this.$store.state.videoPlayer.path;
                return videoFileName ? videoFileName : '';
            },
        },

        methods: {
            open() {
                this.$store.commit(Constants.MUTATION_OPEN_FILE_PANEL, true);
            },
        },
    };
</script>

<style>
    .title-header {
        margin: 0 !important;
        padding: 0 !important;
    }
    .title-button-open {
        width: 58px;
        height: 58px;
    }
</style>
