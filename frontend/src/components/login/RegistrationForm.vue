<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const username = ref('')
const email = ref('')
const password = ref('')
const firstName = ref('')
const lastName = ref('')
const birthDate = ref('')


</script>

<template>
  <button type="submit">{{ 'REGISTRATION_BUTTON' }}}</button>
</template>

