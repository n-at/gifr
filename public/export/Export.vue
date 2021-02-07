<template>
    <div v-if="timeVisible" class="card mt-3 mb-3">
        <div class="card-body">
            <div>
                <em>Start:</em> {{ timeStart }}
            </div>
            <div>
                <em>End:</em> {{ timeEnd }}
            </div>

            <div v-if="exportVisible">
                <hr>
                <div class="form-group">
                    <label for="export-frame-rate">Frame rate</label>
                    <select v-model="framerate" class="custom-select" id="export-frame-rate">
                        <option value="30">30 fps</option>
                        <option value="25">25 fps</option>
                        <option value="20">20 fps</option>
                        <option value="15">15 fps</option>
                        <option value="10">10 fps</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="export-size">Size</label>
                    <select v-model="size" class="custom-select" id="export-size">
                        <option value="120">120p</option>
                        <option value="240">240p</option>
                        <option value="360">360p</option>
                        <option value="480">480p</option>
                        <option value="640">640p</option>
                        <option value="720">720p</option>
                        <option value="960">960p</option>
                        <option value="1080">1080p</option>
                    </select>
                </div>
                <div>
                    <button type="button" class="btn btn-outline-primary"
                            @click="exportFile">
                        <i class="fa fa-download"></i> Export gif
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import TimeUtils from '../utils/time'

    export default {
        data() {
            return {
                framerate: 25,
                size: 480,
            };
        },

        computed: {
            timeVisible() {
                return this.$store.state.position.start !== null ||
                       this.$store.state.position.end !== null;
            },

            exportVisible() {
                return this.$store.state.position.start !== null &&
                       this.$store.state.position.end !== null;
            },

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
            exportFile() {
                const videoFileId = this.$store.state.videoPlayer.id;
                const timeStart = this.$store.state.position.start;
                const timeEnd = this.$store.state.position.end;

                window.open(`/export?id=${videoFileId}&start=${timeStart}&end=${timeEnd}&framerate=${this.framerate}&size=${this.size}` );
            },
        },
    };
</script>

<style>

</style>