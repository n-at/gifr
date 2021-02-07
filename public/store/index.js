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
                url: null,
            },
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
                url: null,
            };
        },
        [Constants.MUTATION_VP_LOADING] (state) {
            state.videoPlayer = {
                state: Constants.STATE_LOADING,
                error: null,
                url: null,
            };
        },
        [Constants.MUTATION_VP_ERROR] (state, message) {
            state.videoPlayer = {
                state: Constants.STATE_ERROR,
                error: message,
                url: null,
            };
        },
        [Constants.MUTATION_VP_PRESENT] (state, url) {
            state.videoPlayer = {
                state: Constants.STATE_PRESENT,
                error: null,
                url: url,
            };
        },
    },

});
