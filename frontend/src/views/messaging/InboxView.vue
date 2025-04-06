<script setup lang="ts">
/**
 * @fileoverview Inbox View Component
 *
 * This component displays a list of message conversations for the user.
 * It handles fetching conversations, displaying conversation previews,
 * and navigating to individual conversation threads.
 *
 * Features:
 * - Real-time message notifications
 * - Conversation list with unread indicators
 * - Item preview images
 * - Error handling for API failures
 */

import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import type { ConversationPreview, Message } from '@/models/Message'
import {
  fetchConversations,
  initializeMessaging,
  onNewMessage,
  removeMessageHandler,
} from '@/services/MessageService'

const router = useRouter()

/**
 * Reactive reference to the list of conversation previews
 * @type {Ref<ConversationPreview[]>}
 */
const conversations = ref<ConversationPreview[]>([])

/**
 * Indicates whether conversations are currently being loaded
 * @type {Ref<boolean>}
 */
const loading = ref(false)

/**
 * Stores any error message that occurs during data fetching
 * @type {Ref<string|null>}
 */
const error = ref<string | null>(null)

/**
 * Event handler for new incoming messages
 * Updates the conversation list when a new message is received
 *
 * @param {Message} message - The newly received message object
 */
const handleNewMessage = (message: Message) => {
  console.log('New message received:', message.messageContent)
  refreshConversations()
}

/**
 * Navigates to a specific conversation thread
 *
 * @param {string|number} conversationId - The ID of the conversation to navigate to
 */
function goToConversation(conversationId: string | number) {
  router.push(`/messages/${conversationId}`)
}

/**
 * Truncates text to the specified maximum length and adds an ellipsis if needed
 *
 * @param {string|undefined|null} text - The text to truncate
 * @param {number} maxLength - Maximum length of the returned string (default: 50)
 * @returns {string} The truncated text
 */
function truncate(text: string | undefined | null, maxLength = 50): string {
  if (!text) return 'No content';
  return text.length > maxLength ? `${text.substring(0, maxLength)}...` : text;
}

/**
 * Fetches the latest conversation list from the API
 * Updates the reactive references with the result or error
 */
async function refreshConversations() {
  loading.value = true
  error.value = null
  try {
    conversations.value = await fetchConversations()
  } catch (err) {
    console.error('InboxView: Failed to fetch conversations:', err)
    error.value = 'Could not load conversations.'
    conversations.value = []
  } finally {
    loading.value = false
  }
}

/**
 * Component lifecycle hook: called when the component is mounted
 * Initializes messaging service, sets up event listeners, and loads initial data
 */
onMounted(async () => {
  try {
    await initializeMessaging()
    onNewMessage(handleNewMessage)
    await refreshConversations()
  } catch (err) {
    console.error('Failed to initialize messaging:', err)
    error.value = 'Connection error. Please refresh.'
  }
})

/**
 * Component lifecycle hook: called when the component is unmounted
 * Cleans up event listeners to prevent memory leaks
 */
onUnmounted(() => {
  removeMessageHandler(handleNewMessage)
})
</script>

<template>
  <div class="inbox-view">
    <!-- Page title - uses i18n translation -->
    <h1>{{ $t('INBOX') }}</h1>

    <!-- Loading state -->
    <div v-if="loading" class="loading">Loading conversations...</div>

    <!-- Error state -->
    <div v-else-if="error" class="error">{{ error }}</div>

    <!-- Empty state -->
    <div v-else-if="conversations.length === 0" class="empty-inbox">
      {{ $t('NO_CONVERSATIONS_YET') }}
    </div>

    <!-- Conversation list -->
    <ul v-else class="conversation-list">
      <li
        v-for="convo in conversations"
        :key="convo.id"
        @click="goToConversation(convo.id)"
        class="conversation-item"
        :class="{ 'has-unread': convo.unreadMessagesCount > 0 }"
      >
        <!-- Item image or placeholder -->
        <div class="item-image-container">
          <img
            v-if="convo.item && convo.item.imageUrl"
            :src="convo.item.imageUrl"
            alt="Item image"
            class="item-preview-image"
          >
          <div v-else class="item-image-placeholder">?</div>
        </div>

        <!-- Conversation details -->
        <div class="convo-details">
          <span class="other-user">{{ convo.otherUser.username }}</span>
          <span v-if="convo.item" class="related-item">Item: {{ convo.item.title || 'N/A' }}</span>
          <p class="last-message-snippet">
            {{ convo.lastMessage ? truncate(convo.lastMessage.content || convo.lastMessage.messageContent) : 'No messages yet' }}
          </p>
        </div>

        <!-- Unread message counter badge -->
        <div v-if="convo.unreadMessagesCount > 0" class="unread-badge">
          {{ convo.unreadMessagesCount }}
        </div>
      </li>
    </ul>
  </div>
</template>

<style scoped>
/**
 * Component layout - centers content with maximum width
 */
.inbox-view {
  padding: 0.7rem;
  max-width: 700px;
  margin: 0 auto;
}

/**
 * Reset list styles
 */
.conversation-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

/**
 * Conversation item styling
 * Uses gradient background with rounded corners
 */
.conversation-item {
  background: linear-gradient(to bottom, #ffffff, #dedede);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-top: 1px solid snow;
  border-bottom: 1px solid gray;
  border-radius: 15px;
  cursor: pointer;
  transition: background-color 0.5s ease;
  margin-bottom: 0.5rem;
}

/**
 * Hover state for conversation items
 */
.conversation-item:hover {
  background: linear-gradient(to bottom, #ffffff, #b5b5b5);
}

/**
 * Container for item preview image
 */
.item-image-container {
  flex-shrink: 0;
  margin-right: 1rem;
}

/**
 * Item preview image styling
 */
.item-preview-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 8px;
  background-color: #eee;
}

/**
 * Placeholder for when item has no image
 */
.item-image-placeholder {
  width: 50px;
  height: 50px;
  background-color: #ccc;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.5em;
  color: #fff;
}

/**
 * Container for conversation text details
 */
.convo-details {
  flex-grow: 1;
  overflow: hidden;
}

/**
 * Username styling
 */
.other-user {
  font-weight: bold;
  margin-right: 0.5rem;
  display: block;
}

/**
 * Related item title styling
 */
.related-item {
  font-size: 0.9em;
  color: #555;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/**
 * Last message preview styling
 */
.last-message-snippet {
  font-size: 0.9em;
  color: #777;
  margin: 0.25rem 0 0 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/**
 * Badge for unread message count
 */
.unread-badge {
  background-color: red;
  color: white;
  font-size: 0.8em;
  font-weight: bold;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  margin-left: 1rem;
}

/**
 * Special styling for conversations with unread messages
 */
.conversation-item.has-unread .other-user {
  font-weight: bold;
}
.conversation-item.has-unread .last-message-snippet {
  font-weight: normal;
}

/**
 * Status message styling (loading, error, empty)
 */
.loading,
.error,
.empty-inbox {
  text-align: center;
  padding: 2rem;
  color: #777;
}

/**
 * Error message specific styling
 */
.error {
  color: red;
}
</style>
