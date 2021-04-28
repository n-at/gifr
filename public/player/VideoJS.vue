<template>
    <div class="mt-3">
        <video ref="videoPlayer" class="video-js"></video>
        <div class="mt-3 mb-3 text-center">

            <div class="btn-group btn-group-sm">
                <button type="button" class="btn btn-outline-secondary" title="Previous frame" @click="previousFrame">
                    <i class="fa fa-minus-square"></i> frame
                </button>
                <button type="button" class="btn btn-outline-secondary" title="Next frame" @click="nextFrame">
                    <i class="fa fa-plus-square"></i> frame
                </button>

                <button type="button" class="btn btn-outline-primary" title="Set start position" @click="setStartPosition">
                    <i class="fa fa-fast-backward"></i> Set start
                </button>
                <button type="button" class="btn btn-outline-primary" title="Set end position" @click="setEndPosition">
                    Set end <i class="fa fa-fast-forward"></i>
                </button>

                <button type="button" class="btn btn-outline-info" title="Close video" @click="close">
                    <i class="fa fa-times"></i> Close
                </button>
            </div>

        </div>
    </div>
</template>

<script>
    import videojs from 'video.js'
    import 'videojs-contrib-quality-levels'
    import 'videojs-hls-quality-selector'
    import Constants from '../store/constants'

    export default {
        props: ['sources', 'tracks'],
        emits: ['start-position', 'end-position'],

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
                fluid: true,
                sources: this.sources,
                tracks: this.tracks,
            });
            this.player.hlsQualitySelector();
        },

        beforeUnmount() {
            if (this.player) {
                this.player.dispose();
            }
        },

        methods: {
            close() {
                this.$store.commit(Constants.Mutation.VideoPlayer.Empty);
            },
            nextFrame() {
                this.player.pause();
                this.player.currentTime(this.player.currentTime() + 1.0/25.0);
            },
            previousFrame() {
                this.player.pause();
                this.player.currentTime(this.player.currentTime() - 1.0/25.0);
            },
            setStartPosition() {
                this.$emit('start-position', this.player.currentTime());
            },
            setEndPosition() {
                this.$emit('end-position', this.player.currentTime());
            },
        },
    };
</script>

<style>
    .vjs-menu .vjs-menu-item {
        text-align: left !important;
        padding: 3px !important;
    }
    .vjs-menu .vjs-menu-item.vjs-selected {
        background-color: white !important;
    }
    .vjs-modal-dialog {
        background-color: rgba(43, 51, 63, 0.75) !important;
    }
</style>
