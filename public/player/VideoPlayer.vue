<template>
    <template v-if="isEmpty"></template>
    <template v-else-if="isLoading">
        <LoadingState/>
    </template>
    <template v-else-if="isError">
        <ErrorState :message="errorMessage"/>
    </template>
    <template v-else>
        <VideoJS :sources="sources" :tracks="tracks"/>
    </template>
</template>

<script>
    import Constants from '../store/constants'
    import LoadingState from "../common/LoadingState.vue"
    import ErrorState from "../common/ErrorState.vue"
    import VideoJS from "./VideoJS.vue"

    export default {
        components: {
            LoadingState,
            ErrorState,
            VideoJS,
        },

        computed: {
            isEmpty() {
                return this.$store.state.videoPlayer.state === Constants.STATE_EMPTY;
            },
            isLoading() {
                return this.$store.state.videoPlayer.state === Constants.STATE_LOADING;
            },
            isError() {
                return this.$store.state.videoPlayer.state === Constants.STATE_ERROR;
            },
            errorMessage() {
                return this.$store.state.videoPlayer.error;
            },
            sources() {
                const videoFileId = this.$store.state.videoPlayer.id;
                return [{
                    src: `/video/dash/${videoFileId}.mpd`,
                    type: 'application/dash+xml',
                }];
            },
            tracks() {
                const subtitles = this.$store.state.videoPlayer.subtitles;
                if (!subtitles || !subtitles.length) {
                    return [];
                }

                const videoFileId = this.$store.state.videoPlayer.id;

                return subtitles.map((subtitles, idx) => {
                    return {
                        src: `/video/subtitles/${videoFileId}-${idx}.vtt`,
                        kind: 'subtitles',
                        srclang: subtitles.language,
                        label: `${subtitles.language}: ${subtitles.title}`,
                    }
                });
            },
        },
    };
</script>

<style>

</style>
