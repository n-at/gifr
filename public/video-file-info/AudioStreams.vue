<template>
    <div><strong>Audio</strong></div>
    <div v-for="audioInfo in info" class="row mb-3">
        <div class="col-4">Name:</div>
        <div class="col-8">{{ audioInfo.language }}: {{ audioInfo.title }}</div>

        <div class="col-4">Codec:</div>
        <div class="col-8">{{ audioInfo.codecDisplay }}</div>

        <div class="col-4">Channels:</div>
        <div class="col-8">{{ audioInfo.channels }}</div>

        <div class="col-4">Sample rate:</div>
        <div class="col-8">{{ audioInfo.sampleRate }} Hz</div>

        <div class="col-4">Bitrate:</div>
        <div class="col-8">{{ audioInfo.bitrate / 1000 }} kbit/s</div>
    </div>
</template>

<script>
    export default {
        props: ['info'],
    };
</script>
