<script setup></script>
<template>
  <header
    class="bg-gray-800 text-white p-4 fixed top-0 left-0 w-full z-10 shadow-md"
  >
    <nav class="container mx-auto flex justify-between items-center">
      <div class="text-xl font-semibold">
        แจ้งซ่อม
      </div>
      <ul class="flex gap-4">
        <li><router-link to="/login" class="hover:underline">Login</router-link></li>
        <li><router-link to="/orders" class="hover:underline">Order</router-link></li>
      </ul>
    </nav>
  </header>
</template>

<style scoped>
</style>
