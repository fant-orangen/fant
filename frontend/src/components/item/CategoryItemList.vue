<script setup lang="ts">
import { defineProps } from 'vue'


interface Item {
  id: string | number;
  title: string;
  imageUrl: string;
  price: number;

}

const { items } = defineProps<{ items: Item[] }>()
</script>

<template>
  <div class="item-list">
    <!-- Loop through items passed as a prop and display them -->
    <div v-for="item in items" :key="item.id" class="item-card">
      <img :src="item.imageUrl" :alt="item.title" class="item-image" />
      <h2 class="item-title">{{ item.title }}</h2>
      <p class="item-price">{{ item.price }}</p>
    </div>
  </div>
</template>



