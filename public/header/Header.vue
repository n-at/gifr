<template>
    <nav class="navbar navbar-themed mb-3">
        <span class="navbar-brand mb-0 h1">GIFR</span>
        <div>
            <i class="fa fa-sun"></i>
            <div class="custom-control custom-switch d-inline-block ml-2">
                <input v-model="light" type="checkbox" class="custom-control-input" id="color-theme-switcher">
                <label class="custom-control-label" for="color-theme-switcher"></label>
            </div>
        </div>
    </nav>
</template>

<script>
    export default {
        data() {
            return {
                light: false,
            };
        },

        watch: {
            light() {
                const body = document.getElementsByTagName('body')[0];
                body.classList = this.light ? 'bootstrap' : 'bootstrap-dark';
                localStorage.gifr_light_theme = this.light ? 1 : 0;
            },
        },

        mounted() {
            if (localStorage.gifr_light_theme !== undefined) {
                this.light = !!parseInt(localStorage.gifr_light_theme);
            }
        },
    };
</script>
