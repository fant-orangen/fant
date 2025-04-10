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
/**
 * @fileoverview BidModal component for placing and updating bids on items.
 * <p>This component provides functionality for:</p>
 * <ul>
 *   <li>Creating new bids on items</li>
 *   <li>Updating existing bids</li>
 *   <li>Displaying success and error states</li>
 *   <li>Form validation</li>
 * </ul>
 */
import { ref, computed, watch, onUnmounted } from 'vue';
import { placeBid, updateMyBid } from '@/services/BidService'; // Import updateMyBid
import type { BidPayload, BidResponseType, BidUpdatePayload } from '@/models/Bid'; // Import BidUpdatePayload
import { useUserStore } from '@/stores/UserStore';

/**
 * Component props definition
 */
const props = defineProps<{
  /**
   * Controls modal visibility
   * @type {boolean}
   */
  isOpen: boolean;

  /**
   * ID of the item being bid on
   * @type {string|number}
   */
  itemId: string | number;

  /**
   * Title of the item being bid on
   * @type {string}
   */
  itemTitle: string;

  /**
   * Current price of the item (optional)
   * @type {number|null}
   */
  currentPrice?: number | null;

  /**
   * Existing bid data when updating (null when creating new bid)
   * @type {BidResponseType|null}
   */
  initialBid: BidResponseType | null;
}>();

/**
 * Event emitters definition
 */
const emit = defineEmits<{
  /**
   * Emitted when modal should be closed
   */
  (e: 'close'): void;

  /**
   * Emitted when a new bid is successfully placed
   */
  (e: 'bid-placed'): void;

  /**
   * Emitted when an existing bid is successfully updated
   */
  (e: 'bid-updated'): void; // Add for updating
}>();

/**
 * Form state for bid amount
 * @type {Ref<number>}
 */
const bidAmount = ref<number>(0);

/**
 * Form state for bid comment
 * @type {Ref<string>}
 */
const bidComment = ref<string>('');

/**
 * Loading state indicator
 * @type {Ref<boolean>}
 */
const loading = ref<boolean>(false);

/**
 * Current status of the bid operation
 * @type {Ref<'idle'|'loading'|'success'|'error'>}
 */
const bidStatus = ref<'idle' | 'loading' | 'success' | 'error'>('idle');

/**
 * Error message when bid operation fails
 * @type {Ref<string>}
 */
const errorMessage = ref<string>('');


/**
 * Determines if modal is in update or create mode
 * @type {ComputedRef<boolean>}
 */
const isUpdateMode = computed(() => !!props.initialBid);

/**
 * Watches for modal open state and initializes form
 * <p>Pre-fills form with existing bid data when updating</p>
 */
watch(() => [props.isOpen, props.initialBid], ([newIsOpen, newInitialBid]) => {
  if (newIsOpen) {
    bidStatus.value = 'idle';
    errorMessage.value = '';
    loading.value = false;

    // --- FIX: Use a more robust type check/guard ---
    // Check if newInitialBid is truthy AND has an 'id' property
    if (newInitialBid && typeof newInitialBid === 'object' && 'id' in newInitialBid) {
      // Pre-fill for update mode - now TS knows it's BidResponseType
      bidAmount.value = newInitialBid.amount;
      bidComment.value = newInitialBid.comment || '';
    } else {
      // Reset for create mode
      bidAmount.value = props.currentPrice ?? 0;
      bidComment.value = '';
    }
  }
}, { immediate: true });

/**
 * Handler for Escape key to close modal
 * @param {KeyboardEvent} event - Keyboard event
 */
const handleEscKey = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && props.isOpen) {
    closeModal();
  }
};

/**
 * Sets up and cleans up escape key listener based on modal visibility
 */
watch(() => props.isOpen, (newIsOpen) => {
  if (newIsOpen) {
    document.addEventListener('keydown', handleEscKey);
  } else {
    document.removeEventListener('keydown', handleEscKey);
  }
});

/**
 * Cleans up event listeners when component is unmounted
 */
onUnmounted(() => {
  document.removeEventListener('keydown', handleEscKey);
});


/**
 * Validates bid amount
 * @type {ComputedRef<boolean>}
 */
const bidAmountValid = computed(() => {
  // Allow 0, but typically bids should be positive? Adjust if needed.
  return typeof bidAmount.value === 'number' && bidAmount.value >= 0;
});

/**
 * Formats current price for display
 * @type {ComputedRef<string|null>}
 */
const formattedPrice = computed(() => {
  if (props.currentPrice === null || props.currentPrice === undefined) {
    // Return null or empty string if not applicable or needed
    return null;
  }
  return `${props.currentPrice.toLocaleString('no-NO')} kr`;
});

/**
 * Closes the modal
 */
function closeModal() {
  emit('close');
}

/**
 * Resets the form to initial state
 */
function resetForm() {
  bidStatus.value = 'idle';
  errorMessage.value = '';
  // Use the same robust check here
  if (props.initialBid && 'id' in props.initialBid) {
    bidAmount.value = props.initialBid.amount;
    bidComment.value = props.initialBid.comment || '';
  } else {
    bidAmount.value = props.currentPrice ?? 0;
    bidComment.value = '';
  }
}

/**
 * Submits the bid form
 * <p>Handles both create and update operations</p>
 */
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
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 5px 25px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid var(--color-border);
  background-color: var(--color-background-soft);
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
  color: var(--color-heading);
  font-weight: 600;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.75rem;
  cursor: pointer;
  color: var(--vt-c-text-light-2);
  line-height: 1;
  padding: 0;
}

.close-button:hover {
  color: var(--vt-c-text-dark-1);
}

.modal-body {
  padding: 1.5rem;
  flex-grow: 1;
}

.bid-form {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

.item-summary {
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--color-border);
}

.item-summary h4 {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  color: var(--color-heading);
  font-weight: 600;
}

.current-price {
  font-weight: 600;
  color: var(--vt-c-teal-dark);
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
  color: var(--vt-c-text-dark-2);
  font-size: 0.9rem;
}

.bid-input,
.comment-input {
  padding: 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 1rem;
  line-height: 1.5;
  background-color: var(--color-background);
  color: var(--color-text);
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}

.bid-input:focus,
.comment-input:focus {
  border-color: var(--vt-c-teal-soft);
  outline: 0;
  box-shadow: 0 0 0 0.1rem var(--vt-c-teal-dark);
}

.bid-input:invalid, .bid-input.is-invalid {
  border-color: var(--vt-c-red-soft);
}

.bid-input:invalid:focus, .bid-input.is-invalid:focus {
  border-color: var(--vt-c-red-soft);
  box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
}

.error-text {
  color: var(--vt-c-red-dark);
  font-size: 0.8rem;
  margin-top: 0.25rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.8rem;
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid var(--color-border);
}

.cancel-button,
.submit-button,
.close-success-button,
.try-again-button {
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;
}

.cancel-button {
  background-color: var(--color-background-mute);
  border: 1px solid var(--color-border);
  color: var(--color-text);
}

.cancel-button:hover {
  background-color: var(--color-background-soft);
}

.submit-button {
  background-color: var(--vt-c-teal-soft);
  border: none;
  color: var(--vt-c-white);
}

.submit-button:hover:not(:disabled) {
  background-color: var(--vt-c-teal-dark);
}

.submit-button:disabled {
  opacity: 0.6;
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
  vertical-align: middle;
  margin-right: 0.3rem;
}

.bid-success,
.bid-error {
  text-align: center;
  padding: 1.5rem 1rem;
}

.bid-success p,
.bid-error p {
  font-size: 1.1rem;
  margin-bottom: 1.5rem;
  color: var(--color-text);
}

.success-icon,
.error-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin: 0 auto 1rem;
  font-size: 1.6rem;
  font-weight: bold;
}

.success-icon {
  background-color: var(--vt-c-teal-light);
  color: var(--vt-c-teal-dark);
  border: 1px solid var(--vt-c-teal-soft);
}

.error-icon {
  background-color: var(--vt-c-red-light);
  color: var(--vt-c-red-dark);
  border: 1px solid var(--vt-c-red-soft);
}

.close-success-button {
  background-color: var(--vt-c-teal-soft);
  border: none;
  color: var(--vt-c-white);
  margin-top: 1rem;
}

.close-success-button:hover {
  background-color: var(--vt-c-teal-dark);
}

.try-again-button {
  background-color: var(--vt-c-teal-light);
  border: 1px solid var(--vt-c-teal-soft);
  color: var(--vt-c-teal-dark);
  margin-top: 1rem;
}

.try-again-button:hover {
  background-color: var(--vt-c-teal-soft);
  color: var(--vt-c-white);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 480px) {
  .form-actions {
    flex-direction: column;
    gap: 0.5rem;
  }

  .cancel-button,
  .submit-button {
    width: 100%;
  }
}
</style>
