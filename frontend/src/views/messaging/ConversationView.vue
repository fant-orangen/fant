<script setup lang="ts">
/**
 * @fileoverview Conversation View Component
 *
 * This component displays a messaging interface for a single conversation between users.
 * It provides real-time messaging, message history, and shows context about the item being discussed.
 *
 * Features:
 * - Real-time message updates
 * - Message sending and display
 * - User and item context information
 * - Automatic scrolling to latest messages
 * - Responsive design with distinct styling for sent vs received messages
 */

import { ref, onMounted, onUnmounted, computed, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import type { Message, MessageUser} from '@/models/Message'
import type { ItemPreviewType } from '@/models/Item'
import {
  fetchMessages,
  sendMessage,
  initializeMessaging,
  onNewMessage,
  removeMessageHandler,
  fetchConversations, readMessages
} from '@/services/MessageService'
import { fetchCurrentUserId } from '@/services/UserService'

const route = useRoute()

/**
 * Array of messages in the current conversation
 * @type {Ref<Message[]>}
 */
const messages = ref<Message[]>([])

/**
 * Content of the new message being composed by the user
 * @type {Ref<string>}
 */
const newMessageContent = ref('')

/**
 * The other user in this conversation
 * @type {Ref<MessageUser|null>}
 */
const otherUser = ref<MessageUser | null>(null)

/**
 * Indicates if messages are currently being loaded
 * @type {Ref<boolean>}
 */
const loading = ref(false)

/**
 * Stores any error message that occurs during operations
 * @type {Ref<string|null>}
 */
const error = ref<string | null>(null)

/**
 * Indicates if a message is currently being sent
 * @type {Ref<boolean>}
 */
const sending = ref(false)

/**
 * Reference to the messages container DOM element for scrolling
 * @type {Ref<HTMLElement|null>}
 */
const messagesContainerRef = ref<HTMLElement | null>(null)

/**
 * ID of the current user
 * @type {Ref<string|number|null>}
 */
const currentUserId = ref<string | number | null>(null)

/**
 * Item that is the subject of this conversation
 * @type {Ref<ItemPreviewType|null>}
 */
const item = ref<ItemPreviewType | null>(null)

/**
 * The ID of the current conversation, derived from route parameters
 * @type {ComputedRef<string>}
 */
const conversationId = computed(() => route.params.conversationId as string)

/**
 * Handles new incoming messages from the messaging service
 * Adds relevant messages to the conversation and scrolls to bottom
 *
 * @param {Message} message - The newly received message
 */
const handleNewMessage = (message: Message) => {
  // Check if message belongs to this conversation
  const isRelevantMessage =
    (message.sender.id === otherUser.value?.id || message.receiver.id === otherUser.value?.id) &&
    message.item?.id === item.value?.id;

  // Check if message is not already in the list
  const isNewMessage = !messages.value.some((m) => m.id === message.id)

  if (isRelevantMessage && isNewMessage) {
    messages.value.push(message)
    scrollToBottom()
  }
}

/**
 * Fetches conversation details including the other user and item information
 *
 * @returns {Promise<boolean>} Success status of the operation
 */
async function getConversationDetails(): Promise<boolean> {
  try {
    const conversations = await fetchConversations()
    const currentConversation = conversations.find(c => c.id.toString() === conversationId.value)

    if (!currentConversation) {
      error.value = 'Conversation not found.'
      item.value = null
      return false
    }

    if (currentConversation.item) {
      item.value = currentConversation.item;
    } else {
      error.value = 'No item associated with this conversation.'
      item.value = null
      return false;
    }

    otherUser.value = currentConversation.otherUser

    return true
  } catch (err) {
    console.error('Failed to fetch conversation details:', err)
    error.value = 'Could not load conversation details.'
    item.value = null
    return false
  }
}

/**
 * Loads message history for the current item
 * Sorts messages by timestamp and scrolls to most recent
 */
async function loadMessages() {
  if (!item.value || !item.value.id) {
    error.value = 'No item associated with this conversation.'
    return
  }

  loading.value = true
  error.value = null

  try {
    const fetchedMessages = await fetchMessages(item.value.id)
    await readMessages(fetchedMessages)
    // Sort messages chronologically
    messages.value = fetchedMessages.sort((a, b) =>
      a.sentDate.getTime() - b.sentDate.getTime()
    )
    console.log("Fetched messages for itemId:", item.value.id)
    scrollToBottom()
  } catch (err) {
    console.error(`Failed to fetch messages:`, err)
    error.value = 'Could not load messages.'
    messages.value = []
  } finally {
    loading.value = false
  }
}

/**
 * Handles sending a new message
 * Validates input, sends message to server, and updates UI
 */
async function handleSendMessage() {
  // Validate required data before sending
  if (!newMessageContent.value.trim() || sending.value || !otherUser.value?.id || !item.value?.id) return

  sending.value = true
  error.value = null

  try {
    const recipientId = otherUser.value.id
    const tempMessage = await sendMessage(recipientId, newMessageContent.value, item.value.id)

    messages.value.push(tempMessage)
    newMessageContent.value = ''
    scrollToBottom()
  } catch (err) {
    console.error(`Failed to send message:`, err)
    error.value = 'Failed to send message.'
  } finally {
    sending.value = false
  }
}

/**
 * Initializes the conversation by loading user, conversation, and message data
 * Resets component state and loads fresh data
 */
async function initializeConversation() {
  loading.value = true;
  error.value = null;
  messages.value = [];
  item.value = null;
  otherUser.value = null;

  try {
    // Get current user ID for message ownership checks
    currentUserId.value = await fetchCurrentUserId()

    // Load conversation metadata
    const success = await getConversationDetails()
    if (!success) {
      loading.value = false;
      return
    }

    // Load message history
    await loadMessages()
  } catch (err) {
    console.error('Failed to initialize conversation:', err)
    error.value = 'Error loading conversation. Please try again.'
  } finally {
    loading.value = false;
  }
}

/**
 * Lifecycle hook - Component mount
 * Sets up messaging listeners and initializes conversation data
 */
onMounted(async () => {
  try {
    await initializeMessaging()
    onNewMessage(handleNewMessage)
    await initializeConversation()
  } catch (err) {
    console.error('Failed to initialize messaging:', err)
    error.value = 'Connection error. Please refresh.'
  }
})

/**
 * Lifecycle hook - Component unmount
 * Removes message event listeners to prevent memory leaks
 */
onUnmounted(() => {
  removeMessageHandler(handleNewMessage)
})

/**
 * Watches for changes in the conversation ID (route parameter)
 * Reinitializes the conversation when the ID changes
 */
watch(conversationId, async (newId, oldId) => {
  if (newId !== oldId) {
    await initializeConversation()
  }
})

/**
 * Determines if a message was sent by the current user
 *
 * @param {Message} message - The message to check
 * @returns {boolean} True if message was sent by current user
 */
function isMyMessage(message: Message): boolean {
  return message.sender.id.toString() === currentUserId.value?.toString()
}

/**
 * Formats a timestamp into a readable time string
 *
 * @param {string|Date} date - The date to format
 * @returns {string} Formatted time string (HH:MM)
 */
function formatTimestamp(date: string | Date): string {
  if (!date) return ''
  const d = typeof date === 'string' ? new Date(date) : date
  return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

/**
 * Scrolls the message container to the bottom
 * Uses Vue's nextTick to ensure DOM is updated first
 */
function scrollToBottom() {
  nextTick(() => {
    const container = messagesContainerRef.value
    if (container) {
      container.scrollTop = container.scrollHeight
    }
  })
}

</script>

<template>
  <div class="conversation-view">
    <!-- Header section with navigation, user, and item info -->
    <div class="header">
      <!-- Top title bar with back button and chat partner name -->
      <div class="title-bar">
        <router-link to="/messages" class="back-link">
          &lt; {{ $t('BACK_TO_INBOX') }}
        </router-link>
        <h2 v-if="otherUser" class="chat-partner-name">
          {{ $t('CHAT_WITH') }}{{ otherUser.displayName }}
        </h2>
        <span class="title-spacer"></span>
      </div>

      <!-- Item context section -->
      <div v-if="item" class="item-preview-header">
        <!-- Item image or placeholder -->
        <img
          v-if="item.imageUrl"
          :src="item.imageUrl"
          alt="Item image"
          class="item-preview-image-header"
        />
        <div v-else class="item-image-placeholder-header">?</div>

        <!-- Item details -->
        <div class="item-info-header">
          <router-link :to="{ name: 'item-detail', params: { id: item.id } }" class="item-title-link">
            <h3>{{ item.title }}</h3>
          </router-link>
          <p class="item-price-header">{{ $t('PRICE') }}: {{ item.price }} kr</p>
        </div>
      </div>
    </div>

    <!-- Loading and error states -->
    <div v-if="loading && !messages.length" class="loading">{{ $t('LOADING_MESSAGES') }}</div>
    <div v-if="error" class="error">{{ error }}</div>

    <!-- Messages container -->
    <div class="messages-container" ref="messagesContainerRef">
      <!-- Empty state message -->
      <div v-if="!loading && messages.length === 0 && !error" class="empty-chat">Start the conversation!</div>

      <!-- Message bubbles -->
      <div
        v-for="message in messages"
        :key="message.id"
        class="message-bubble-wrapper"
        :class="isMyMessage(message) ? 'sent' : 'received'"
      >
        <div class="message-bubble">
          <p class="message-content">{{ message.messageContent }}</p>
          <span class="message-timestamp">{{ formatTimestamp(message.sentDate) }}</span>
        </div>
      </div>
    </div>

    <!-- Message input area -->
    <div class="message-input-area">
      <textarea
        v-model="newMessageContent"
        :placeholder="$t('TYPE_MESSAGE')"
        @keyup.enter.prevent="handleSendMessage"
        :disabled="sending || loading || !!error"
      ></textarea>
      <button @click="handleSendMessage" :disabled="!newMessageContent.trim() || sending || loading || !!error">
        {{ $t('SEND') }}
      </button>
    </div>
  </div>
</template>

<style scoped>
/**
 * Main container styles
 * Full-height flex container with fixed width and border
 */
.conversation-view {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
  border: 1px solid #eee;
  margin: 0.5rem auto;
  border-radius: 8px;
  overflow: hidden;
  max-width: 800px;
  background-color: #fff;
}

/**
 * Header section styles
 */
.header {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #eee;
  background-color: #f9f9f9;
}

/**
 * Title bar layout
 * Uses flexbox to position back button, title, and spacer
 */
.title-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 0.75rem;
}

/**
 * Back navigation link
 */
.back-link {
  font-size: 0.9em;
  color: #007bff;
  text-decoration: none;
  white-space: nowrap;
  flex-shrink: 0;
}

.back-link:hover {
  text-decoration: underline;
}

/**
 * Chat partner name in header
 * Centered with flex grow
 */
.chat-partner-name {
  margin: 0;
  font-size: 1.2em;
  font-weight: bold;
  text-align: center;
  flex-grow: 1;
  padding: 0 0.5rem;
}

/**
 * Empty space to balance the back button
 * Creates symmetrical header layout
 */
.title-spacer {
  flex-basis: 80px;
  flex-shrink: 0;
  visibility: hidden;
}

/**
 * Item preview section in header
 * Shows context about the item being discussed
 */
.item-preview-header {
  display: flex;
  align-items: center;
  padding-top: 0.75rem;
  border-top: 1px solid #eee;
}

/**
 * Item image styling
 */
.item-preview-image-header {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 1rem;
  background-color: #eee;
  flex-shrink: 0;
}

/**
 * Placeholder for when item has no image
 */
.item-image-placeholder-header {
  width: 50px;
  height: 50px;
  background-color: #ccc;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.5em;
  color: #fff;
  margin-right: 1rem;
  flex-shrink: 0;
}

/**
 * Container for item text information
 */
.item-info-header {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex-grow: 1;
  overflow: hidden;
}

/**
 * Link to item detail page
 */
.item-title-link {
  text-decoration: none;
  color: #333;
}

.item-title-link:hover h3 {
  color: #007bff;
  text-decoration: underline;
}

/**
 * Item title styling
 * Truncates with ellipsis if too long
 */
.item-info-header h3 {
  margin: 0 0 0.2rem 0;
  font-size: 1em;
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/**
 * Item price styling
 */
.item-price-header {
  margin: 0;
  font-size: 0.9em;
  color: #555;
}

/**
 * Message display area
 * Scrollable container with flex-grow to fill available space
 */
.messages-container {
  flex-grow: 1;
  overflow-y: auto;
  padding: 1rem;
  background-color: #f0f0f0;
}

/**
 * Empty conversation state
 */
.empty-chat {
  text-align: center;
  color: #888;
  padding: 2rem;
  font-style: italic;
}

/**
 * Container for a single message bubble
 * Uses flex layout to position sent vs received messages
 */
.message-bubble-wrapper {
  display: flex;
  margin-bottom: 0.5rem;
}

/**
 * Alignment for sent messages (right-aligned)
 */
.message-bubble-wrapper.sent {
  justify-content: flex-end;
}

/**
 * Alignment for received messages (left-aligned)
 */
.message-bubble-wrapper.received {
  justify-content: flex-start;
}

/**
 * Individual message bubble styling
 */
.message-bubble {
  max-width: 70%;
  padding: 0.5rem 0.8rem;
  border-radius: 15px;
  position: relative;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

/**
 * Sent message styling (green gradient)
 */
.message-bubble-wrapper.sent .message-bubble {
  background: linear-gradient(to bottom, #20a830, #187f27);
  color: white;
  border-bottom-right-radius: 5px;
}

/**
 * Received message styling (blue gradient)
 */
.message-bubble-wrapper.received .message-bubble {
  background: linear-gradient(to bottom, #72b1d6, #5d9bb6);
  color: black;
  border-bottom-left-radius: 5px;
}

/**
 * Message text content styling
 */
.message-content {
  margin: 0 0 0.2rem 0;
  word-wrap: break-word;
  white-space: pre-wrap;
  font-size: 0.95em;
}

/**
 * Message timestamp styling
 */
.message-timestamp {
  font-size: 0.7em;
  opacity: 0.8;
  display: block;
  text-align: right;
  margin-top: 0.2rem;
  clear: both;
}

/**
 * Color adjustment for timestamp on sent messages
 */
.message-bubble-wrapper.sent .message-timestamp {
  color: #e0e0e0;
}

/**
 * Color adjustment for timestamp on received messages
 */
.message-bubble-wrapper.received .message-timestamp {
  color: #555;
}

/**
 * Message input area styling
 * Fixed at bottom of container
 */
.message-input-area {
  display: flex;
  align-items: center;
  padding: 0.75rem;
  border-top: 1px solid #eee;
  background-color: #f9f9f9;
}

/**
 * Text input field styling
 * Flexible width with rounded corners
 */
.message-input-area textarea {
  flex-grow: 1;
  padding: 0.6rem 0.8rem;
  border: 1px solid #ccc;
  border-radius: 20px;
  resize: none;
  margin-right: 0.75rem;
  min-height: 40px;
  max-height: 100px;
  line-height: 1.4;
  font-size: 0.95em;
}

/**
 * Send button styling
 */
.message-input-area button {
  padding: 0.6rem 1.2rem;
  border-radius: 20px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s ease;
}

/**
 * Send button hover state
 */
.message-input-area button:hover:not(:disabled) {
  background-color: #0056b3;
}

/**
 * Send button disabled state
 */
.message-input-area button:disabled {
  background-color: #a0a0a0;
  cursor: not-allowed;
}

/**
 * Loading and error states styling
 */
.loading,
.error {
  text-align: center;
  padding: 2rem;
  color: #777;
}

/**
 * Error message specific styling
 */
.error {
  color: red;
  font-weight: bold;
}
</style>
