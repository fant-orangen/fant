<template>
  <div class="profile-favorites-view">
    <h2>{{ $t('MY_FAVORITES') }}</h2>
    <ItemList
      :fetchFunction="fetchItems"
      :categoryId="null"
      :emptyMessage="$t('FAVORITES_EMPTY_MESSAGE')"
      :pageSize="5"
      :paginationEnabled="true"
    />
  </div>
</template>

<script setup lang="ts">
import { fetchPagedFavoriteItems } from '@/services/ItemService'
import type { PaginatedItemPreviewResponse } from '@/models/Item'
import ItemList from '@/components/item/ItemList.vue'

async function fetchItems(page: number, size: number, sort: string = 'createdAt,desc'): Promise<PaginatedItemPreviewResponse> {
  return await fetchPagedFavoriteItems(page, size, sort)
}
</script>
