<template>
  <div class="form-group">
    <label :for="id">{{ label }}</label>
    <component
      :is="multiline ? 'textarea' : 'input'"
      :id="id"
      :value="inputValue"
      @input="handleInput"
      :required="required"
      :placeholder="placeholder"
      :rows="multiline ? rows : null"
      :class="['text-input']"
      :type="!multiline ? type : undefined"
      :inputmode="type === 'number' ? 'numeric' : undefined"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, watch } from 'vue'
import '@/assets/styles/input/input.css'

export default defineComponent({
  props: {
    id: { type: String, required: true },
    label: { type: String, required: true },
    modelValue: { type: String, required: true },
    placeholder: { type: String, default: '' },
    required: { type: Boolean, default: false },
    multiline: { type: Boolean, default: false },
    rows: { type: Number, default: 3 },
    type: { type: String, default: 'text' }, // NEW: input type
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const inputValue = ref(props.modelValue)

    // Sync prop updates into local input value
    watch(() => props.modelValue, val => {
      inputValue.value = val
    })

    function handleInput(event: Event) {
      const target = event.target as HTMLInputElement | HTMLTextAreaElement
      let value = target.value

      if (props.type === 'number') {
        // Remove non-digit characters
        value = value.replace(/[^\d]/g, '')
        target.value = value // prevent flickering
      }

      inputValue.value = value
      emit('update:modelValue', value)
    }

    return {
      inputValue,
      handleInput,
    }
  }
})
</script>

<style scoped>
</style>
