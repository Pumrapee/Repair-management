<script setup>
import { ref } from 'vue';

const emit = defineEmits(['files-changed']);

const files = ref([]);

const handleFileChange = (event) => {
  files.value = Array.from(event.target.files);
  emit('files-changed', files.value);
};
</script>
<template>
  <div>
    <input type="file" multiple @change="handleFileChange" />
  </div>
</template>