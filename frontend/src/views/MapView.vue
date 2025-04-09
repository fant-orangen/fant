<template>
  <div class="map-view">
    <div class="category-sidebar-desktop">
      <h3 class="sidebar-title">{{ t('CATEGORIES') }}</h3>
      <div class="scrollable-categories">
        <CategoryGrid
          @select="onCategoryClick"
          layout="vertical"
          :selectedCategoryId="selectedCategoryId"
        />
      </div>
    </div>

    <div class="map-wrapper">
      <MapComponent
        :categoryId="selectedCategoryId"
        height="100%"
      />
    </div>

    <div class="category-bar-mobile">
      <CategoryGrid
        @select="onCategoryClick"
        layout="horizontal"
        :selectedCategoryId="selectedCategoryId"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import MapComponent from '@/components/map/MapComponent.vue'
import CategoryGrid from '@/components/category/categoryGrid.vue'
import { ref } from 'vue'
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const selectedCategoryId = ref<string | null>(null);

function onCategoryClick(categoryId: string) {
  selectedCategoryId.value = categoryId === selectedCategoryId.value ? null : categoryId;
}
</script>

<style scoped>
/* --- Main Layout --- */
.map-view {
  display: flex;
  position: relative;
  height: calc(100vh - 100px); /* Example: Full viewport height minus header height */
  max-width: 1800px;
  margin: 0 auto;
  padding: 1rem;
  gap: 1rem;
  box-sizing: border-box;
}

/* --- Desktop Sidebar Styles --- */
.category-sidebar-desktop {
  width: 250px;
  flex-shrink: 0;
  background-color: var(--color-background-soft, #f9f9f9);
  border-radius: var(--border-radius, 8px);
  border: 1px solid var(--color-border, #e0e0e0);
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.sidebar-title {
  padding: 1rem 1rem 0.5rem 1rem;
  margin: 0;
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--color-heading, #333);
  border-bottom: 1px solid var(--color-border, #e0e0e0);
  flex-shrink: 0;
}

.scrollable-categories {
  flex-grow: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 1rem;
}
/* Optional: Custom Scrollbar styles */
.scrollable-categories::-webkit-scrollbar {
  width: 6px;
}
.scrollable-categories::-webkit-scrollbar-track {
  background: transparent;
}
.scrollable-categories::-webkit-scrollbar-thumb {
  background-color: var(--color-border-hover, #ccc);
  border-radius: 3px;
}
.scrollable-categories::-webkit-scrollbar-thumb:hover {
  background-color: var(--color-text, #555);
}

/* --- Map Wrapper Styles --- */
.map-wrapper {
  flex: 1;
  position: relative;
  border-radius: var(--border-radius, 8px);
  overflow: hidden;
  height: 100%;
  border: 1px solid var(--color-border, #e0e0e0);
}

/* --- Mobile Bottom Bar Styles --- */
.category-bar-mobile {
  display: none;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 85px;
  background-color: var(--color-background-soft, #f9f9f9);
  border-top: 1px solid var(--color-border, #e0e0e0);
  box-shadow: 0 -2px 5px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
  overflow-y: hidden;
  white-space: nowrap;
  padding: 0.5rem 1rem;
  z-index: 1000;
  box-sizing: border-box;
}
/* Mobile scrollbar styles */
.category-bar-mobile::-webkit-scrollbar {
  height: 4px;
}
.category-bar-mobile::-webkit-scrollbar-track {
  background: transparent;
}
.category-bar-mobile::-webkit-scrollbar-thumb {
  background: var(--color-border-hover, #ccc);
  border-radius: 2px;
}

.category-bar-mobile > :deep(.category-row) {
  display: flex;
  flex-wrap: nowrap;
  gap: 0.75rem;
  align-items: center;
  height: 100%;
}

.category-bar-mobile :deep(.category-wrapper) {
  min-width: auto;
  height: 100%;
}

.category-bar-mobile :deep(.category-button),
.category-bar-mobile :deep(.all-categories-button) {
  padding: 0.4rem 0.6rem;
  flex-shrink: 0;
  height: 100%;
}

.category-bar-mobile :deep(.category-icon),
.category-bar-mobile :deep(.all-icon) {
  width: 28px; height: 28px; margin-bottom: 0.2rem;
}
.category-bar-mobile :deep(.category-icon svg),
.category-bar-mobile :deep(.all-icon svg) {
  width: 20px; height: 20px;
}
.category-bar-mobile :deep(.category-label) {
  font-size: 0.75rem;
}

/* --- Media Query for Mobile --- */
@media (max-width: 768px) {
  .map-view {
    flex-direction: column;
    gap: 0;
    padding: 0;
    padding-bottom: 85px; /* Match mobile bar height */
    height: 100vh;
  }

  .category-sidebar-desktop {
    display: none;
  }

  .category-bar-mobile {
    display: block;
  }

  .map-wrapper {
    width: 100%;
    height: 100%;
    border-radius: 0;
    border: none;
  }
  .map-wrapper > :deep(.map-container) {
    height: 100% !important;
    width: 100% !important;
  }
}

/* Add default values for CSS Variables if they might not be defined globally */
:root {
  --color-background-soft: #f9f9f9;
  --color-border: #e0e0e0;
  --color-border-hover: #cccccc;
  --color-heading: #333333;
  --color-text: #555555;
  --border-radius: 8px;
}
</style>
