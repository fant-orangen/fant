<template>
  <div class="form-group">
    <label :for="id">{{ label }}</label>
    <div class="input-wrapper">
      <input
        :id="id"
        :type="inputType"
        :value="modelValue"
        @input="handleInput"
        :required="required"
        :placeholder="placeholder"
        class="text-input"
        :class="{ 'input-error': !!translatedErrorMessage && modelValue.length > 0 }"
        :aria-invalid="!!translatedErrorMessage && modelValue.length > 0"
      />
      <span v-if="modelValue.length > 0" class="validity-icon">
        <span v-if="isValid" class="icon-valid">✓</span>
        <span v-else class="icon-invalid">✗</span>
      </span>
      <button
        type="button"
        @click="togglePasswordVisibility"
        class="toggle-visibility"
        :aria-label="t(isPasswordVisible ? 'PASSWORD_HIDE' : 'PASSWORD_SHOW')" >
        {{ t(isPasswordVisible ? 'PASSWORD_TOGGLE_HIDE' : 'PASSWORD_TOGGLE_SHOW') }}
      </button>
    </div>
    <p v-if="translatedErrorMessage && modelValue.length > 0" class="error-message">
      {{ translatedErrorMessage }}
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { useI18n } from 'vue-i18n'; // Import useI18n
import { validatePassword } from '@/utils/validation';

// --- i18n setup ---
const { t } = useI18n();
// --- ---

const props = defineProps<{
  id: string;
  label: string;
  modelValue: string;
  placeholder?: string;
  required?: boolean;
}>();

const emit = defineEmits(['update:modelValue', 'update:isValid']);

const errorMessageKey = ref<string | null>(null); // Store the key now
const isValid = ref(false);
const isPasswordVisible = ref(false);

// Computed property to get the translated error message
const translatedErrorMessage = computed(() => {
  return errorMessageKey.value ? t(errorMessageKey.value) : '';
});

const inputType = computed(() => (isPasswordVisible.value ? 'text' : 'password'));

function togglePasswordVisibility() {
  isPasswordVisible.value = !isPasswordVisible.value;
}

// Function to handle input and validation
function handleInput(event: Event) {
  const target = event.target as HTMLInputElement;
  const currentValue = target.value;

  emit('update:modelValue', currentValue);

  const validationResult = validatePassword(currentValue);

  // Store the message key (or null if valid)
  errorMessageKey.value = currentValue.length > 0 ? validationResult.messageKey : null;
  isValid.value = validationResult.isValid;

  emit('update:isValid', isValid.value);
}

// Validate initial modelValue and when it changes externally
watch(() => props.modelValue, (newValue) => {
  const validationResult = validatePassword(newValue);
  errorMessageKey.value = newValue.length > 0 ? validationResult.messageKey : null;
  isValid.value = validationResult.isValid;
  emit('update:isValid', isValid.value);
}, { immediate: true });

</script>

<style scoped>
/* Styles remain the same as the previous version */
.form-group {
  margin-bottom: 1rem;
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

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.text-input {
  flex-grow: 1;
  width: auto;
  padding: 10px 12px;
  padding-right: 70px; /* Adjust if button text width changes significantly */
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  color: #333;
  background-color: #fff;
  transition: all 0.2s ease;
  font-family: inherit;
  height: 42px;
  box-sizing: border-box;
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

.text-input.input-error {
  border-color: red;
  box-shadow: 0 0 0 2px rgba(255, 0, 0, 0.2);
}

.validity-icon {
  position: absolute;
  right: 55px; /* Adjust based on button width */
  top: 50%;
  transform: translateY(-50%);
  font-size: 1.2em;
  pointer-events: none;
}

.icon-valid {
  color: green;
}

.icon-invalid {
  color: red;
}

.toggle-visibility {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: #555;
  font-size: 0.8em;
  padding: 4px;
  min-width: 35px;
  text-align: center;
}

.toggle-visibility:hover {
  color: #000;
}

.error-message {
  color: red;
  font-size: 0.8em;
  margin-top: 4px;
}
</style>
