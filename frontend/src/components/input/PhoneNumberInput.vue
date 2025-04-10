<template>
  <div class="form-group">
    <label :for="id">{{ label }}</label>
    <div class="input-wrapper">
      <input
        :id="id"
        type="tel"
        :value="modelValue"
        @input="handleInput" :required="required"
        :placeholder="placeholder"
        class="text-input"
        :class="{ 'input-error': !!translatedErrorMessage && modelValue.length > 0 }"
        :aria-invalid="!!translatedErrorMessage && modelValue.length > 0"
        :aria-describedby="id + '-hint'"
      />
      <span v-if="modelValue.length > 0" class="validity-icon">
        <span v-if="isValid" class="icon-valid">✓</span>
        <span v-else class="icon-invalid">✗</span>
      </span>
    </div>
    <p :id="id + '-hint'" v-if="translatedErrorMessage && modelValue.length > 0" class="error-message">
      {{ translatedErrorMessage }}
    </p>
    <p :id="id + '-hint'" v-else class="hint-message">
      {{ t('INTERNATIONAL_FORMAT_PHONENUMBER_REQUIREMENT') }}
    </p>
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview PhoneNumberInput component with validation and format guidance.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Phone number input with real-time validation</li>
 *   <li>Sanitization of input to allow only valid phone number characters</li>
 *   <li>Visual feedback on validation state</li>
 *   <li>Internationalized error messages and hint text</li>
 *   <li>Accessibility support with ARIA attributes</li>
 * </ul>
 */
import { ref, watch, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { validatePhoneNumber } from '@/utils/validation';

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
   * Label text for the phone input
   * @type {string}
   */
  label: string;

  /**
   * Current phone number value (v-model)
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
   * @param {string} value - New sanitized phone number value
   */
  'update:modelValue',

  /**
   * Update validity state
   * @param {boolean} isValid - Whether phone number meets requirements
   */
  'update:isValid']);

/**
 * Translation key for current error message
 * @type {Ref<string|null>}
 */
const errorMessageKey = ref<string | null>(null);

/**
 * Whether the phone number meets all validation requirements
 * @type {Ref<boolean>}
 */
const isValid = ref(true);

/**
 * Translated error message based on the current error key
 * @type {ComputedRef<string>}
 */
const translatedErrorMessage = computed(() => {
  return errorMessageKey.value ? t(errorMessageKey.value) : '';
});

/**
 * Handles input changes and validates phone number
 * <p>Sanitizes input to allow only valid phone characters (+, digits, spaces)</p>
 * @param {Event} event - Input change event
 */
function handleInput(event: Event) {
  const target = event.target as HTMLInputElement;
  const currentValue = target.value;

  // Sanitize the input: Allow only digits, +, and space
  // Remove any character that is NOT a digit, '+', or space
  const sanitizedValue = currentValue.replace(/[^0-9+ ]/g, '');

  // If the sanitized value is different from the input's current value,
  // update the input field directly to prevent the invalid character from showing.
  if (sanitizedValue !== currentValue) {
    target.value = sanitizedValue;
  }

  // Emit the sanitized value for v-model
  emit('update:modelValue', sanitizedValue);

  // Perform validation on the sanitized value
  const validationResult = validatePhoneNumber(sanitizedValue);
  errorMessageKey.value = validationResult.messageKey;
  isValid.value = validationResult.isValid;

  // Emit validity status based on the sanitized value
  emit('update:isValid', isValid.value);
}
// --- ---

/**
 * Watches for external model value changes and validates phone number
 */
watch(() => props.modelValue, (newValue) => {
  // newValue is already sanitized because it comes from the emitted update:modelValue
  const validationResult = validatePhoneNumber(newValue);
  errorMessageKey.value = validationResult.messageKey;
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
  padding-right: 35px;
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
  right: 10px;
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

.error-message, .hint-message {
  font-size: 0.8em;
  margin-top: 4px;
  display: block;
}

.error-message {
  color: var(--vt-c-red-dark);
}

.hint-message {
  color: var(--vt-c-text-light-2);
}
</style>
