<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import api from '@/services/api/axiosInstance'
import CategoryItemList from '@/components/category/CategoryItemList.vue'

const { t } = useI18n()
const route = useRoute()

// Get the category key from the route parameters.
const categoryKey = ref(route.params.categoryKey as string)

// Reactive list of items in the selected category.
const items = ref([])

async function fetchCategoryItems() {
  try {
    // Assume the backend supports filtering by category.
    const response = await api.get(`/items?category=${categoryKey.value}`)
    items.value = response.data
  } catch (error) {
    console.error('Error fetching items:', error)
  }
}

// Fetch items when the component is mounted.
onMounted(() => {
  fetchCategoryItems()
})
</script>

<template>
  <section class="category-view">
    <h1>{{ t(categoryKey) }}</h1>
    <!-- Delegate the rendering of items to a child component -->
    <CategoryItemList :items="items" />
  </section>
</template>

