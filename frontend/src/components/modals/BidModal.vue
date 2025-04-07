<template>
  <div v-if="isOpen" class="modal-backdrop" @click="closeModal"> <div class="modal-content" @click.stop>
    <div class="modal-header">
      <h3>{{ isUpdateMode ? 'Update Your Bid' : 'Place a Bid' }}</h3>
      <button @click="closeModal" class="close-button">&times;</button>
    </div>

    <div class="modal-body">
      <div v-if="bidStatus === 'success'" class="bid-success">
        <div class="success-icon">✓</div>
        <p>{{ isUpdateMode ? 'Your bid has been updated successfully!' : 'Your bid has been placed successfully!' }}</p>
        <button @click="closeModal" class="close-success-button">Close</button>
      </div>

      <div v-else-if="bidStatus === 'error'" class="bid-error">
        <div class="error-icon">!</div>
        <p>{{ errorMessage || `There was an error ${isUpdateMode ? 'updating' : 'placing'} your bid. Please try again.` }}</p>
        <button @click="resetForm" class="try-again-button">Try Again</button>
      </div>

      <form v-else @submit.prevent="submitBid" class="bid-form">
        <div class="item-summary">
          <h4>{{ itemTitle }}</h4>
          <p v-if="!isUpdateMode && formattedPrice" class="current-price">Current price: {{ formattedPrice }}</p>
        </div>

        <div class="form-group">
          <label for="bid-amount">Your {{ isUpdateMode ? 'Updated' : '' }} Bid Amount (kr)</label>
          <input
              id="bid-amount"
              type="number"
              v-model.number="bidAmount"
              min="0"
              step="any"
              required
              class="bid-input"
          />
          <small v-if="!bidAmountValid" class="error-text">
            Bid amount must be 0 or more.
          </small>
        </div>

        <div class="form-group">
          <label for="bid-comment">Comment (Optional)</label>
          <textarea
              id="bid-comment"
              v-model="bidComment"
              class="comment-input"
              rows="3"
              placeholder="Add any details or terms about your bid..."
          ></textarea>
        </div>

        <div class="form-actions">
          <button type="button" @click="closeModal" class="cancel-button">Cancel</button>
          <button
              type="submit"
              class="submit-button"
              :disabled="loading || !bidAmountValid"
          >
            <span v-if="loading" class="loading-spinner-small"></span>
            <span v-else>{{ isUpdateMode ? 'Update Bid' : 'Place Bid' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onUnmounted } from 'vue';
import { placeBid, updateMyBid } from '@/services/BidService'; // Import updateMyBid
import type { BidPayload, BidResponseType, BidUpdatePayload } from '@/models/Bid'; // Import BidUpdatePayload
import { useUserStore } from '@/stores/UserStore';
import type { PropType } from 'vue'; // Import PropType

/**
 * Component props
 */
const props = defineProps<{
  isOpen: boolean;
  itemId: string | number;
  itemTitle: string;
  currentPrice?: number | null; // Make optional as not needed for update
  // --- Add prop for existing bid data ---
  initialBid: BidResponseType | null;
}>();

/**
 * Component emits
 */
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'bid-placed'): void; // Keep for placing
  (e: 'bid-updated'): void; // Add for updating
}>();

// Form state
const bidAmount = ref<number>(0);
const bidComment = ref<string>('');
const loading = ref<boolean>(false);
const bidStatus = ref<'idle' | 'loading' | 'success' | 'error'>('idle');
const errorMessage = ref<string>('');

// --- Determine Mode ---
const isUpdateMode = computed(() => !!props.initialBid);

// --- Watcher to reset/prefill form when modal opens/props change ---
watch(() => [props.isOpen, props.initialBid], ([newIsOpen, newInitialBid]) => {
  if (newIsOpen) {
    bidStatus.value = 'idle'; // Reset status on open
    errorMessage.value = '';
    loading.value = false;

    if (newInitialBid) {
      // Pre-fill for update mode
      bidAmount.value = newInitialBid.amount;
      bidComment.value = newInitialBid.comment || '';
    } else {
      // Reset for create mode (or set based on currentPrice if needed)
      bidAmount.value = props.currentPrice ?? 0; // Use current price if available for create
      bidComment.value = '';
    }
  }
}, { immediate: true }); // immediate: true to run on initial mount

// Close modal with escape key
const handleEscKey = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && props.isOpen) {
    closeModal();
  }
};

// Add/remove listener using watch on isOpen prop
watch(() => props.isOpen, (newIsOpen) => {
  if (newIsOpen) {
    document.addEventListener('keydown', handleEscKey);
  } else {
    document.removeEventListener('keydown', handleEscKey);
  }
});

// Cleanup listener on unmount just in case
onUnmounted(() => {
  document.removeEventListener('keydown', handleEscKey);
});


// Computed properties
const bidAmountValid = computed(() => {
  // Allow 0, but typically bids should be positive? Adjust if needed.
  return typeof bidAmount.value === 'number' && bidAmount.value >= 0;
});

const formattedPrice = computed(() => {
  if (props.currentPrice === null || props.currentPrice === undefined) {
    // Return null or empty string if not applicable or needed
    return null;
  }
  return `${props.currentPrice.toLocaleString('no-NO')} kr`;
});

// Methods
function closeModal() {
  emit('close');
}

function resetForm() {
  bidStatus.value = 'idle';
  errorMessage.value = '';
  // Reset form fields based on mode if needed, but watcher handles main reset
  if (props.initialBid) {
    bidAmount.value = props.initialBid.amount;
    bidComment.value = props.initialBid.comment || '';
  } else {
    bidAmount.value = props.currentPrice ?? 0;
    bidComment.value = '';
  }
}

async function submitBid() {
  if (!bidAmountValid.value) {
    errorMessage.value = 'Please enter a valid bid amount (0 or more).';
    bidStatus.value = 'error';
    return;
  }

  const userStore = useUserStore();
  if (!userStore.loggedIn) {
    bidStatus.value = 'error';
    errorMessage.value = 'You must be logged in to place or update a bid.';
    return;
  }

  bidStatus.value = 'loading';
  loading.value = true;
  errorMessage.value = ''; // Clear previous errors

  try {
    if (isUpdateMode.value && props.initialBid) {
      // --- Update Logic ---
      const bidUpdateData: BidUpdatePayload = {
        itemId: props.initialBid.itemId, // Use itemId from initial bid
        amount: bidAmount.value,
        comment: bidComment.value.trim() || undefined,
      };
      await updateMyBid(bidUpdateData);
      bidStatus.value = 'success';
      emit('bid-updated'); // Emit update event

    } else {
      // --- Create Logic (Existing) ---
      const bidCreateData: BidPayload = {
        amount: bidAmount.value,
        comment: bidComment.value.trim() || undefined,
        itemId: props.itemId,
      };
      await placeBid(bidCreateData);
      bidStatus.value = 'success';
      emit('bid-placed'); // Emit placed event
    }
  } catch (error) {
    bidStatus.value = 'error';
    errorMessage.value = error instanceof Error
        ? error.message
        : `Failed to ${isUpdateMode.value ? 'update' : 'place'} bid. Please try again.`;
    console.error(`Error ${isUpdateMode.value ? 'updating' : 'placing'} bid:`, error);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
/* ...(keep existing styles)... */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6); /* Darker backdrop */
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050; /* Ensure it's above other content */
  backdrop-filter: blur(3px); /* Optional blur */
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 5px 25px rgba(0, 0, 0, 0.2); /* Stronger shadow */
  display: flex; /* Use flex for better structure */
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #dee2e6; /* Slightly darker border */
  background-color: #f8f9fa; /* Light header background */
  border-top-left-radius: 8px; /* Match content radius */
  border-top-right-radius: 8px;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem; /* Slightly larger */
  color: #495057; /* Darker text */
  font-weight: 600;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.75rem; /* Larger close button */
  cursor: pointer;
  color: #6c757d; /* Muted color */
  line-height: 1;
  padding: 0;
}

.close-button:hover {
  color: #343a40; /* Darken on hover */
}

.modal-body {
  padding: 1.5rem;
  flex-grow: 1; /* Allow body to take available space */
}

.bid-form {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

.item-summary {
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

.item-summary h4 {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  color: #333;
  font-weight: 600;
}

.current-price {
  font-weight: 600;
  color: #007bff;
  margin: 0;
  font-size: 0.95rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 500;
  color: #555;
  font-size: 0.9rem;
}

.bid-input,
.comment-input {
  padding: 0.75rem; /* Adjusted padding */
  border: 1px solid #ced4da; /* Standard BS border */
  border-radius: 4px;
  font-size: 1rem;
  line-height: 1.5;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}

.bid-input:focus,
.comment-input:focus {
  border-color: #80bdff; /* BS focus color */
  outline: 0;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25); /* BS focus shadow */
}

/* Style for invalid input */
.bid-input:invalid, .bid-input.is-invalid {
  border-color: #dc3545;
}
.bid-input:invalid:focus, .bid-input.is-invalid:focus {
  border-color: #dc3545;
  box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
}


.error-text {
  color: #dc3545;
  font-size: 0.8rem; /* Smaller error text */
  margin-top: 0.25rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.8rem;
  margin-top: 1.5rem; /* More space before actions */
  padding-top: 1rem;
  border-top: 1px solid #eee; /* Separator */
}

.cancel-button,
.submit-button,
.close-success-button,
.try-again-button {
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  font-size: 0.95rem; /* Slightly adjusted font size */
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;
}

.cancel-button {
  background-color: #6c757d; /* BS secondary */
  border: 1px solid #6c757d;
  color: white;
}

.cancel-button:hover {
  background-color: #5a6268;
  border-color: #545b62;
}

.submit-button {
  background-color: #007bff; /* BS primary */
  border: none;
  color: white;
}

.submit-button:hover:not(:disabled) {
  background-color: #0069d9;
}

.submit-button:disabled {
  background-color: #6cacee; /* Lighter blue when disabled */
  cursor: not-allowed;
}

.loading-spinner-small {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s linear infinite;
  vertical-align: middle; /* Align spinner better */
  margin-right: 0.3rem; /* Space between spinner and text */
}

.bid-success,
.bid-error {
  text-align: center;
  padding: 1.5rem 1rem;
}
.bid-success p, .bid-error p{
  font-size: 1.1rem;
  margin-bottom: 1.5rem;
  color: #333;
}

.success-icon,
.error-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px; /* Slightly smaller icon */
  height: 50px;
  border-radius: 50%;
  margin: 0 auto 1rem;
  font-size: 1.6rem; /* Slightly smaller font */
  font-weight: bold;
}

.success-icon {
  background-color: #d4edda; /* Lighter green background */
  color: #155724; /* Darker green checkmark */
  border: 1px solid #c3e6cb;
}

.error-icon {
  background-color: #f8d7da; /* Lighter red background */
  color: #721c24; /* Darker red exclamation */
  border: 1px solid #f5c6cb;
}

.close-success-button {
  background-color: #28a745; /* BS success */
  border: none;
  color: white;
  margin-top: 1rem;
}

.close-success-button:hover {
  background-color: #218838;
}

.try-again-button {
  background-color: #ffc107; /* BS warning */
  border: none;
  color: #212529; /* Dark text on yellow */
  margin-top: 1rem;
}

.try-again-button:hover {
  background-color: #e0a800;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 480px) {
  .form-actions {
    flex-direction: column;
    gap: 0.5rem; /* Reduce gap for stacked buttons */
  }

  .cancel-button,
  .submit-button {
    width: 100%;
  }
}
</style>
