<template>
  <div class="form-group">
    <label :for="id">{{ label }}</label>
    <component
      :is="multiline ? 'textarea' : 'input'"
      :id="id"
      :value="modelValue"
      @input="handleInput"
      :required="required"
      :placeholder="placeholder"
      :rows="multiline ? rows : null"
      class="text-input"
    />
  </div>
</template>

<script lang="ts">
export default {
  props: {
    id: {
      type: String,
      required: true,
    },
    label: {
      type: String,
      required: true,
    },
    modelValue: {
      type: String,
      required: true,
    },
    placeholder: {
      type: String,
      default: '',
    },
    required: {
      type: Boolean,
      default: false,
    },
    multiline: {
      type: Boolean,
      default: false,
    },
    rows: {
      type: Number,
      default: 3,
    },
  },
  emits: ['update:modelValue'],
  methods: {
    handleInput(event: Event) {
      const target = event.target as HTMLInputElement | HTMLTextAreaElement;
      this.$emit('update:modelValue', target.value);
    },
  },
};
</script>

<style scoped>
.form-group {
  margin-bottom: 16px;
  position: relative;
  width: 100%;
}

label {
  display: block;
  font-weight: 500;
  margin-bottom: 6px;
  color: #2c3e50;
  font-size: 0.9rem;
}

.text-input {
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  color: #333;
  background-color: #fff;
  transition: all 0.2s ease;
  font-family: inherit;
  overflow: auto;
  resize: vertical;
  min-height: 42px; /* Match the select height */
  height: 42px; /* Fixed height for inputs */
}

input.text-input {
  height: 42px; /* Fixed height specifically for input elements */
}

.text-input:hover {
  border-color: #3498db;
}

.text-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.text-input::placeholder {
  color: #999;
  opacity: 0.7;
}

textarea.text-input {
  min-height: 80px;
  line-height: 1.5;
  height: auto; /* Allow textareas to grow */
}
</style>
