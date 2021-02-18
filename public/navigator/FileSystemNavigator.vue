<template>

    <template v-if="isEmpty">
        <EmptyState :message="'File list not yet loaded'"/>
    </template>
    <template v-else-if="isLoading">
        <LoadingState/>
    </template>
    <template v-else-if="isError">
        <ErrorState :message="errorMessage"/>
    </template>
    <template v-else>
        <div class="file-system-navigator card">
            <div class="card-body">
                <h5 class="card-title">Choose a video file</h5>
                <div class="file-system-navigator-path mb-3">
                    <button type="button" class="btn btn-sm btn-outline-primary mr-3" title="Go up one level"
                            @click="gotoPrevious">
                        <i class="fa fa-arrow-circle-up"></i>
                    </button>
                    <span class="text-secondary">
                        <button type="button" class="btn btn-sm btn-outline-secondary mr-1"
                                v-for="entry in currentPathEntries"
                                @click="goto(entry.path)">
                            /{{ entry.name }}
                        </button>
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
            currentPathEntries() {
                const currentPath = this.currentPath;
                if (currentPath === '') {
                    return [];
                }

                let path = '/';
                let entries = [];

                currentPath.split('/').forEach(segment => {
                    if (segment === '' && entries.length !== 0) {
                        return;
                    }
                    path += segment + '/';
                    entries.push({
                        name: segment,
                        path: path,
                    });
                });

                return entries;
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
            goto(path) {
                Api.loadFileList(path);
            },
            open(entry) {
                if (entry.type === 'Directory') {
                    Api.loadFileList(entry.fullPath);
                } else if (entry.type === 'File') {
                    Api.videoFileInfo(entry.fullPath);
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
