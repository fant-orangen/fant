<template>
  <div v-if="isOpen" class="modal-backdrop" @click="emitCancel">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ title }}</h3>
        <button @click="emitCancel" class="close-button">&times;</button>
      </div>
      <div class="modal-body">
        <p>{{ message }}</p>
      </div>
      <div class="modal-footer">
        <button @click="emitCancel" class="cancel-btn">{{ cancelText }}</button>
        <button @click="emitConfirm" class="confirm-btn">{{ confirmText }}</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview ConfirmDeleteModal component for displaying confirmation dialogs before destructive actions.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Customizable confirmation dialogs</li>
 *   <li>Keyboard accessibility with Escape key support</li>
 *   <li>Customizable text for buttons and messages</li>
 *   <li>Backdrop click to cancel</li>
 * </ul>
 */
import { onMounted, onUnmounted } from 'vue';

/**
 * Component props definition
 */
const props = defineProps({

  /**
   * Controls modal visibility
   * @type {Boolean}
   */
  isOpen: {
    type: Boolean,
    required: true,
  },

  /**
   * Modal title text
   * @type {String}
   */
  title: {
    type: String,
    default: 'Confirm Action',
  },

  /**
   * Modal body message
   * @type {String}
   */
  message: {
    type: String,
    required: true,
  },

  /**
   * Text for the confirmation button
   * @type {String}
   */
  confirmText: {
    type: String,
    default: 'Confirm',
  },

  /**
   * Text for the cancel button
   * @type {String}
   */
  cancelText: {
    type: String,
    default: 'Cancel',
  },
});

/**
 * Event emitters definition
 */
const emit = defineEmits<{
  /**
   * Emitted when the user confirms the action
   */
  (e: 'confirm'): void;

  /**
   * Emitted when the user cancels the action
   */
  (e: 'cancel'): void;
}>();

/**
 * Emits the confirm event to the parent component
 */
function emitConfirm() {
  emit('confirm');
}

/**
 * Emits the cancel event to the parent component
 */
function emitCancel() {
  emit('cancel');
}

/**
 * Handler for Escape key to cancel modal
 * @param {KeyboardEvent} event - Keyboard event
 */
const handleEscKey = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && props.isOpen) {
    emitCancel();
  }
};

/**
 * Sets up event listeners when component is mounted
 */
onMounted(() => {
  if (props.isOpen) {
    document.addEventListener('keydown', handleEscKey);
  }
});

/**
 * Cleans up event listeners when component is unmounted
 */
onUnmounted(() => {
  document.removeEventListener('keydown', handleEscKey);
});

/**
 * Watches for changes in modal visibility to manage event listeners
 */
import { watch } from 'vue';
watch(() => props.isOpen, (newIsOpen) => {
  if (newIsOpen) {
    document.addEventListener('keydown', handleEscKey);
  } else {
    document.removeEventListener('keydown', handleEscKey);
  }
});
</script>

<style scoped>
/* Basic Modal Styling - Adapt based on your project's design system */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
  backdrop-filter: blur(3px);
}

.modal-content {
  background-color: var(--color-background);
  border-radius: 8px;
  width: 90%;
  max-width: 450px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem 1.2rem;
  border-bottom: 1px solid var(--color-border);
  background-color: var(--color-background-soft);
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.15rem;
  color: var(--color-heading);
  font-weight: 600;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: var(--vt-c-text-light-2);
  padding: 0;
  line-height: 1;
}

.close-button:hover {
  color: var(--vt-c-text-dark-1);
}

.modal-body {
  padding: 1.5rem 1.2rem;
  line-height: 1.6;
  color: var(--color-text);
  font-size: 1rem;
}

.modal-body p {
  margin: 0;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding: 0.8rem 1.2rem;
  border-top: 1px solid var(--color-border);
  background-color: var(--color-background-soft);
  border-bottom-left-radius: 8px;
  border-bottom-right-radius: 8px;
}

.cancel-btn,
.confirm-btn {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;
  border: 1px solid transparent;
}

.cancel-btn {
  background-color: var(--color-background-mute);
  color: var(--color-text);
  border-color: var(--color-border);
}

.cancel-btn:hover {
  background-color: var(--color-background);
  border-color: var(--color-border);
}

.confirm-btn {
  background-color: var(--vt-c-red-dark);
  color: var(--vt-c-white);
  border-color: var(--vt-c-red-dark);
}

.confirm-btn:hover {
  background-color: var(--vt-c-red-dark);
  filter: brightness(0.9);
  border-color: var(--vt-c-red-dark);
}
</style>
