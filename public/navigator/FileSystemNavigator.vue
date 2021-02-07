<template>

    <template v-if="isEmpty">
        <EmptyState :message="'File list not yet loaded'"></EmptyState>
    </template>
    <template v-else-if="isLoading">
        <LoadingState></LoadingState>
    </template>
    <template v-else-if="isError">
        <ErrorState :message="errorMessage"></ErrorState>
    </template>
    <template v-else>
        <div class="file-system-navigator card">
            <div class="card-body">
                <div class="file-system-navigator-path mb-3">
                    <button type="button" class="btn btn-sm btn-outline-secondary mr-3" title="Go up one level"
                            @click="gotoPrevious">
                        <i class="fa fa-arrow-circle-up"></i>
                    </button>
                    <span class="text-secondary">
                        {{ currentPath }}
                    </span>
                </div>
                <div class="file-system-navigator-list list-group">
                    <button type="button" class="list-group-item list-group-item-action"
                            v-for="entry in entries" :key="entry.name" @click="open(entry)">
                        <i class="fa fa-folder" v-if="entry.type === 'Directory'"></i>
                        <i class="fa fa-file" v-if="entry.type === 'File'"></i>
                        {{ entry.name }}
                    </button>
                </div>
            </div>
        </div>
    </template>

</template>

<script>
    import Constants from '../store/constants'
    import EmptyState from "../common/EmptyState.vue";
    import LoadingState from "../common/LoadingState.vue";
    import ErrorState from "../common/ErrorState.vue";
    import Api from '../store/api';

    export default {
        components: {
            EmptyState,
            LoadingState,
            ErrorState,
        },

        computed: {
            isEmpty() {
                return this.$store.state.fileSystemNavigator.state === Constants.STATE_EMPTY;
            },
            isLoading() {
                return this.$store.state.fileSystemNavigator.state === Constants.STATE_LOADING;
            },
            isError() {
                return this.$store.state.fileSystemNavigator.state === Constants.STATE_ERROR;
            },
            errorMessage() {
                return this.$store.state.fileSystemNavigator.error;
            },
            currentPath() {
                if (this.$store.state.fileSystemNavigator.data && this.$store.state.fileSystemNavigator.data.currentPath) {
                    return this.$store.state.fileSystemNavigator.data.currentPath;
                } else {
                    return '';
                }
            },
            entries() {
                return this.$store.state.fileSystemNavigator.data.entries;
            },
        },

        mounted() {
            Api.loadFileList(null);
        },

        methods: {
            gotoPrevious() {
                if (this.$store.state.fileSystemNavigator.data && this.$store.state.fileSystemNavigator.data.previousPath) {
                    Api.loadFileList(this.$store.state.fileSystemNavigator.data.previousPath);
                }
            },
            open(entry) {
                if (entry.type === 'Directory') {
                    Api.loadFileList(entry.fullPath);
                } else if (entry.type === 'File') {
                    //TODO load file info
                }
            },
        },
    };
</script>

<style>
    .file-system-navigator-list {
        overflow-y: auto;
        max-height: 300px;
    }
</style>
