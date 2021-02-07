<template>
    <template v-if="isEmpty"></template>
    <template v-else-if="isLoading">
        <LoadingState/>
    </template>
    <template v-else-if="isError">
        <ErrorState :message="errorMessage"/>
    </template>
    <template v-else>
        <VideoJS :url="url"/>
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
            url() {
                return this.$store.state.videoPlayer.url;
            },
        },
    };
</script>

<style>

</style>
