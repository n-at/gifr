<template>
    <div class="mt-3">
        <video ref="videoPlayer" class="video-js" width="640" height="480"></video>
    </div>
</template>

<script>
    import videojs from 'video.js'
    import 'videojs-contrib-quality-levels'
    import 'videojs-hls-quality-selector'

    export default {
        props: ['url'],

        data() {
            return {
                player: null,
            };
        },

        mounted() {
            this.player = videojs(this.$refs.videoPlayer, {
                controls: true,
                autoplay: false,
                preload: 'auto',
                sources: [{
                    src: this.$props.url,
                    type: 'application/dash+xml',
                }]
            });
            this.player.hlsQualitySelector();
        },

        beforeUnmount() {
            if (this.player) {
                this.player.dispose();
            }
        },
    };
</script>

<style>
    .video-js {
        margin: 0 auto;
    }
</style>
