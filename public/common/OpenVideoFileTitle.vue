<template>
    <div class="d-flex align-items-center mb-3">
        <button type="button" class="btn btn-lg btn-outline-secondary title-button-close" title="Return to player" @click="close">
            <i class="fa fa-times"></i>
        </button>
        <div class="d-inline-block ml-3">
            <h1 class="title-header">Open video file</h1>
        </div>
    </div>
</template>

<script>
    import Constants from '../store/constants'

    export default {
        methods: {
            close() {
                this.$store.commit(Constants.Mutation.OpenFilePanel.Visible, false);
            },
        },
    };
</script>

<style>
    .title-button-close {
        width: 58px;
        height: 58px;
        font-size: 1.5rem !important;
    }
</style>
