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
    },

});
