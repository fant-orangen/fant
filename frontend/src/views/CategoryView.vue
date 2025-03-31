<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import api from '@/services/api/axiosInstance'

const props = defineProps<{ categoryKey: string }>()
const { t } = useI18n()
const items = ref([])

async function fetchCategoryItems() {
  try {
    // Use the categoryKey prop to filter items
    const response = await api.get(`/items?category=${props.categoryKey}`)
    items.value = response.data
  } catch (error) {
    console.error('Error fetching category items:', error)
  }
}

onMounted(() => {
  fetchCategoryItems()
})
</script>

<template>
  <section class="category-view">
    <h1>{{ t(props.categoryKey) }}</h1>
    <div class="item-grid">

    </div>
  </section>
</template>

