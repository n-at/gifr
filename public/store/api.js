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
};
