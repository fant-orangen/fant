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

/**
 * @fileoverview TextInput component for single and multiline text fields.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Single line or multiline text input field</li>
 *   <li>Dynamic component rendering based on input type</li>
 *   <li>Two-way binding with v-model</li>
 *   <li>Configurable placeholder and required state</li>
 *   <li>Adjustable number of rows for multiline inputs</li>
 * </ul>
 */
import '@/assets/styles/input/input.css'
export default {

  /**
   * Component props definition
   */
  props: {

    /**
     * HTML ID for the input element
     * @type {string}
     */
    id: {
      type: String,
      required: true,
    },

    /**
     * Label text for the text input
     * @type {string}
     */
    label: {
      type: String,
      required: true,
    },

    /**
     * Current text value (v-model)
     * @type {string}
     */
    modelValue: {
      type: String,
      required: true,
    },

    /**
     * Placeholder text for the input
     * @type {string}
     * @default ''
     */
    placeholder: {
      type: String,
      default: '',
    },

    /**
     * Whether the field is required
     * @type {boolean}
     * @default false
     */
    required: {
      type: Boolean,
      default: false,
    },

    /**
     * Whether to render a multiline textarea
     * @type {boolean}
     * @default false
     */
    multiline: {
      type: Boolean,
      default: false,
    },

    /**
     * Number of rows for multiline textarea
     * @type {number}
     * @default 3
     */
    rows: {
      type: Number,
      default: 3,
    },
  },

  /**
   * Update model value
   * @param {string} event - Event name
   * @param {string} value - New input value
   */
  emits: ['update:modelValue'],

  /**
   * Handles input changes on the text field
   * <p>Emits the input value for v-model binding</p>
   * @param {Event} event - Input change event
   */
  methods: {
    handleInput(event: Event) {
      const target = event.target as HTMLInputElement | HTMLTextAreaElement;
      this.$emit('update:modelValue', target.value);
    },
  },
};
</script>

<style scoped>
</style>
