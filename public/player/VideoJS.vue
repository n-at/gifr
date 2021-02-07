<template>
    <div class="mt-3">
        <video ref="videoPlayer" class="video-js" width="640" height="480"></video>
        <div class="mt-3 text-center">
            <button type="button" class="btn btn-sm btn-outline-secondary" title="Previous frame"
                    @click="previousFrame">
                <i class="fa fa-backward"></i>
            </button>
            <button type="button" class="btn btn-sm btn-outline-secondary ml-1" title="Next frame"
                    @click="nextFrame">
                <i class="fa fa-forward"></i>
            </button>

            <button type="button" class="btn btn-sm btn-outline-primary ml-3" title="Set start position"
                    @click="setStartPosition">
                <i class="fa fa-fast-backward"></i>
            </button>
            <button type="button" class="btn btn-sm btn-outline-primary ml-1" title="Set end position"
                    @click="setEndPosition">
                <i class="fa fa-fast-forward"></i>
            </button>
        </div>
        <div class="mt-1">
            <div>
                <em>Start:</em> {{ timeStart }}
            </div>
            <div>
                <em>End:</em> {{ timeEnd }}
            </div>
        </div>
    </div>
</template>

<script>
    import videojs from 'video.js'
    import 'videojs-contrib-quality-levels'
    import 'videojs-hls-quality-selector'
    import Constants from '../store/constants'
    import TimeUtils from '../utils/time'

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
                }],
            });
            this.player.hlsQualitySelector();
        },

        beforeUnmount() {
            if (this.player) {
                this.player.dispose();
            }
        },

        computed: {
            timeStart() {
                if (this.$store.state.position.start !== null) {
                    return TimeUtils.formatTime(this.$store.state.position.start);
                } else {
                    return 'not defined';
                }
            },

            timeEnd() {
                if (this.$store.state.position.end !== null) {
                    return TimeUtils.formatTime(this.$store.state.position.end);
                } else {
                    return 'not defined';
                }
            },
        },

        methods: {
            nextFrame() {
                this.player.pause();
                this.player.currentTime(this.player.currentTime() + 1.0/25.0);
            },

            previousFrame() {
                this.player.pause();
                this.player.currentTime(this.player.currentTime() - 1.0/25.0);
            },

            setStartPosition() {
                this.$store.commit(Constants.MUTATION_POSITION_START, this.player.currentTime());
            },

            setEndPosition() {
                this.$store.commit(Constants.MUTATION_POSITION_END, this.player.currentTime());
            },
        },
    };
</script>

<style>
    .video-js {
        margin: 0 auto;
    }
</style>
