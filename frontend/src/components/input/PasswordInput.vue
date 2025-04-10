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
.form-group {
  margin-bottom: 16px;
  position: relative;
  width: 100%;
}

label {
  display: block;
  font-weight: 500;
  margin-bottom: 6px;
  color: var(--vt-c-black-soft);
  font-size: 0.9rem;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.text-input {
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  padding: 10px 12px;
  padding-right: 70px; /* Space for toggle button */
  border: 1px solid var(--vt-c-white);
  border-radius: 6px;
  font-size: 1rem;
  color: var(--vt-c-text-dark-2);
  background-color: var(--vt-c-white-soft);
  transition: all 0.2s ease;
  font-family: inherit;
  height: 42px;
}

.text-input:hover {
  border-color: var(--vt-c-teal-light);
}

.text-input:focus {
  outline: none;
  border-color: var(--vt-c-teal-soft);
  box-shadow: 0 0 4px var(--vt-c-teal-light);
}

.text-input::placeholder {
  color: var(--vt-c-text-light-2);
  opacity: 0.7;
}

.text-input.input-error {
  border-color: var(--vt-c-red-soft);
  box-shadow: 0 0 4px var(--vt-c-red-light);
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
  color: var(--vt-c-teal-dark);
}

.icon-invalid {
  color: var(--vt-c-red-dark);
}

.toggle-visibility {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: var(--vt-c-text-light-2);
  font-size: 0.8em;
  padding: 4px;
  min-width: 35px;
  text-align: center;
}

.toggle-visibility:hover {
  color: var(--vt-c-black-soft);
}

.error-message {
  color: var(--vt-c-red-dark);
  font-size: 0.8em;
  margin-top: 4px;
}
</style>
