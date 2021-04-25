<template>
    <template v-if="isEmpty"></template>
    <template v-else-if="isLoading">
        <LoadingState/>
    </template>
    <template v-else-if="isError">
        <ErrorState :message="errorMessage"/>
    </template>
    <template v-else>
        <div class="card mb-3">
            <div class="card-body">
                <h5 class="card-title">Edit video fragment</h5>

                <div class="text-center">
                    <template v-for="(preview, idx) in previews">
                        <img :id="frameId(idx)" :src="preview" :alt="preview" class="d-none">
                    </template>
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
                currentFrame: 0,
                timeoutId: null,
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
            framesMin() {
                return 0;
            },
            framesMax() {
                return this.$store.state.editor.frames-1 ?? 1;
            },

            previews() {
                if (!this.exportId || !this.framesMax) {
                    return [];
                }
                const previews = [];
                for (let frameIdx = 0; frameIdx <= this.framesMax; frameIdx++) {
                    previews.push(`/export-frames/preview/${this.exportId}/${frameIdx}`);
                }
                return previews;
            },
        },

        watch: {
            exportId() {
                setTimeout(() => this.resetRange());
            },
        },

        methods: {
            cancel() {
                this.$store.commit(Constants.MUTATION_EDITOR_EMPTY);
            },
            save() {
                const exportId = this.exportId;
                const framerate = this.$store.state.editor.framerate;
                const start = this.range[0];
                const end = this.range[1];

                window.open(`/export-frames/gif?id=${exportId}&framerate=${framerate}&start=${start}&end=${end}`);
            },
            resetRange() {
                this.range = [this.framesMin, this.framesMax];
            },

            nextFrame() {
                let nextFrameIdx = this.currentFrame + 1;
                if (nextFrameIdx > this.range[1]) {
                    nextFrameIdx = this.range[0];
                }

                const nextFrameId = this.frameId(nextFrameIdx);
                const nextFrame = document.getElementById(nextFrameId);
                if (nextFrame) {
                    nextFrame.classList = '';
                }

                const currentFrameId = this.frameId(this.currentFrame);
                const currentFrame = document.getElementById(currentFrameId);
                if (currentFrame) {
                    currentFrame.classList = 'd-none';
                }

                const timeout = 1000.0 / this.$store.state.editor.framerate;

                this.currentFrame = nextFrameIdx;
                this.timeoutId = setTimeout(() => this.nextFrame(), timeout);
            },
            frameId(frameIdx) {
                return `preview-${this.exportId}-${frameIdx}`;
            },
        },

        mounted() {
            this.nextFrame();
        },
        beforeUnmount() {
            clearTimeout(this.timeoutId);
        },
    };
</script>

<style src="@vueform/slider/themes/default.css"></style>
<style>
    .slider-connect {
        background-color: #007bff;
    }
</style>
