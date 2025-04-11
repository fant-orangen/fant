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
/**
 * @fileoverview PasswordInput component with validation and visibility toggle.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Password input with toggle for visibility</li>
 *   <li>Real-time validation with visual feedback</li>
 *   <li>Internationalized error messages</li>
 *   <li>Accessibility support with ARIA attributes</li>
 *   <li>Customizable label and placeholder</li>
 * </ul>
 */
import { ref, watch, computed } from 'vue';
import { useI18n } from 'vue-i18n'; // Import useI18n
import { validatePassword } from '@/utils/validation';

/**
 * i18n instance for translations
 */
const { t } = useI18n();

/**
 * Component props definition
 */
const props = defineProps<{
  /**
   * HTML ID for the input element
   * @type {string}
   */
  id: string;

  /**
   * Label text for the password input
   * @type {string}
   */
  label: string;

  /**
   * Current password value (v-model)
   * @type {string}
   */
  modelValue: string;

  /**
   * Placeholder text for the input
   * @type {string}
   */
  placeholder?: string;

  /**
   * Whether the field is required
   * @type {boolean}
   * @default false
   */
  required?: boolean;
}>();

/**
 * Event emitters definition
 */
const emit = defineEmits([
  /**
   * Update model value
   * @param {string} value - New password value
   */
  'update:modelValue',

  /**
   * Update validity state
   * @param {boolean} isValid - Whether password meets requirements
   */
  'update:isValid']);

/**
 * Translation key for current error message
 * @type {Ref<string|null>}
 */
const errorMessageKey = ref<string | null>(null);

/**
 * Whether the password meets all validation requirements
 * @type {Ref<boolean>}
 */
const isValid = ref(false);

/**
 * Whether the password is currently visible
 * @type {Ref<boolean>}
 */
const isPasswordVisible = ref(false);

/**
 * Translated error message based on the current error key
 * @type {ComputedRef<string>}
 */
const translatedErrorMessage = computed(() => {
  return errorMessageKey.value ? t(errorMessageKey.value) : '';
});

/**
 * Current input type (text or password) based on visibility state
 * @type {ComputedRef<string>}
 */
const inputType = computed(() => (isPasswordVisible.value ? 'text' : 'password'));

/**
 * Toggles password visibility between shown and hidden
 */
function togglePasswordVisibility() {
  isPasswordVisible.value = !isPasswordVisible.value;
}

/**
 * Handles input changes and validates password
 * @param {Event} event - Input change event
 */
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

/**
 * Watches for external model value changes and validates password
 */
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
