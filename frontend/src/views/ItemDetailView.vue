<template>
  <ItemDetail :item="item" />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import ItemDetail from '@/components/item/ItemDetail.vue';
import type { ItemDetailsType } from '@/models/Item';

// Example item data
const item = ref<ItemDetailsType>({
  id: 1,
  title: 'Sample Item',
  description: 'This is a sample item description.',
  price: 100,
  location: 'Sample Location',
  contact: 'nic@ntnu.no',
  imageUrls: [
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSmmKsSJQM7-pvAYClePZmpp6oLWKHCn4rujA&s',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSmmKsSJQM7-pvAYClePZmpp6oLWKHCn4rujA&s',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSmmKsSJQM7-pvAYClePZmpp6oLWKHCn4rujA&s'
  ]
});
</script>
