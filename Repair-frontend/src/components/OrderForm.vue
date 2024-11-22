<script setup>
import { ref, watch, computed } from 'vue';
import FileUpload from './FileUpload.vue';
import { fetchWithAuth } from '@/utils/fetchWithAuth';

const props = defineProps({
  order: { type: Object, required: true },
  existingFiles: { type: Array, required: true },
});

const emit = defineEmits(['submit-success', 'close']);

const localOrder = ref({ ...props.order });
const filesToUpload = ref([]);
const localFiles = ref([...props.existingFiles]);
const filesToDelete = ref([]);

watch(
  () => props.existingFiles,
  (newFiles) => {
    localFiles.value = [...newFiles];
  }
);

const handleFiles = (newFiles) => {
  filesToUpload.value = [...filesToUpload.value, ...newFiles];
};

const hasChanges = computed(() => {
  const isOrderChanged =
    JSON.stringify(localOrder.value) !== JSON.stringify(props.order);

  const hasFileChanges =
    filesToUpload.value.length > 0 || filesToDelete.value.length > 0;

  return isOrderChanged || hasFileChanges;
});

const submitForm = async () => {
  for (const fileId of filesToDelete.value) {
    const deleteResponse = await fetchWithAuth(
      `${import.meta.env.VITE_BASE_API}/orders/${localOrder.value.id}/files/${fileId}`,
      { method: 'DELETE' }
    );
    if (!deleteResponse.ok) {
      alert(`Failed to delete file with ID ${fileId}.`);
      return;
    }
  }

  const method = localOrder.value.id ? 'PUT' : 'POST';
  const endpoint = localOrder.value.id ? `/orders/${localOrder.value.id}` : `/orders`;

  const response = await fetchWithAuth(`${import.meta.env.VITE_BASE_API}${endpoint}`, {
    method,
    body: JSON.stringify(localOrder.value),
  });

  if (response.ok) {
    const order = await response.json();

    if (filesToUpload.value.length > 0) {
      const formData = new FormData();
      filesToUpload.value.forEach((file) => {
        formData.append('files', file);
      });

      const uploadResponse = await fetchWithAuth(
        `${import.meta.env.VITE_BASE_API}/orders/${order.id}/files/upload`,
        {
          method: 'POST',
          body: formData,
        }
      );

      if (!uploadResponse.ok) {
        alert('Failed to upload files.');
        return;
      }
    }

    alert('Order saved successfully!');
    emit('submit-success');
  } else {
    alert('Failed to save order.');
  }
};

const markFileForDeletion = (fileId) => {
  filesToDelete.value.push(fileId);
  localFiles.value = localFiles.value.filter((file) => file.id !== fileId);
};

const downloadFile = async (orderId, fileId) => {
  const response = await fetchWithAuth(
    `${import.meta.env.VITE_BASE_API}/orders/${orderId}/files/${fileId}/download`
  );
  if (response.ok) {
    const blob = await response.blob();
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `file-${fileId}`;
    link.click();
  } else {
    alert('Failed to download file.');
  }
};
</script>
<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-lg w-full max-w-4xl p-10 relative">
      <button
        class="absolute top-5 right-5 text-gray-500 hover:text-gray-800 text-2xl"
        @click="$emit('close')"
      >
        ✕
      </button>
      <form @submit.prevent="submitForm">
        <h2 class="text-2xl font-bold mb-6">{{ localOrder.id ? 'Edit Order' : 'Add Order' }}</h2>
        
        <div class="mb-6">
          <label for="name" class="block text-lg font-medium text-gray-700">Order Name</label>
          <input
            v-model="localOrder.name"
            id="name"
            required
            class="mt-2 block w-full px-4 py-3 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        <div class="mb-6">
          <label for="status" class="block text-lg font-medium text-gray-700">Status</label>
          <select
            v-model="localOrder.status"
            id="status"
            required
            class="mt-2 block w-full px-4 py-3 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="TODO">TODO</option>
            <option value="DONE">DONE</option>
          </select>
        </div>

        <div class="mb-6">
          <label for="description" class="block text-lg font-medium text-gray-700">Description</label>
          <textarea
            v-model="localOrder.description"
            id="description"
            class="mt-2 block w-full px-4 py-3 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            rows="4"
          ></textarea>
        </div>

        <div class="mb-6">
          <label class="block text-lg font-medium text-gray-700">Existing Files</label>
          <ul>
            <li
              v-for="file in localFiles"
              :key="file.id"
              class="flex justify-between items-center text-lg"
            >
              <span>{{ file.fileName }}</span>
              <div>
                <button
                  type="button"
                  @click="downloadFile(localOrder.id, file.id)"
                  class="text-blue-500 hover:text-blue-700 ml-2"
                >
                  Download
                </button>
                <button
                  type="button"
                  @click="markFileForDeletion(file.id)"
                  class="text-red-500 hover:text-red-700 ml-2"
                >
                  Delete
                </button>
              </div>
            </li>
          </ul>
        </div>

        <div class="mb-6">
          <label class="block text-lg font-medium text-gray-700">Files to Upload</label>
          <ul>
            <li
              v-for="(file, index) in filesToUpload"
              :key="index"
              class="flex justify-between items-center text-lg"
            >
              <span>{{ file.name }}</span>
              <button
                type="button"
                @click="filesToUpload.splice(index, 1)"
                class="text-red-500 hover:text-red-700"
              >
                Remove
              </button>
            </li>
          </ul>
        </div>

        <FileUpload @files-changed="handleFiles" />

        <div class="mt-8 flex justify-end">
          <button
            type="button"
            @click="$emit('close')"
            class="bg-gray-300 text-gray-700 px-6 py-3 rounded-md shadow hover:bg-gray-400 mr-4 text-lg"
          >
            Cancel
          </button>
          <button
            type="submit"
            :disabled="!hasChanges"
            :class="hasChanges ? 'bg-blue-500 hover:bg-blue-600' : 'bg-gray-300 cursor-not-allowed'"
            class="text-white px-6 py-3 rounded-md shadow text-lg"
          >
            Submit
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
