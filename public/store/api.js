import $ from 'jquery'

import Store from './index'
import Constants from './constants'


export default {
    /**
     * Request file list for given path
     *
     * @param path
     */
    loadFileList(path) {
        Store.commit(Constants.Mutation.FileSystemNavigator.Loading);

        $.post('/file-system-navigator/list', {path: path}, response => {
            if (!response) {
                Store.commit(Constants.Mutation.FileSystemNavigator.Error, 'File list request failed');
                return;
            }

            if (!response.success) {
                Store.commit(Constants.Mutation.FileSystemNavigator.Error, response.message);
                return;
            }

            Store.commit(Constants.Mutation.FileSystemNavigator.Present, {
                currentPath: response.currentPath,
                previousPath: response.previousPath,
                entries: response.entries,
            });
        });
    },

    /**
     * Request info about video file
     *
     * @param path
     */
    videoFileInfo(path) {
        Store.commit(Constants.Mutation.VideoFileInfo.Loading);

        $.post('/video-info', {path: path}, response => {
            if (!response) {
                Store.commit(Constants.Mutation.VideoFileInfo.Error, 'Video file info request failed');
                return;
            }

            if (!response.success) {
                Store.commit(Constants.Mutation.VideoFileInfo.Error, response.message);
                return;
            }

            Store.commit(Constants.Mutation.VideoFileInfo.Present, response.info);
        });
    },

    /**
     * Export selected video fragment
     *
     * @param framerate
     * @param size
     */
    exportVideoFragment(framerate, size) {
        Store.commit(Constants.Mutation.Editor.Loading);

        const videoFileId = Store.state.videoPlayer.id;
        const start = Store.state.position.start;
        const end = Store.state.position.end;

        if (!videoFileId) {
            Store.commit(Constants.Mutation.Editor.Error, 'Video file not opened');
            return;
        }
        if (start === null) {
            Store.commit(Constants.Mutation.Editor.Error, 'Video fragment start not defined');
            return;
        }
        if (end === null) {
            Store.commit(Constants.Mutation.Editor.Error, 'Video fragment end not defined');
            return;
        }

        const params = {
            id: videoFileId,
            start,
            end,
            framerate,
            size,
        };

        $.post('/export-frames', params, response => {
            if (!response) {
                Store.commit(Constants.Mutation.Editor.Error, 'Video fragment save failed');
                return;
            }
            if (!response.success) {
                Store.commit(Constants.Mutation.Editor.Error, response.message);
                return;
            }

            Store.commit(Constants.Mutation.Editor.Present, response);
        });
    },
};
