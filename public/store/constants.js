export default {

    State: {
        Loading: 'loading',
        Present: 'present',
        Error: 'error',
        Empty: 'empty'
    },

    Mutation: {
        OpenFilePanel: {
            Visible: 'openFilePanel',
        },
        FileSystemNavigator: {
            Empty: 'fileSystemNavigatorEmpty',
            Loading: 'fileSystemNavigatorLoading',
            Error: 'fileSystemNavigatorError',
            Present: 'fileSystemNavigatorPresent',
        },
        VideoFileInfo: {
            Empty: 'videoFileInfoEmpty',
            Loading: 'videoFileInfoLoading',
            Error: 'videoFileInfoError',
            Present: 'videoFileInfoPresent',
        },
        VideoPlayer: {
            Empty: 'videoPlayerEmpty',
            Loading: 'videoPlayerLoading',
            Error: 'videoPlayerError',
            Present: 'videoPlayerPresent',
        },
        RecordPosition: {
            Start: 'positionStart',
            End: 'positionEnd',
        },
        Editor: {
            Empty: 'fragmentEditorEmpty',
            Loading: 'fragmentEditorLoading',
            Error: 'fragmentEditorError',
            Present: 'fragmentEditorPresent',
        },
    },
};
