<script setup>
import Header from "./components/Header.vue";
</script>
<template>
  <div class="min-h-screen flex flex-col">
    <Header />
    <main class="flex-grow container mx-auto p-6 pt-16">
      <router-view />
    </main>
  </div>
</template>

<style>

</style>
