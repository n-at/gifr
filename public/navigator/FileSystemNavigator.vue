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
        <div class="file-system-navigator card mb-3">
            <div class="card-body">
                <div class="file-system-navigator-path">
                    <span class="text-secondary">
                        <button type="button" class="btn btn-sm btn-outline-secondary mr-1"
                                v-for="entry in currentPathEntries"
                                @click="open(entry)">
                            <span v-if="entry.fullPath === 'FILE_SYSTEM_ROOTS'">
                               <i class="fa fa-desktop"></i>
                            </span>
                            {{ entry.name }}
                        </button>
                    </span>
                </div>
            </div>
            <div class="file-system-navigator-list list-group list-group-flush">
                <button type="button" class="list-group-item list-group-item-action" :class="isSelectedEntry(entry) ? 'active' : ''"
                        v-for="entry in entries" :key="entry.name" @click="open(entry)">
                    <i class="fa fa-folder" v-if="entry.type === 'Directory'"></i>
                    <i class="fa fa-file" v-if="entry.type === 'File'"></i>
                    {{ entry.name }}
                </button>
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
                return this.$store.state.fileSystemNavigator.state === Constants.State.Empty;
            },
            isLoading() {
                return this.$store.state.fileSystemNavigator.state === Constants.State.Loading;
            },
            isError() {
                return this.$store.state.fileSystemNavigator.state === Constants.State.Error;
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
            separator() {
                return this.$store.state.fileSystemNavigator.data.separator;
            },
            currentPathEntries() {
                let currentPath = this.currentPath;
                if (currentPath === '' || currentPath === 'FILE_SYSTEM_ROOTS') {
                    return [];
                }
                if (currentPath[currentPath.length-1] === this.separator) {
                    currentPath = currentPath.substring(0, currentPath.length-1);
                }

                let path = '';
                let entries = [{
                    type: 'Directory',
                    name: '',
                    fullPath: 'FILE_SYSTEM_ROOTS',
                }];

                currentPath.split(this.separator).forEach(segment => {
                    path += segment + this.separator;
                    entries.push({
                        type: 'Directory',
                        name: segment.length === 0 ? this.separator : segment,
                        fullPath: path,
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
            open(entry) {
                if (entry.type === 'Directory') {
                    Api.loadFileList(entry.fullPath);
                } else if (entry.type === 'File') {
                    Api.videoFileInfo(entry.fullPath);
                }
            },
            isSelectedEntry(entry) {
                const videoFileInfoData = this.$store.state.videoFileInfo.data;
                if (!videoFileInfoData) {
                    return false;
                }
                return videoFileInfoData.path === entry.fullPath;
            },
        },
    };
</script>

<style>

</style>
