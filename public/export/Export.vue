<template>
    <div v-if="timeVisible" class="card mb-3">
        <div class="video-fragment-export card-body">
            <h5 class="card-title">
                Record gif
                <button type="button" class="close" aria-label="Cancel" @click="cancel">
                    <span aria-hidden="true">&times;</span>
                </button>
            </h5>
            <div class="row">
                <div class="col-2">Start:</div>
                <div class="col-10">
                    <span class="time-value mr-2">{{ timeStart }}</span>
                    <div v-if="timeStartDefined" class="btn-group btn-group-sm time-controls">
                        <button type="button" class="btn btn-outline-secondary" title="-1 frame" @click="minusStart">
                            <i class="fa fa-minus-square"></i>
                        </button>
                        <button type="button" class="btn btn-outline-secondary" title="+1 frame" @click="plusStart">
                            <i class="fa fa-plus-square"></i>
                        </button>
                    </div>
                </div>

                <div class="col-2">End:</div>
                <div class="col-10">
                    <span class="time-value mr-2">{{ timeEnd }}</span>
                    <div v-if="timeEndDefined" class="btn-group btn-group-sm time-controls">
                        <button type="button" class="btn btn-outline-secondary" title="-1 frame" @click="minusEnd">
                            <i class="fa fa-minus-square"></i>
                        </button>
                        <button type="button" class="btn btn-outline-secondary" title="+1 frame" @click="plusEnd">
                            <i class="fa fa-plus-square"></i>
                        </button>
                    </div>
                </div>

                <div class="col-2">Length:</div>
                <div class="col-8">
                    <span class="time-value">{{ duration }}</span>
                </div>
            </div>

            <template v-if="exportVisible">
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
            </template>
        </div>
        <div v-if="exportVisible" class="card-footer text-right">
            <button type="button" class="btn btn-outline-primary mr-2" @click="save">
                <i class="fa fa-download"></i> Save gif
            </button>
            <button type="button" class="btn btn-outline-primary" @click="edit">
                <i class="fa fa-arrow-right"></i> Edit
            </button>
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
                return this.timeStartDefined || this.timeEndDefined;
            },

            exportVisible() {
                return this.timeStartDefined && this.timeEndDefined;
            },

            timeStartDefined() {
                return this.$store.state.position.start !== null;
            },
            timeStart() {
                if (this.timeStartDefined) {
                    return TimeUtils.formatTime(this.$store.state.position.start);
                } else {
                    return 'not defined';
                }
            },

            timeEndDefined() {
                return this.$store.state.position.end !== null;
            },
            timeEnd() {
                if (this.timeEndDefined) {
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
                this.$store.commit(Constants.Mutation.RecordPosition.Start, null);
                this.$store.commit(Constants.Mutation.RecordPosition.End, null);
            },

            plusStart() {
                const duration = this.$store.state.videoFileInfo.data.duration;
                const start = this.$store.state.position.start;
                this.$store.commit(Constants.Mutation.RecordPosition.Start, Math.min(duration, start + 1/25.0));
            },
            minusStart() {
                const start = this.$store.state.position.start;
                this.$store.commit(Constants.Mutation.RecordPosition.Start, Math.max(0.0, start - 1/25.0));
            },
            plusEnd() {
                const duration = this.$store.state.videoFileInfo.data.duration;
                const end = this.$store.state.position.end;
                this.$store.commit(Constants.Mutation.RecordPosition.End, Math.min(duration, end + 1/25.0));
            },
            minusEnd() {
                const end = this.$store.state.position.end;
                this.$store.commit(Constants.Mutation.RecordPosition.End, Math.max(0.0, end - 1/25.0));
            },
        },
    };
</script>

<style>
    .video-fragment-export {
        min-height: 430px !important;
    }
    .time-controls .btn {
        padding: 0.05rem 0.25rem !important;
        font-size: 0.7rem !important;
        line-height: 1.7 !important;
    }
    .time-value {
        font-family: Consolas, Monaco, 'Andale Mono', monospace;
    }
</style>
