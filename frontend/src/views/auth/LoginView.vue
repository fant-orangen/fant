<script setup lang="ts">
/**
 * Login View component.
 *
 * This component serves as a container for the LoginForm component,
 * providing the route-level view for the authentication functionality.
 * It isolates the login form UI from the routing layer.
 *
 * Features:
 * - Serves as the route endpoint for user authentication
 * - Encapsulates the LoginForm component which contains the actual login functionality
 * - Maintains separation of concerns between routing and UI components
 *
 * @component LoginView
 * @requires LoginForm
 * @displayName LoginView
 */
import LoginForm from '@/components/auth/LoginForm.vue';
</script>

<template>
<LoginForm />
</template>

