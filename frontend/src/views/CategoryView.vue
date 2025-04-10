<script setup lang="ts">
/**
 * Category View component.
 *
 * This component displays items filtered by a specific category.
 * It fetches and renders items associated with the category key provided via props.
 *
 * Features:
 * - Dynamic fetching of items based on category key parameter
 * - Localized category title display using i18n
 * - Item grid layout for displaying category items
 * - Error handling for API failures
 *
 * @component CategoryView
 * @requires vue
 * @requires vue-i18n
 * @requires @/services/api/axiosInstance
 * @displayName CategoryView
 */
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import api from '@/services/api/axiosInstance'

const props = defineProps<{ categoryKey: string }>()
const { t } = useI18n()
const items = ref([])


/**
 * Fetches items belonging to the current category from the API.
 * Uses the categoryKey prop to filter items by category.
 *
 * @returns {Promise<void>}
 */
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

