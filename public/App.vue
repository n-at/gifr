<template>
  <div class="alert alert-info">
    <Second></Second>
    {{ message }}
  </div>
</template>

<script>
  import Second from './Second.vue';

  export default {
    data() {
      return {
        message: 'hello, world!!',
      }
    },
    components: {
      Second,
    }
  };
</script>

<style>

</style>
