<script setup lang="ts">
/**
 * Registration View component.
 *
 * This component serves as a container for the RegistrationForm component,
 * providing the route-level view for the user registration functionality.
 * It isolates the registration form UI from the routing layer.
 *
 * Features:
 * - Serves as the route endpoint for new user registration
 * - Encapsulates the RegistrationForm component which contains the actual registration functionality
 * - Maintains separation of concerns between routing and UI components
 *
 * @component RegistrationView
 * @requires RegistrationForm
 * @displayName RegistrationView
 */
import RegistrationForm from "@/components/auth/RegistrationForm.vue";
</script>

<template>
  <main class="registration-view">
    <RegistrationForm />
  </main>
</template>
