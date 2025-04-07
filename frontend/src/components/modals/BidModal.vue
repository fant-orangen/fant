<template>
  <div v-if="isOpen" class="modal-backdrop">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>Place a Bid</h3>
        <button @click="closeModal" class="close-button">&times;</button>
      </div>

      <div class="modal-body">
        <div v-if="bidStatus === 'success'" class="bid-success">
          <div class="success-icon">✓</div>
          <p>Your bid has been placed successfully!</p>
          <button @click="closeModal" class="close-success-button">Close</button>
        </div>

        <div v-else-if="bidStatus === 'error'" class="bid-error">
          <div class="error-icon">!</div>
          <p>{{ errorMessage || 'There was an error placing your bid. Please try again.' }}</p>
          <button @click="resetForm" class="try-again-button">Try Again</button>
        </div>

        <form v-else @submit.prevent="submitBid" class="bid-form">
          <div class="item-summary">
            <h4>{{ itemTitle }}</h4>
            <p class="current-price">Current price: {{ formattedPrice }}</p>
          </div>

          <div class="form-group">
            <label for="bid-amount">Your Bid Amount (kr)</label>
            <input
              id="bid-amount"
              type="number"
              v-model.number="bidAmount"
              min="0"
              required
              class="bid-input"
            />
            <small v-if="!bidAmountValid" class="error-text">
              Bid amount must be 0 or more
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
              <span v-else>Place Bid</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { placeBidOnItem } from '@/services/BidService';
import type { BidCreatePayload } from '@/models/Bid';
import { useUserStore } from '@/stores/UserStore';

/**
 * Component props
 */
const props = defineProps<{
  isOpen: boolean;
  itemId: string | number;
  itemTitle: string;
  currentPrice: number | null;
}>();

/**
 * Component emits
 */
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'bid-placed'): void;
}>();

// Form state
const bidAmount = ref<number>(0);
const bidComment = ref<string>('');
const loading = ref<boolean>(false);
const bidStatus = ref<'idle' | 'loading' | 'success' | 'error'>('idle');
const errorMessage = ref<string>('');

// Set initial bid amount slightly higher than current price
// Set initial bid amount to match current price
onMounted(() => {
  if (props.currentPrice) {
    // Start with a bid equal to the current price
    bidAmount.value = props.currentPrice;
  } else {
    bidAmount.value = 0;
  }
});

// Close modal with escape key
const handleEscKey = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && props.isOpen) {
    closeModal();
  }
};

onMounted(() => {
  document.addEventListener('keydown', handleEscKey);
});

onUnmounted(() => {
  document.removeEventListener('keydown', handleEscKey);
});

// Computed properties
const bidAmountValid = computed(() => {
  return bidAmount.value >= 0;
});

const formattedPrice = computed(() => {
  if (props.currentPrice === null || props.currentPrice === undefined) {
    return 'Price not specified';
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
}

async function submitBid() {
  if (!bidAmountValid.value) return;

  // Get the user store to access token
  const userStore = useUserStore();

  // Check if user is still logged in
  if (!userStore.loggedIn) {
    bidStatus.value = 'error';
    errorMessage.value = 'You must be logged in to place a bid. Please log in and try again.';
    return;
  }

  bidStatus.value = 'loading';
  loading.value = true;

  const bidData: BidCreatePayload = {
    amount: bidAmount.value,
    comment: bidComment.value.trim() || undefined,
  };

  try {
    await placeBidOnItem(props.itemId, bidData);
    bidStatus.value = 'success';
    emit('bid-placed');
  } catch (error) {
    bidStatus.value = 'error';
    errorMessage.value = error instanceof Error
      ? error.message
      : 'Failed to place bid. Please try again.';
    console.error('Error placing bid:', error);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.3rem;
  color: #333;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #888;
}

.close-button:hover {
  color: #333;
}

.modal-body {
  padding: 1.5rem;
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
}

.current-price {
  font-weight: 600;
  color: #007bff;
  margin: 0;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 500;
  color: #555;
}

.bid-input,
.comment-input {
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.bid-input:focus,
.comment-input:focus {
  border-color: #007bff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.error-text {
  color: #dc3545;
  font-size: 0.85rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.8rem;
  margin-top: 1rem;
}

.cancel-button,
.submit-button,
.close-success-button,
.try-again-button {
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-button {
  background-color: #f8f9fa;
  border: 1px solid #ddd;
  color: #555;
}

.cancel-button:hover {
  background-color: #e9ecef;
}

.submit-button {
  background-color: #007bff;
  border: none;
  color: white;
  font-weight: 500;
}

.submit-button:hover:not(:disabled) {
  background-color: #0069d9;
}

.submit-button:disabled {
  background-color: #74b1f9;
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
}

.bid-success,
.bid-error {
  text-align: center;
  padding: 1.5rem 1rem;
}

.success-icon,
.error-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  margin: 0 auto 1rem;
  font-size: 1.8rem;
}

.success-icon {
  background-color: #28a745;
  color: white;
}

.error-icon {
  background-color: #dc3545;
  color: white;
}

.close-success-button {
  background-color: #28a745;
  border: none;
  color: white;
  margin-top: 1rem;
}

.close-success-button:hover {
  background-color: #218838;
}

.try-again-button {
  background-color: #dc3545;
  border: none;
  color: white;
  margin-top: 1rem;
}

.try-again-button:hover {
  background-color: #c82333;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 480px) {
  .form-actions {
    flex-direction: column;
  }

  .cancel-button,
  .submit-button {
    width: 100%;
  }
}
</style>
