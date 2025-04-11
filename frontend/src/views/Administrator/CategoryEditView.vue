<template>
  <div class="category-edit-view">
    <CategoryEdit />
  </div>
</template>

<script setup lang="ts">
/**
 * Category Edit View component.
 *
 * This component serves as a wrapper/container for the CategoryEdit component,
 * providing the route-level view for the category editing functionality in the admin section.
 * It isolates the category editing UI from the routing layer and applies consistent styling.
 *
 * Features:
 * - Serves as the route endpoint for category editing in the admin section
 * - Provides consistent padding and layout for the contained editor component
 * - Separates routing concerns from the actual editing functionality
 *
 * @component CategoryEditView
 * @requires CategoryEdit
 * @displayName CategoryEditView
 */
import CategoryEdit from '@/components/administrator/CategoryEdit.vue';
</script>

<style scoped>
.category-edit-view {
  padding: 20px;
}
</style>
