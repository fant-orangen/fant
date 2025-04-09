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
    options: {
      type: Array as () => string[],
      required: true,
    },
    placeholder: {
      type: String,
      default: 'Select an option',
    },
    required: {
      type: Boolean,
      default: true,
    },
  },
  emits: ['update:modelValue'],
  methods: {
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
.form-group {
  margin-bottom: 16px;
  position: relative;
}

label {
  display: block;
  font-weight: 500;
  margin-bottom: 6px;
  color: #2c3e50;
  font-size: 0.9rem;
}

select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  color: #333;
  background-color: #fff;
  transition: all 0.2s ease;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%232c3e50' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 16px;
  padding-right: 40px;
  height: 42px; /* Fixed height to match TextInput */
  box-sizing: border-box;
}

select:hover {
  border-color: #3498db;
}

select:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

select:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
  opacity: 0.7;
}

select option {
  padding: 8px;
}

/* Style for placeholder option */
select option[value=""][disabled] {
  color: #999;
}

/* Add soft blue highlight on hover for options */
select option:hover {
  background-color: #e3f2fd;
}
</style>
