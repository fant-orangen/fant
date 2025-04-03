<!-- IconWithText.vue -->
<template>
  <div class="icon-text">
    <img :src="iconSrc" :alt="text" class="icon" />
    <span>{{ text }}</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  iconSrc: string;
  text: string;
}>();
</script>

<style scoped>
.icon-text {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.icon {
  width: 20px;
  height: 20px;
}
</style>
