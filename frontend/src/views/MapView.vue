<template>
  <div class="map-view">
    <div class="map-container">
      <div class="category-sidebar">
        <h3 class="sidebar-title">{{ $t('CATEGORIES') }}</h3>
        <div class="scrollable-categories">
          <CategoryGrid
            @select="onCategoryClick"
            layout="vertical"
            :selectedCategoryId="selectedCategoryId"
          />
        </div>
      </div>
      <div class="map-wrapper">
        <MapComponent :categoryId="selectedCategoryId" />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import MapComponent from '@/components/map/MapComponent.vue'
import CategoryGrid from '@/components/category/categoryGrid.vue'
import { ref } from 'vue'

const selectedCategoryId = ref<string | null>(null)

function onCategoryClick(categoryId: string) {
  selectedCategoryId.value = categoryId === selectedCategoryId.value ? null : categoryId
}
</script>

<style scoped>
.map-view {
  max-width: 1800px;
  margin: 0 auto;
  padding: 1rem;
}

.map-description {
  margin-bottom: 1rem;
}

.map-container {
  display: flex;
  gap: 1rem;
  height: 770px;
}

.map-wrapper {
  flex: 1;
  position: relative;
}

.category-sidebar {
  width: 250px;
  flex-shrink: 0;
  background-color: #f5f5f5;
  border-radius: 8px;
  border: 1px solid #ddd;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.sidebar-title {
  padding: 1rem 1rem 0.5rem 1rem;
  margin: 0;
  font-size: 1.2rem;
}

.scrollable-categories {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 0 1rem 1rem 1rem;
}

.scrollable-categories::-webkit-scrollbar {
  width: 6px;
}

.scrollable-categories::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.scrollable-categories::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.scrollable-categories::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

@media (max-width: 768px) {
  .map-container {
    flex-direction: column;
    height: auto;
  }

  .category-sidebar {
    width: 100%;
    height: 300px;
  }

  .map-wrapper {
    height: 500px;
  }
}
</style>
