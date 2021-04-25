<template>
    <template v-if="isEmpty"></template>
    <template v-else-if="isLoading">
        <LoadingState/>
    </template>
    <template v-else-if="isError">
        <ErrorState :message="errorMessage"/>
    </template>
    <template v-else>
        <div class="video-fragment-editor card mb-3">
            <div class="card-body">
                <h5 class="card-title">Edit video fragment</h5>

                <div class="text-center">
                    <canvas ref="editorPreview" @click="playPreviewToggle"></canvas>
                </div>

                <slider v-model="range" :tooltips="false" class="mt-3"
                        :min="framesMin" :max="framesMax" :step="1" />

                <hr>
                <div class="form-group">
                    <label for="export-frame-rate">Frame rate</label>
                    <select v-model="this.$store.state.editor.framerate" class="custom-select" id="export-frame-rate">
                        <option value="30">30 fps</option>
                        <option value="25">25 fps</option>
                        <option value="20">20 fps</option>
                        <option value="15">15 fps</option>
                        <option value="10">10 fps</option>
                    </select>
                </div>
                <div class="text-right">
                    <button type="button" class="btn btn-outline-secondary mr-2" @click="cancel">Cancel</button>
                    <button type="button" class="btn btn-outline-primary" @click="save">
                        <i class="fa fa-download"></i> Save gif
                    </button>
                </div>
            </div>
        </div>
    </template>
</template>

<script>
import Slider from '@vueform/slider'
import Constants from '../store/constants'
import LoadingState from '../common/LoadingState.vue'
import ErrorState from '../common/ErrorState.vue'

export default {
        components: {
            LoadingState,
            ErrorState,
            Slider,
        },

        data() {
            return {
                range: [0, 0],
                currentFrameIdx: 0,
                playPreview: true,
            };
        },

        computed: {
            isEmpty() {
                return this.$store.state.editor.state === Constants.STATE_EMPTY;
            },
            isLoading() {
                return this.$store.state.editor.state === Constants.STATE_LOADING;
            },
            isError() {
                return this.$store.state.editor.state === Constants.STATE_ERROR;
            },
            errorMessage() {
                return this.$store.state.editor.error;
            },

            exportId() {
                return this.$store.state.editor.id;
            },
            framerate() {
                return this.$store.state.editor.framerate;
            },
            framesMin() {
                return 0;
            },
            framesMax() {
                return this.$store.state.editor.frames-1 ?? 1;
            },

            previewFrames() {
                if (!this.exportId || !this.framesMax) {
                    return {};
                }
                const frames = {};
                for (let frameIdx = this.framesMin; frameIdx <= this.framesMax; frameIdx++) {
                    const image = new Image();
                    image.onload = () => frames[frameIdx] = image;
                    image.src = `/export-frames/preview/${this.exportId}/${frameIdx}`;
                }
                return frames;
            },
        },

        watch: {
            exportId(value) {
                this.playPreview = !!value;
                this.currentFrameIdx = 0;
                this.nextPreviewFrame();
            },
            framesMin(value) {
                this.range[0] = value;
            },
            framesMax(value) {
                this.range[1] = value;
            },
        },

        methods: {
            cancel() {
                this.$store.commit(Constants.MUTATION_EDITOR_EMPTY);
            },
            save() {
                const start = this.range[0];
                const end = this.range[1];
                window.open(`/export-frames/gif?id=${this.exportId}&framerate=${this.framerate}&start=${start}&end=${end}`);
            },

            nextPreviewFrame() {
                let nextFrameIdx = this.currentFrameIdx + 1;
                if (nextFrameIdx > this.range[1]) {
                    nextFrameIdx = this.range[0];
                }
                this.currentFrameIdx = nextFrameIdx;

                if (this.previewFrames[nextFrameIdx]) {
                    const frame = this.previewFrames[nextFrameIdx];
                    const canvas = this.$refs.editorPreview;
                    canvas.width = frame.width;
                    canvas.height = frame.height;
                    canvas.getContext('2d').drawImage(frame, 0, 0);
                }
                if (this.playPreview) {
                    setTimeout(() => this.nextPreviewFrame(), 1000.0 / this.framerate);
                }
            },
            playPreviewToggle() {
                this.playPreview = !this.playPreview;
                this.nextPreviewFrame();
            },
        },
    };
</script>

<style src="@vueform/slider/themes/default.css"></style>
<style>
    .slider-connect {
        background-color: #007bff;
    }
    .video-fragment-editor {
        height: 500px;
    }
</style>
