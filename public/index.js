import "core-js/stable";
import "regenerator-runtime/runtime";

import 'popper.js'
import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.css'
import '@fortawesome/fontawesome-free/css/all.css'
import 'video.js/dist/video-js.css'
import { createApp } from 'vue'

import store from './store'
import App from './App.vue'

const app = createApp(App);
app.use(store);
app.mount('#app');
