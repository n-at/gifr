<template>
    <div><strong>Subtitles</strong></div>
    <div v-for="subtitlesInfo in info" class="row mb-3">
        <div class="col-4">Name:</div>
        <div class="col-8">{{ subtitlesInfo.language }}: {{ subtitlesInfo.title }}</div>

        <div class="col-4">Format:</div>
        <div class="col-8">{{ subtitlesInfo.codecDisplay }}</div>
    </div>
</template>

<script>
    export default {
        props: ['info'],
    };
</script>
