<template>
  <div class="form-group">
    <label :for="id">{{ label }}</label>
    <select
      :id="id"
      :value="modelValue"
      @change="handleChange"
      :required="required"
    >
      <option value="" disabled selected>{{ placeholder }}</option>
      <option v-for="option in options" :key="option" :value="option">{{ option }}</option>
    </select>
  </div>
</template>

<script lang="ts">
/**
 * @fileoverview SelectInput component for dropdown selection fields.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Customizable dropdown select input</li>
 *   <li>Support for placeholder option</li>
 *   <li>Dynamic population of options</li>
 *   <li>Two-way binding with v-model</li>
 *   <li>Configurable required state</li>
 * </ul>
 * @author nicktuf
 * @version 1.0
 */

import '@/assets/styles/input/input.css'
import '@/assets/styles/input/file.css'

/**
 * Component props definition
 */
export default {
  props: {

    /**
     * HTML ID for the select element
     * @type {string}
     */
    id: {
      type: String,
      required: true,
    },

    /**
     * Label text for the select input
     * @type {string}
     */
    label: {
      type: String,
      required: true,
    },

    /**
     * Currently selected value (v-model)
     * @type {string}
     */
    modelValue: {
      type: String,
      required: true,
    },

    /**
     * Available options for the dropdown
     * @type {string[]}
     */
    options: {
      type: Array as () => string[],
      required: true,
    },

    /**
     * Placeholder text for the empty selection
     * @type {string}
     * @default 'Select an option'
     */
    placeholder: {
      type: String,
      default: 'Select an option',
    },

    /**
     * Whether the field is required
     * @type {boolean}
     * @default true
     */
    required: {
      type: Boolean,
      default: true,
    },
  },

  /**
   * Update model value
   * @param {string} event - Event name
   * @param {string} value - New selected value
   */
  emits: ['update:modelValue'],
  methods: {

    /**
     * Handles change events on the select element
     * <p>Emits the selected value for v-model binding</p>
     * @param {Event} event - Select change event
     */
    handleChange(event: Event) {
      const target = event.target as HTMLSelectElement;
      if (target) {
        this.$emit('update:modelValue', target.value);
      }
    },
  },
};
</script>

<style scoped>
</style>
