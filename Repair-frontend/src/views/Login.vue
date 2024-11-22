<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
      <h2 class="text-2xl font-bold text-gray-800 text-center mb-6">
        {{ isRegistering ? "Register" : "Login" }}
      </h2>
      <form @submit.prevent="isRegistering ? register() : login()" class="space-y-4">
        <!-- Username Input -->
        <div>
          <label for="username" class="block text-sm font-medium text-gray-700 mb-1">
            Username
          </label>
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
          <label for="password" class="block text-sm font-medium text-gray-700 mb-1">
            Password
          </label>
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
            {{ isRegistering ? "Register" : "Login" }}
          </button>
        </div>
      </form>
      <p class="text-center mt-4">
        {{ isRegistering ? "Already have an account?" : "Don't have an account?" }}
        <button
          @click="toggleForm"
          class="text-blue-500 hover:underline focus:outline-none"
        >
          {{ isRegistering ? "Login" : "Register" }}
        </button>
      </p>
    </div>
  </div>
</template>

<script setup>
import router from "@/router";
import { ref } from "vue";

const username = ref("");
const password = ref("");
const isRegistering = ref(false);

const toggleForm = () => {
  isRegistering.value = !isRegistering.value;
};

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
    router.push("/orders");
  } else {
    alert("Invalid credentials!");
  }
};

const register = async () => {
  const response = await fetch(`${import.meta.env.VITE_BASE_API}/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: username.value,
      password: password.value,
    }),
  });
  if (response.ok) {
    alert("Registration successful! Please log in.");
    isRegistering.value = false;
  } else {
    alert("Registration failed!");
  }
};
</script>
