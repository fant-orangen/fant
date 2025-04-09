<template>
  <EditAddItem
    :onSubmit="createItem"
    @success="onSuccess"
  />
</template>

<script lang="ts">
import EditAddItem from "@/components/item/EditAddItem.vue";
import { createItem } from "@/services/ItemService.ts";
import { useRouter } from 'vue-router';

export default {
  components: {
    EditAddItem,
  },
  setup() {
    const router = useRouter();

    function onSuccess(redirectPath = '/') {
      router.push(redirectPath);
    }

    return {
      createItem,
      onSuccess
    };
  }
};
</script>
<style>
@import '@/assets/styles/responsiveStyles.css'; /* This will still be global */
</style>
