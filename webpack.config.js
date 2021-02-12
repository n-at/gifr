const path = require('path');
const webpack = require('webpack');
const { VueLoaderPlugin } = require('vue-loader');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const ESLintWebpackPlugin = require('eslint-webpack-plugin');


module.exports = (env, argv) => {
    let outputPath = path.resolve(__dirname, 'public', 'build');
    let optimization = {};

    if (argv.mode === 'production') {
        outputPath = path.resolve(__dirname, 'src', 'main', 'resources', 'public', 'build');
        optimization = {
            minimize: true,
            minimizer: ['...', new CssMinimizerPlugin()],
        };
    }

    ///////////////////////////////////////////////////////////////////////////

    const vueRule = {
        test: /\.vue$/,
        use: 'vue-loader',
    };
    const jsRule = {
        test: /\.js$/,
        exclude: /(node_modules)/,
        use: 'babel-loader',
    };
    const cssRule = {
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
    };
    const imagesRule = {
        test: /\.(png|svg|jpg|gif)$/,
        use: 'file-loader',
    };
    const fontsRule = {
        test: /\.(woff|woff2|eot|ttf|otf)$/,
        use: 'file-loader',
    };

    ///////////////////////////////////////////////////////////////////////////

    return {
        mode: 'development',

        entry: {
            index: './public/index.js',
        },

        output: {
            filename: '[name].js',
            path: outputPath,
        },

        plugins: [
            new webpack.ProgressPlugin(),
            new CleanWebpackPlugin(),
            new VueLoaderPlugin(),
            new MiniCssExtractPlugin(),
            new ESLintWebpackPlugin(),
        ],

        module: {
            rules: [
                vueRule,
                jsRule,
                cssRule,
                imagesRule,
                fontsRule,
            ],
        },

        devtool: 'source-map',
        optimization: optimization,
        performance: { hints: false },
        resolve: {},
    };
};
