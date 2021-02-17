<template>
    <template v-if="isEmpty"></template>
    <template v-else-if="isLoading">
        <LoadingState/>
    </template>
    <template v-else-if="isError">
        <ErrorState :message="errorMessage"/>
    </template>
    <template v-else>
        <div class="alert alert-secondary mt-3" v-if="notAVideoFile">
            Not a video file
        </div>
        <div class="card mt-3" v-else>
            <div class="card-body">
                <div class="row">
                    <div class="col-10">
                        <div class="text-primary">
                            <i class="fa fa-play"></i> {{ fileInfo.path }}
                        </div>
                        <div>
                            <i class="fa fa-clock" title="Length"></i> {{ fileInfo.duration }}
                        </div>
                        <div class="row">
                            <div class="col-6">
                                <div><strong>Video</strong></div>
                                <div class="row">
                                    <div class="col-4">Codec:</div>
                                    <div class="col-8">{{ fileInfo.video.codec }}</div>

                                    <div class="col-4">Resolution:</div>
                                    <div class="col-8">{{ fileInfo.video.width }}x{{ fileInfo.video.height }}</div>

                                    <div class="col-4">Frame rate:</div>
                                    <div class="col-8">{{ fileInfo.video.framerate }}</div>

                                    <div class="col-4">Bitrate:</div>
                                    <div class="col-8">{{ fileInfo.video.bitrate / 1000 }} kbit/s</div>

                                    <div class="col-4">Pixel format:</div>
                                    <div class="col-8">{{ fileInfo.video.pixelFormat }}</div>
                                </div>
                            </div>
                            <div class="col-6">
                                <div><strong>Audio</strong></div>
                                <div class="row">
                                    <div class="col-4">Codec:</div>
                                    <div class="col-8">{{ fileInfo.audio.codec }}</div>

                                    <div class="col-4">Channels:</div>
                                    <div class="col-8">{{ fileInfo.audio.channels }}</div>

                                    <div class="col-4">Sample rate:</div>
                                    <div class="col-8">{{ fileInfo.audio.sampleRate }} Hz</div>

                                    <div class="col-4">Bitrate:</div>
                                    <div class="col-8">{{ fileInfo.audio.bitrate / 1000 }} kbit/s</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-2 text-right">
                        <button type="button" class="btn btn-outline-primary"
                                @click="open">
                            <i class="fa fa-play"></i> Open
                        </button>
                    </div>
                </div>

            </div>
        </div>
    </template>
</template>

<script>
    import Constants from "../store/constants"
    import LoadingState from "../common/LoadingState.vue"
    import ErrorState from "../common/ErrorState.vue"
    import TimeUtils from '../utils/time'

    export default {
        components: {
            LoadingState,
            ErrorState,
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
                        url: `/video/dash/${videoFileId}.mpd`,
                    });
                });
            },
        }
    };
</script>
