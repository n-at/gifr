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
        Store.commit(Constants.MUTATION_FSN_LOADING);

        $.post('/file-system-navigator/list', {path: path}, response => {
            if (!response) {
                Store.commit(Constants.MUTATION_FSN_ERROR, 'File list request failed');
                return;
            }

            if (!response.success) {
                Store.commit(Constants.MUTATION_FSN_ERROR, response.message);
                return;
            }

            Store.commit(Constants.MUTATION_FSN_PRESENT, {
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
        Store.commit(Constants.MUTATION_VFI_LOADING);

        $.post('/video-info', {path: path}, response => {
            if (!response) {
                Store.commit(Constants.MUTATION_VFI_ERROR, 'Video file info request failed');
                return;
            }

            if (!response.success) {
                Store.commit(Constants.MUTATION_VFI_ERROR, response.message);
                return;
            }

            Store.commit(Constants.MUTATION_VFI_PRESENT, response.info);
        });
    },

    /**
     * Export selected video fragment
     *
     * @param framerate
     * @param size
     */
    exportVideoFragment(framerate, size) {
        Store.commit(Constants.MUTATION_EDITOR_LOADING);

        const videoFileId = Store.state.videoPlayer.id;
        const start = Store.state.position.start;
        const end = Store.state.position.end;

        if (!videoFileId) {
            Store.commit(Constants.MUTATION_EDITOR_ERROR, 'Video file not opened');
            return;
        }
        if (start === null) {
            Store.commit(Constants.MUTATION_EDITOR_ERROR, 'Video fragment start not defined');
            return;
        }
        if (end === null) {
            Store.commit(Constants.MUTATION_EDITOR_ERROR, 'Video fragment end not defined');
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
                Store.commit(Constants.MUTATION_EDITOR_ERROR, 'Video fragment save failed');
                return;
            }
            if (!response.success) {
                Store.commit(Constants.MUTATION_EDITOR_ERROR, response.message);
                return;
            }

            Store.commit(Constants.MUTATION_EDITOR_PRESENT, response);
        });
    },
};
