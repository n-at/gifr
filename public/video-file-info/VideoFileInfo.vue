<template>
    <template v-if="isEmpty"></template>
    <template v-else-if="isLoading">
        <LoadingState/>
    </template>
    <template v-else-if="isError">
        <ErrorState :message="errorMessage"/>
    </template>
    <template v-else>
        <div class="alert alert-secondary" v-if="notAVideoFile">
            Not a video file
        </div>
        <div v-else class="card">
            <div class="card-body">
                <h5 class="card-title">Video file info</h5>
                <div class="row mb-2">
                    <div class="col-9">
                        <div class="video-file-path text-primary">{{ fileInfo.path }}</div>
                    </div>
                    <div class="col-3 text-right">
                        <button type="button" class="btn btn-outline-primary" @click="open">
                            <i class="fa fa-play"></i> Open
                        </button>
                    </div>
                </div>

                <div class="video-file-info-streams">
                    <VideoStreams :info="fileInfo.video" :duration="fileInfo.duration"/>
                    <AudioStreams v-if="fileInfo.audio.length" :info="fileInfo.audio"/>
                    <SubtitlesStreams v-if="fileInfo.subtitles.length" :info="fileInfo.subtitles"/>
                </div>
            </div>
        </div>
    </template>
</template>

<script>
    import Constants from "../store/constants";
    import LoadingState from "../common/LoadingState.vue";
    import ErrorState from "../common/ErrorState.vue";
    import TimeUtils from "../utils/time";
    import VideoStreams from "./VideoStreams.vue";
    import AudioStreams from "./AudioStreams.vue";
    import SubtitlesStreams from "./SubtitlesStreams.vue";

    export default {
        components: {
            LoadingState,
            ErrorState,
            VideoStreams,
            AudioStreams,
            SubtitlesStreams,
        },

        computed: {
            isEmpty() {
                return this.$store.state.videoFileInfo.state === Constants.STATE_EMPTY;
            },
            isLoading() {
                return this.$store.state.videoFileInfo.state === Constants.STATE_LOADING;
            },
            isError() {
                return this.$store.state.videoFileInfo.state === Constants.STATE_ERROR;
            },
            errorMessage() {
                return this.$store.state.videoFileInfo.error;
            },

            notAVideoFile() {
                return !this.$store.state.videoFileInfo.data;
            },
            fileInfo() {
                const info = this.$store.state.videoFileInfo.data;
                if (info === null) {
                    return {};
                }

                return {
                    path: info.path,
                    checksum: info.checksum,
                    duration: TimeUtils.formatTime(info.duration),
                    video: info.video,
                    audio: info.audio,
                    subtitles: info.subtitles,
                };
            },
        },

        methods: {
            open() {
                this.$store.commit(Constants.MUTATION_VP_LOADING);

                setTimeout(() => {
                    const videoFileId = this.$store.state.videoFileInfo.data.id;
                    this.$store.commit(Constants.MUTATION_VP_PRESENT, {
                        id: videoFileId,
                        subtitles: this.$store.state.videoFileInfo.data.subtitles,
                    });
                });
            },
        }
    };
</script>

<style>
    .video-file-info-streams {
        overflow-y: auto;
        overflow-x: hidden;
        max-height: 300px;
    }
</style>
