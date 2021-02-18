<template>
    <div><strong>Video</strong></div>
    <div class="row mb-3">
        <div class="col-4">Codec:</div>
        <div class="col-8">{{ info.codecDisplay }}</div>

        <div class="col-4">Profile:</div>
        <div class="col-8">{{ info.profile }}</div>

        <div class="col-4">Resolution:</div>
        <div class="col-8">{{ info.width }}x{{ info.height }}</div>

        <div class="col-4">Frame rate:</div>
        <div class="col-8">{{ info.framerate }}</div>

        <div class="col-4">Bitrate:</div>
        <div class="col-8">{{ info.bitrate / 1000 }} kbit/s</div>

        <div class="col-4">Pixel format:</div>
        <div class="col-8">{{ info.pixelFormat }}</div>
    </div>
</template>

<script>
    export default {
        props: ['info'],
    };
</script>
