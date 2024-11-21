<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
      <h2 class="text-2xl font-bold text-gray-800 text-center mb-6">Login</h2>
      <form @submit.prevent="login" class="space-y-4">
        <!-- Username Input -->
        <div>
          <label
            for="username"
            class="block text-sm font-medium text-gray-700 mb-1"
            >Username</label
          >
          <input
            v-model="username"
            id="username"
            required
            type="text"
            class="block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        <!-- Password Input -->
        <div>
          <label
            for="password"
            class="block text-sm font-medium text-gray-700 mb-1"
            >Password</label
          >
          <input
            v-model="password"
            id="password"
            required
            type="password"
            class="block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        <!-- Submit Button -->
        <div>
          <button
            type="submit"
            class="w-full bg-blue-500 text-white font-semibold py-2 px-4 rounded-md shadow hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-400 focus:ring-offset-2"
          >
            Login
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const username = ref("");
const password = ref("");

const login = async () => {
  const response = await fetch(`${import.meta.env.VITE_BASE_API}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: username.value,
      password: password.value,
    }),
  });
  if (response.ok) {
    const data = await response.json();
    localStorage.setItem("accessToken", data.access_token);
    alert("Login successful!");
  } else {
    alert("Invalid credentials!");
  }
};
</script>
