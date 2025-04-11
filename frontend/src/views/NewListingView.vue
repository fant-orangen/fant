<template>
  <EditAddItem
    :onSubmit="createItem"
    @success="onSuccess"
  />
</template>

<script lang="ts">

/**
 * New Listing View component.
 *
 * This component provides the interface for users to create new item listings.
 * It wraps the EditAddItem component and handles the creation logic and post-submission navigation.
 *
 * @component
 */
import EditAddItem from "@/components/item/EditAddItem.vue";
import { createItem } from "@/services/ItemService.ts";
import { useRouter } from 'vue-router';

export default {
  components: {
    EditAddItem,
  },
  setup() {
    const router = useRouter();

    /**
     * Handles successful item creation by navigating to the specified path.
     *
     * @param {string} redirectPath - The path to redirect to after successful item creation (defaults to home route)
     * @returns {void}
     */
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
