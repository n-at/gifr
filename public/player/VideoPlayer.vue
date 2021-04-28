<template>
    <template v-if="isEmpty">
        <VideoPlayerEmpty/>
    </template>
    <template v-else-if="isLoading">
        <LoadingState/>
    </template>
    <template v-else-if="isError">
        <ErrorState :message="errorMessage"/>
    </template>
    <template v-else>
        <VideoJS :sources="sources"
                 :tracks="tracks"
                 @start-position="setStartPosition"
                 @end-position="setEndPosition"/>
    </template>
</template>

<script>
    import Constants from '../store/constants'
    import VideoPlayerEmpty from "./VideoPlayerEmpty.vue"
    import LoadingState from "../common/LoadingState.vue"
    import ErrorState from "../common/ErrorState.vue"
    import VideoJS from "./VideoJS.vue"

    export default {
        components: {
            LoadingState,
            ErrorState,
            VideoJS,
            VideoPlayerEmpty,
        },

        computed: {
            isEmpty() {
                return this.$store.state.videoPlayer.state === Constants.State.Empty;
            },
            isLoading() {
                return this.$store.state.videoPlayer.state === Constants.State.Loading;
            },
            isError() {
                return this.$store.state.videoPlayer.state === Constants.State.Error;
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

        methods: {
            setStartPosition(position) {
                this.$store.commit(Constants.Mutation.RecordPosition.Start, position);
            },
            setEndPosition(position) {
                this.$store.commit(Constants.Mutation.RecordPosition.End, position);
            },
        },
    };
</script>

<style>

</style>
