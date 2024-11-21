<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
  orders: {
    type: Array,
    required: true,
  },
  isLoggedIn: {
    type: Boolean,
    required: true,
  },
});

const emit = defineEmits(['edit-order', 'delete-order', 'add-order']);

const searchQuery = ref('');

const filteredOrders = computed(() => {
  if (!searchQuery.value) {
    return props.orders;
  }
  return props.orders.filter(order =>
    order.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    order.description.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});
</script>
<template>
  <div class="p-6 bg-gray-100 min-h-screen">
    <!-- Add Order Button -->
    <button
      @click="$emit('add-order')"
      :disabled="!isLoggedIn"
      :class="!isLoggedIn ? 'bg-gray-300 cursor-not-allowed' : 'bg-green-500 hover:bg-green-600'"
      class="text-white px-4 py-2 rounded-md shadow mb-4"
    >
      Add Order
    </button>

    <!-- Search Box -->
    <div class="mb-4">
      <label for="search" class="block text-sm font-medium text-gray-700">Search Orders</label>
      <input
        v-model="searchQuery"
        id="search"
        type="text"
        placeholder="Search by name or description"
        class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
      />
    </div>

    <!-- Orders Table -->
    <div class="overflow-x-auto">
      <table class="min-w-full table-auto border-collapse border border-gray-300 rounded-lg">
        <thead>
          <tr class="bg-gray-100">
            <th class="px-4 py-2 text-left border border-gray-300">Order Name</th>
            <th class="px-4 py-2 text-left border border-gray-300">Status</th>
            <th class="px-4 py-2 text-left border border-gray-300">Description</th>
            <th class="px-4 py-2 text-left border border-gray-300">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="order in filteredOrders"
            :key="order.id"
            class="bg-white hover:bg-gray-50"
          >
            <td class="px-4 py-2 border border-gray-300">{{ order.name }}</td>
            <td
              class="px-4 py-2 border border-gray-300 font-bold text-center"
              :class="{
                'bg-green-200 text-green-800': order.status === 'DONE',
                'bg-yellow-200 text-yellow-800': order.status !== 'DONE',
              }"
            >
              {{ order.status }}
            </td>
            <td class="px-4 py-2 border border-gray-300">{{ order.description }}</td>
            <td class="px-4 py-2 border border-gray-300">
              <!-- Edit Button -->
              <button
                @click="$emit('edit-order', order)"
                :disabled="!isLoggedIn"
                :class="!isLoggedIn ? 'bg-gray-300 cursor-not-allowed' : 'bg-blue-500 hover:bg-blue-600'"
                class="text-white px-4 py-2 rounded-md shadow mr-2"
              >
                Edit
              </button>
              <!-- Delete Button -->
              <button
                @click="$emit('delete-order', order.id)"
                :disabled="!isLoggedIn"
                :class="!isLoggedIn ? 'bg-gray-300 cursor-not-allowed' : 'bg-red-500 hover:bg-red-600'"
                class="text-white px-4 py-2 rounded-md shadow"
              >
                Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
