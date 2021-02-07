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
     * Open video file and prepare to play
     *
     * @param path
     */
    openVideoFile(path) {
        Store.commit(Constants.MUTATION_VP_LOADING);

        $.post('/video/open', {path: path}, response => {
            if (!response) {
                Store.commit(Constants.MUTATION_VP_ERROR, 'Failed to open video file');
                return;
            }

            if (!response.success) {
                Store.commit(Constants.MUTATION_VP_ERROR, response.message);
                return;
            }

            Store.commit(Constants.MUTATION_VP_PRESENT, response.url);
        });
    },
};
