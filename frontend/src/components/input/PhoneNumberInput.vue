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
import { ref, watch, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { validatePhoneNumber } from '@/utils/validation';

const { t } = useI18n();

const props = defineProps<{
  id: string;
  label: string;
  modelValue: string;
  placeholder?: string;
  required?: boolean;
}>();

const emit = defineEmits(['update:modelValue', 'update:isValid']);

const errorMessageKey = ref<string | null>(null);
const isValid = ref(true);

const translatedErrorMessage = computed(() => {
  return errorMessageKey.value ? t(errorMessageKey.value) : '';
});

// --- Updated handleInput function ---
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

// Watcher remains the same, it will validate the sanitized value passed via v-model
watch(() => props.modelValue, (newValue) => {
  // newValue is already sanitized because it comes from the emitted update:modelValue
  const validationResult = validatePhoneNumber(newValue);
  errorMessageKey.value = validationResult.messageKey;
  isValid.value = validationResult.isValid;
  emit('update:isValid', isValid.value);
}, { immediate: true });

</script>

<style scoped>
.form-group { margin-bottom: 1rem; position: relative; width: 100%; }
label { display: block; font-weight: 500; margin-bottom: 6px; color: #2c3e50; font-size: 0.9rem; }
.input-wrapper { position: relative; display: flex; align-items: center; }
.text-input { flex-grow: 1; width: auto; padding: 10px 12px; padding-right: 35px; border: 1px solid #ddd; border-radius: 6px; font-size: 1rem; color: #333; background-color: #fff; transition: all 0.2s ease; font-family: inherit; height: 42px; box-sizing: border-box; }
.text-input:hover { border-color: #3498db; }
.text-input:focus { outline: none; border-color: #3498db; box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2); }
.text-input::placeholder { color: #999; opacity: 0.7; }
.text-input.input-error { border-color: red; box-shadow: 0 0 0 2px rgba(255, 0, 0, 0.2); }
.validity-icon { position: absolute; right: 10px; top: 50%; transform: translateY(-50%); font-size: 1.2em; pointer-events: none; }
.icon-valid { color: green; }
.icon-invalid { color: red; }
.error-message, .hint-message { font-size: 0.8em; margin-top: 4px; display: block; }
.error-message { color: red; }
.hint-message { color: #666; }
</style>
