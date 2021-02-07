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
                        <div>
                            <em>Path:</em> {{ fileInfo.path }}
                        </div>
                        <div>
                            <em>Size:</em> {{ fileInfo.resolution }}
                        </div>
                        <div>
                            <em>Length:</em> {{ fileInfo.duration }}
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
    import Constants from "../store/constants";
    import LoadingState from "../common/LoadingState.vue";
    import ErrorState from "../common/ErrorState.vue";

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

                const intSeconds = Math.trunc(info.duration);
                const minutes = Math.trunc(intSeconds / 60);
                const hours = Math.trunc(intSeconds / 3600);
                const frac = info.duration - intSeconds;
                const seconds = intSeconds % 60 + Math.round(frac * 1000) / 1000;

                return {
                    path: info.path,
                    checksum: info.checksum,
                    resolution: `${info.width}x${info.height}`,
                    duration: `${hours < 10 ? '0'+hours : hours}:${minutes < 10 ? '0'+minutes : minutes}:${seconds < 10 ? '0'+seconds : seconds}`,
                };
            },
        },

        methods: {
            open() {
                //TODO open video file in player
            },
        }
    };
</script>
