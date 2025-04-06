<template>
  <div class="profile-favorites-view">
    <h2>{{ $t('MY_FAVORITES') }}</h2>

    <ItemList
      :fetchFunction="fetchFavoriteItems"
      emptyMessage="You haven't added any items to your favorites yet."
      :pageSize="10"
      :paginationEnabled="true"
    />
  </div>
</template>

<script setup lang="ts">

import { fetchFavoriteItems } from '@/services/ItemService';
import ItemList from '@/components/item/ItemList.vue';
</script>
