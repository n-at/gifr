<template>
    <div v-if="timeVisible" class="card mb-3">
        <div class="video-fragment-export card-body">
            <h5 class="card-title">Save video fragment</h5>
            <div class="row">
                <div class="col-2">Start:</div>
                <div class="col-10">{{ timeStart }}</div>

                <div class="col-2">End:</div>
                <div class="col-10">{{ timeEnd }}</div>

                <div class="col-2">Length:</div>
                <div class="col-10">{{ duration }}</div>
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
                <div class="text-right">
                    <button type="button" class="btn btn-outline-secondary mr-2" @click="cancel">
                        Cancel
                    </button>
                    <button type="button" class="btn btn-outline-primary mr-2" @click="save">
                        <i class="fa fa-download"></i> Save gif
                    </button>
                    <button type="button" class="btn btn-outline-primary" @click="edit">
                        <i class="fa fa-arrow-right"></i> Edit
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import TimeUtils from '../utils/time'
    import Constants from '../store/constants'
    import Api from '../store/api'

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

            duration() {
                if (this.exportVisible) {
                    const diff = this.$store.state.position.start - this.$store.state.position.end;
                    return TimeUtils.formatTime(Math.abs(diff));
                } else {
                    return 'not defined';
                }
            },
        },

        methods: {
            save() {
                const videoFileId = this.$store.state.videoPlayer.id;
                const timeStart = this.$store.state.position.start;
                const timeEnd = this.$store.state.position.end;

                window.open(`/export?id=${videoFileId}&start=${timeStart}&end=${timeEnd}&framerate=${this.framerate}&size=${this.size}` );
            },

            edit() {
                Api.exportVideoFragment(this.framerate, this.size);
            },

            cancel() {
                this.$store.commit(Constants.MUTATION_POSITION_START, null);
                this.$store.commit(Constants.MUTATION_POSITION_END, null);
            },
        },
    };
</script>

<style>
    .video-fragment-export {
        height: 500px;
    }
</style>
