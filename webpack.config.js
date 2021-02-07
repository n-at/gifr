const path = require('path');
const webpack = require('webpack');
const { VueLoaderPlugin } = require('vue-loader');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

module.exports = (env, argv) => {
    let outputPath = path.resolve(__dirname, 'public', 'build');
    if (argv.mode === 'production') {
        outputPath = path.resolve(__dirname, 'src', 'main', 'resources', 'public', 'build');
    }

    return {
        mode: 'development',

        entry: {
            index: './public/index.js',
        },

        output: {
            filename: '[name].js',
            path: outputPath,
        },

        optimization: {

        },

        devtool: 'source-map',

        performance: {
            hints: false,
        },

        resolve: {
            alias: {
                '@': path.resolve(__dirname, 'public'),
            },
        },

        plugins: [
            new webpack.ProgressPlugin(),
            new CleanWebpackPlugin(),
            new VueLoaderPlugin(),
            new MiniCssExtractPlugin(),
        ],

        module: {
            rules: [
                {
                    test: /\.vue$/,
                    use: 'vue-loader',
                }, {
                    test: /\.js$/,
                    exclude: /(node_modules)/,
                    use: [
                        'babel-loader',
                        'eslint-loader',
                    ],
                }, {
                    test: /\.css$/,
                    use: [
                        {
                            loader: MiniCssExtractPlugin.loader,
                            options: {
                                publicPath: '/build/',
                            },
                        },
                        'css-loader',
                    ],
                }, {
                    test: /\.(png|svg|jpg|gif)$/,
                    use: [
                        'file-loader',
                    ],
                }, {
                    test: /\.(woff|woff2|eot|ttf|otf)$/,
                    use: [
                        'file-loader',
                    ],
                },
            ],
        },
    };
};
