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
import { onMounted, onUnmounted } from 'vue';

const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true,
  },
  title: {
    type: String,
    default: 'Confirm Action',
  },
  message: {
    type: String,
    required: true,
  },
  confirmText: {
    type: String,
    default: 'Confirm',
  },
  cancelText: {
    type: String,
    default: 'Cancel',
  },
});

const emit = defineEmits<{
  (e: 'confirm'): void;
  (e: 'cancel'): void;
}>();

function emitConfirm() {
  emit('confirm');
}

function emitCancel() {
  emit('cancel');
}

// Optional: Close modal with Escape key
const handleEscKey = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && props.isOpen) {
    emitCancel();
  }
};

onMounted(() => {
  if (props.isOpen) {
    document.addEventListener('keydown', handleEscKey);
  }
});

onUnmounted(() => {
  document.removeEventListener('keydown', handleEscKey);
});

// Watch for isOpen changes to add/remove listener
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
  z-index: 1050; /* Ensure it's above other content */
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 450px; /* Suitable for confirmation */
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem 1.2rem; /* Slightly smaller padding */
  border-bottom: 1px solid #eee;
  background-color: #f8f9fa;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.15rem; /* Adjusted size */
  color: #333;
  font-weight: 600;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #888;
  padding: 0;
  line-height: 1;
}
.close-button:hover {
  color: #333;
}

.modal-body {
  padding: 1.5rem 1.2rem;
  line-height: 1.6;
  color: #444;
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
  border-top: 1px solid #eee;
  background-color: #f8f9fa;
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
  background-color: #6c757d; /* Secondary */
  color: white;
  border-color: #6c757d;
}
.cancel-btn:hover {
  background-color: #5a6268;
  border-color: #545b62;
}

.confirm-btn {
  background-color: #dc3545; /* Danger */
  color: white;
  border-color: #dc3545;
}
.confirm-btn:hover {
  background-color: #c82333;
  border-color: #bd2130;
}
</style>
