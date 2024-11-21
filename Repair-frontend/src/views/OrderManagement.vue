<script setup>
import { ref, watch, onMounted } from "vue";
import OrderList from "../components/OrderList.vue";
import OrderForm from "../components/OrderForm.vue";
import { fetchWithAuth } from '@/utils/fetchWithAuth';

const isModalVisible = ref(false);
const selectedOrder = ref(null);
const orders = ref([]);
const existingFiles = ref([]);

onMounted(() => {
  fetchOrders();
});

const fetchOrders = async () => {
  const response = await fetchWithAuth(`${import.meta.env.VITE_BASE_API}/orders`);
  if (response.ok) {
    orders.value = await response.json();
  } else {
    alert('Failed to fetch orders.');
  }
};

const fetchFiles = async (orderId) => {
  const response = await fetchWithAuth(`${import.meta.env.VITE_BASE_API}/orders/${orderId}/files`);
  if (response.ok) {
    existingFiles.value = await response.json();
  } else {
    alert('Failed to fetch files.');
  }
};

const deleteOrder = async (orderId) => {
  const response = await fetchWithAuth(`${import.meta.env.VITE_BASE_API}/orders/${orderId}`, {
    method: 'DELETE',
  });

  if (response.ok) {
    alert('Order deleted successfully!');
    fetchOrders();
  } else {
    alert('Failed to delete order.');
  }
};

const showForm = () => {
  selectedOrder.value = { name: "", status: "", description: "" };
  existingFiles.value = [];
  isModalVisible.value = true;
};

const editOrder = (order) => {
  selectedOrder.value = { ...order };
  fetchFiles(selectedOrder.value.id);
  isModalVisible.value = true;
};

const closeModal = () => {
  isModalVisible.value = false;
  fetchOrders();
};

const handleFormSubmit = () => {
  isModalVisible.value = false;
  fetchOrders();
};

const isLoggedIn = () => {
  return localStorage.getItem('accessToken') !== null
}
</script>

<template>
  <div class="p-6 bg-gray-100 min-h-screen">
    <h1 class="text-2xl font-bold mb-6">Repair Order</h1>
    <OrderList :orders="orders" :isLoggedIn="isLoggedIn()" @add-order="showForm" @edit-order="editOrder" @delete-order="deleteOrder" />

    <div
      v-if="isModalVisible"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-lg shadow-lg w-full max-w-lg p-6 relative">
        <button
          class="absolute top-3 right-3 text-gray-500 hover:text-gray-800"
          @click="closeModal"
        >
          ✕
        </button>
        <OrderForm 
      :order="selectedOrder" 
      :existingFiles="existingFiles" 
      @submit-success="handleFormSubmit" 
      @close="closeModal"
    />
      </div>
    </div>
  </div>
</template>
