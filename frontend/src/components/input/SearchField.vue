<template>
  <div class="search-field">
    <input
      v-model="query"
      @input="onInputChange"
      :placeholder="$t('SEARCH_PLACEHOLDER')"
    type="text"
    />
    <ul v-if="results.length > 0" class="search-results">
      <li v-for="(result, index) in results" :key="index" @click="onResultSelect(result)">
        {{ result.name }}
      </li>
    </ul>
    <p v-if="loading">{{ $t('LOADING') }}</p>
    <p v-if="error" class="error">{{ error }}</p>
  </div>
</template>


<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';

// Initialize the i18n instance using useI18n
const { t } = useI18n();  // Destructure t from useI18n()

// Props for API call and placeholder
defineProps({
  apiCall: {
    type: Function,
    required: true,
  },
});

// Local state for query, results, loading, and error
const query = ref('');
const results = ref<any[]>([]);
const loading = ref(false);
const error = ref('');

// Handle input change and search
const onInputChange = async () => {
  if (query.value.trim() === '') {
    results.value = [];
    return;
  }

  loading.value = true;
  error.value = '';

  try {
    // Call the provided API function with the query
    //const response = await props.apiCall(query.value);
    //results.value = response.data;
  } catch (err) {
    error.value = t('ERROR_SEARCHING');  // Use t to get the translation
    console.error(err);
  } finally {
    loading.value = false;
  }
};

// Handle result selection
const onResultSelect = (result: any) => {
  console.log('Selected result:', result);
};
</script>

<style scoped>
.search-field {
  position: relative;
}

.search-field input {
  width: 100%;
  padding: 8px;
  font-size: 16px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.search-results {
  position: absolute;
  top: 100%;
  width: 100%;
  border: 1px solid #ccc;
  background-color: white;
  max-height: 200px;
  overflow-y: auto;
  margin-top: 5px;
  padding: 0;
  list-style-type: none;
}

.search-results li {
  padding: 8px;
  cursor: pointer;
}

.search-results li:hover {
  background-color: #f1f1f1;
}

.error {
  color: red;
  font-size: 12px;
}
</style>
