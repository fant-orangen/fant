<template>
  <div class="item-list-container">
    <div v-if="isLoading && currentPage === 1" class="loading-indicator">
      <p>Loading items...</p>
    </div>

    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
    </div>

    <div v-else>
      <transition-group
        name="fade-resize"
        tag="div"
        :class="['items-container', { 'small-thumbnails': smallThumbnails }]"
      >
        <ItemPreview
          v-for="item in items"
          :key="item.id"
          :item="item"
        />
      </transition-group>

      <div v-if="items?.length === 0" class="no-items-message">
        <p>{{ effectiveEmptyMessage }}</p>
      </div>

      <div v-if="!paginationEnabled && isLoading && currentPage > 1" class="loading-indicator">
        <p>Loading more items...</p>
      </div>

      <div v-if="paginationEnabled && totalPages > 1" class="pagination-controls">
        <button
          :disabled="currentPage <= 1"
          @click="changePage(currentPage - 1)"
          class="pagination-button"
        >
          Previous
        </button>
        <span class="page-info">Page {{ currentPage }} of {{ totalPages }}</span>
        <button
          :disabled="currentPage >= totalPages"
          @click="changePage(currentPage + 1)"
          class="pagination-button"
        >
          Next
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  withDefaults,
  defineProps,
  defineEmits,
  computed,
  onMounted,
  onUnmounted,
  watch,
} from 'vue';
import ItemPreview from '@/components/item/ItemPreview.vue';
import type { ItemPreviewType, PaginatedItemPreviewResponse } from '@/models/Item';

interface Props {
  items?: ItemPreviewType[] | null;
  isLoading?: boolean;
  error?: string | null;
  currentPage?: number;
  totalPages?: number;
  emptyMessage?: string;
  paginationEnabled?: boolean;
  smallThumbnails?: boolean;
  fetchFunction?: (page: number, size: number, sort?: string) => Promise<PaginatedItemPreviewResponse>;
}

const props = withDefaults(defineProps<Props>(), {
  items: null,
  isLoading: false,
  error: null,
  currentPage: 1,
  totalPages: 1,
  emptyMessage: 'No items found.',
  paginationEnabled: true,
  smallThumbnails: false,
  fetchFunction: undefined
});

const emit = defineEmits<{
  (e: 'change-page', page: number): void;
}>();

const effectiveEmptyMessage = computed(() => props.emptyMessage || 'No items found.');

function changePage(page: number) {
  if (page >= 1 && page <= props.totalPages!) {
    emit('change-page', page);
  }
}

function onScroll() {
  if (props.paginationEnabled) return;

  const scrollThreshold = 300;
  const scrollPosition = window.innerHeight + window.scrollY;
  const bottom = document.documentElement.offsetHeight;

  if (
    scrollPosition >= bottom - scrollThreshold &&
    !props.isLoading &&
    props.currentPage! < props.totalPages!
  ) {
    emit('change-page', props.currentPage! + 1);
  }
}

onMounted(() => {
  if (!props.paginationEnabled) {
    window.addEventListener('scroll', onScroll);
  }
});

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll);
});

watch(() => props.paginationEnabled, (newVal) => {
  if (!newVal) {
    window.addEventListener('scroll', onScroll);
  } else {
    window.removeEventListener('scroll', onScroll);
  }
});
</script>

<style scoped>
.item-list-container {
  width: 100%;
}

.loading-indicator,
.error-message,
.no-items-message {
  text-align: center;
  margin: 2rem 0;
  color: var(--color-text);
}

.items-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  margin-top: 1.5rem;
  border-top: 2px solid lightgray;
  transition: all 0.3s ease;
}
.items-container.small-thumbnails {
  grid-template-columns: repeat(6, 1fr);
  gap: 0.75rem;
}

@media (max-width: 1200px) {
  .items-container { grid-template-columns: repeat(3, 1fr); }
  .items-container.small-thumbnails { grid-template-columns: repeat(6, 1fr); }
}
@media (max-width: 900px) {
  .items-container { grid-template-columns: repeat(2, 1fr); }
  .items-container.small-thumbnails { grid-template-columns: repeat(4, 1fr); }
}
@media (max-width: 600px) {
  .items-container { grid-template-columns: repeat(2, 1fr); gap: 1rem; }
  .items-container.small-thumbnails { grid-template-columns: repeat(3, 1fr); gap: 0.5rem; }
}

/* Animation for smoother transitions */
.fade-resize-enter-active,
.fade-resize-leave-active {
  transition: all 0.25s ease;
}
.fade-resize-enter-from,
.fade-resize-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
  padding-bottom: 1rem;
  gap: 1rem;
}

.pagination-button {
  padding: 0.5rem 1rem;
  background-color: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}
.pagination-button:hover:not(:disabled) {
  background-color: var(--color-background-mute);
}
.pagination-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.page-info {
  font-size: 0.9rem;
  color: var(--color-text);
}
</style>
