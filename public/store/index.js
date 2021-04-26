import { createStore } from "vuex"
import Constants from './constants'


export default createStore({
    state() {
        return {
            fileSystemNavigator: {
                state: Constants.STATE_EMPTY,
                error: null,
                data: null,
            },
            videoFileInfo: {
                state: Constants.STATE_EMPTY,
                error: null,
                data: null,
            },
            videoPlayer: {
                state: Constants.STATE_EMPTY,
                error: null,
                id: null,
                path: null,
                subtitles: [],
            },
            position: {
                start: null,
                end: null,
            },
            editor: {
                state: Constants.STATE_EMPTY,
                error: null,
                id: null,
                framerate: null,
                frames: null,
            },
            openFilePanel: true,
        }
    },

    mutations: {
        //FileSystemNavigator
        [Constants.MUTATION_FSN_EMPTY] (state) {
            state.fileSystemNavigator = {
                state: Constants.STATE_EMPTY,
                error: null,
                data: null,
            };
        },
        [Constants.MUTATION_FSN_LOADING] (state) {
            state.fileSystemNavigator = {
                state: Constants.STATE_LOADING,
                error: null,
                data: null,
            };
        },
        [Constants.MUTATION_FSN_PRESENT] (state, data) {
            state.fileSystemNavigator = {
                state: Constants.STATE_PRESENT,
                error: null,
                data: data,
            };
        },
        [Constants.MUTATION_FSN_ERROR] (state, message) {
            state.fileSystemNavigator = {
                state: Constants.STATE_ERROR,
                error: message,
                data: null,
            };
        },

        //VideoFileInfo
        [Constants.MUTATION_VFI_EMPTY] (state) {
            state.videoFileInfo = {
                state: Constants.STATE_EMPTY,
                error: null,
                data: null,
            };
        },
        [Constants.MUTATION_VFI_LOADING] (state) {
            state.videoFileInfo = {
                state: Constants.STATE_LOADING,
                error: null,
                data: null,
            };
        },
        [Constants.MUTATION_VFI_PRESENT] (state, data) {
            state.videoFileInfo = {
                state: Constants.STATE_PRESENT,
                error: null,
                data: data,
            };
        },
        [Constants.MUTATION_VFI_ERROR] (state, message) {
            state.videoFileInfo = {
                state: Constants.STATE_ERROR,
                error: message,
                data: null,
            };
        },

        //VideoPlayer
        [Constants.MUTATION_VP_EMPTY] (state) {
            state.videoPlayer = {
                state: Constants.STATE_EMPTY,
                error: null,
                id: null,
                path: null,
                subtitles: [],
            };
            state.position = {
                start: null,
                end: null,
            };
        },
        [Constants.MUTATION_VP_LOADING] (state) {
            state.videoPlayer = {
                state: Constants.STATE_LOADING,
                error: null,
                id: null,
                path: null,
                subtitles: [],
            };
            state.position = {
                start: null,
                end: null,
            };
        },
        [Constants.MUTATION_VP_ERROR] (state, message) {
            state.videoPlayer = {
                state: Constants.STATE_ERROR,
                error: message,
                id: null,
                path: null,
                subtitles: [],
            };
            state.position = {
                start: null,
                end: null,
            };
        },
        [Constants.MUTATION_VP_PRESENT] (state, payload) {
            state.videoPlayer = {
                state: Constants.STATE_PRESENT,
                error: null,
                id: payload.id,
                path: payload.path,
                subtitles: payload.subtitles,
            };
            state.position = {
                start: null,
                end: null,
            };
        },

        //Position
        [Constants.MUTATION_POSITION_START] (state, position) {
            state.position.start = position;
        },
        [Constants.MUTATION_POSITION_END] (state, position) {
            state.position.end = position;
        },

        //Editor
        [Constants.MUTATION_EDITOR_EMPTY] (state) {
            state.editor = {
                state: Constants.STATE_EMPTY,
                error: null,
                id: null,
                framerate: null,
                frames: null,
            };
        },
        [Constants.MUTATION_EDITOR_LOADING] (state) {
            state.editor = {
                state: Constants.STATE_LOADING,
                error: null,
                id: null,
                framerate: null,
                frames: null,
            };
        },
        [Constants.MUTATION_EDITOR_ERROR] (state, message) {
            state.editor = {
                state: Constants.STATE_ERROR,
                error: message,
                id: null,
                framerate: null,
                frames: null,
            };
        },
        [Constants.MUTATION_EDITOR_PRESENT] (state, data) {
            state.editor = {
                state: Constants.STATE_PRESENT,
                error: null,
                id: data.id,
                framerate: parseInt(data.framerate),
                frames: parseInt(data.frames),
            };
        },

        //open file panel
        [Constants.MUTATION_OPEN_FILE_PANEL] (state, value) {
            state.openFilePanel = !!value;
        },
    },

});
