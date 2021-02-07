<template>
    <div class="alert alert-danger">
        <i class="fa fa-exclamation-triangle"></i> {{ message }}
    </div>
</template>

<script>
    export default {
        props: ['message'],
    };
</script>
