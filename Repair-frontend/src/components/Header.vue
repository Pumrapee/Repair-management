<script setup></script>
<template>
  <header class="bg-gray-800 text-white p-4">
    <nav class="container mx-auto flex justify-between">
      <ul class="flex gap-4">
        <li><router-link to="/" class="hover:underline">Login</router-link></li>
        <li>
          <router-link to="/orders" class="hover:underline"
            >Order</router-link
          >
        </li>
      </ul>
    </nav>
  </header>
</template>

<style scoped></style>
