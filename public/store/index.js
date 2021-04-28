import { createStore } from "vuex"
import Constants from './constants'


export default createStore({
    state() {
        return {
            fileSystemNavigator: {
                state: Constants.State.Empty,
                error: null,
                data: null,
            },
            videoFileInfo: {
                state: Constants.State.Empty,
                error: null,
                data: null,
            },
            videoPlayer: {
                state: Constants.State.Empty,
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
                state: Constants.State.Empty,
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
        [Constants.Mutation.FileSystemNavigator.Empty] (state) {
            state.fileSystemNavigator = {
                state: Constants.State.Empty,
                error: null,
                data: null,
            };
        },
        [Constants.Mutation.FileSystemNavigator.Loading] (state) {
            state.fileSystemNavigator = {
                state: Constants.State.Loading,
                error: null,
                data: null,
            };
        },
        [Constants.Mutation.FileSystemNavigator.Present] (state, data) {
            state.fileSystemNavigator = {
                state: Constants.State.Present,
                error: null,
                data: data,
            };
        },
        [Constants.Mutation.FileSystemNavigator.Error] (state, message) {
            state.fileSystemNavigator = {
                state: Constants.State.Error,
                error: message,
                data: null,
            };
        },

        //VideoFileInfo
        [Constants.Mutation.VideoFileInfo.Empty] (state) {
            state.videoFileInfo = {
                state: Constants.State.Empty,
                error: null,
                data: null,
            };
        },
        [Constants.Mutation.VideoFileInfo.Loading] (state) {
            state.videoFileInfo = {
                state: Constants.State.Loading,
                error: null,
                data: null,
            };
        },
        [Constants.Mutation.VideoFileInfo.Present] (state, data) {
            state.videoFileInfo = {
                state: Constants.State.Present,
                error: null,
                data: data,
            };
        },
        [Constants.Mutation.VideoFileInfo.Error] (state, message) {
            state.videoFileInfo = {
                state: Constants.State.Error,
                error: message,
                data: null,
            };
        },

        //VideoPlayer
        [Constants.Mutation.VideoPlayer.Empty] (state) {
            state.videoPlayer = {
                state: Constants.State.Empty,
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
        [Constants.Mutation.VideoPlayer.Loading] (state) {
            state.videoPlayer = {
                state: Constants.State.Loading,
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
        [Constants.Mutation.VideoPlayer.Present] (state, payload) {
            state.videoPlayer = {
                state: Constants.State.Present,
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
        [Constants.Mutation.VideoPlayer.Error] (state, message) {
            state.videoPlayer = {
                state: Constants.State.Error,
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

        //Position
        [Constants.Mutation.RecordPosition.Start] (state, position) {
            state.position.start = position;
        },
        [Constants.Mutation.RecordPosition.End] (state, position) {
            state.position.end = position;
        },

        //Editor
        [Constants.Mutation.Editor.Empty] (state) {
            state.editor = {
                state: Constants.State.Empty,
                error: null,
                id: null,
                framerate: null,
                frames: null,
            };
        },
        [Constants.Mutation.Editor.Loading] (state) {
            state.editor = {
                state: Constants.State.Loading,
                error: null,
                id: null,
                framerate: null,
                frames: null,
            };
        },
        [Constants.Mutation.Editor.Present] (state, data) {
            state.editor = {
                state: Constants.State.Present,
                error: null,
                id: data.id,
                framerate: parseInt(data.framerate),
                frames: parseInt(data.frames),
            };
        },
        [Constants.Mutation.Editor.Error] (state, message) {
            state.editor = {
                state: Constants.State.Error,
                error: message,
                id: null,
                framerate: null,
                frames: null,
            };
        },

        //open file panel
        [Constants.Mutation.OpenFilePanel.Visible] (state, value) {
            state.openFilePanel = !!value;
        },
    },

});
