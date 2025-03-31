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
.text-input {
  width: 100%;
  box-sizing: border-box;
  padding: 10px;
  margin: 10px 0;
}
</style>
