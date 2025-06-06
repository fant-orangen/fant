<template>
  <div class="conversation-view">
    <!-- Header section with navigation, user, and item info -->
    <div class="header">
      <!-- Top title bar with back button and chat partner name -->
      <div class="title-bar">
        <router-link to="/messages" class="back-link"> &lt; {{ $t('BACK_TO_INBOX') }} </router-link>
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
          <router-link
            :to="{ name: 'item-detail', params: { id: item.id } }"
            class="item-title-link"
          >
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
      <div v-if="!loading && messages.length === 0 && !error" class="empty-chat">
        Start the conversation!
      </div>

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
      <button class="edit-button"
              @click="handleSendMessage"
              :disabled="!newMessageContent.trim() || sending || loading || !!error"
      >
        {{ $t('SEND') }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * Conversation View component.
 *
 * A comprehensive real-time messaging interface that allows users to view and send messages
 * related to a specific marketplace item. This component handles loading conversation history,
 * sending new messages, and receiving real-time updates through WebSockets.
 *
 * Features:
 * - Paginated loading of message history with infinite scroll
 * - Real-time message receiving using WebSocket communication
 * - Display of conversation context including item details and chat partner
 * - Message status indicators (sent/received)
 * - Responsive design with mobile-friendly UI
 * - Automatic scrolling to latest messages
 * - Message timestamp display
 * - Error handling for network issues
 * - Automatic marking of messages as read
 *
 * The component uses the MessageService for communication with the backend API and
 * manages conversation state including loading older messages when scrolling up.
 *
 * @component ConversationView
 * @requires vue
 * @requires vue-router
 * @requires vue-i18n
 * @requires @/services/MessageService
 * @requires @/services/UserService
 * @requires @/models/Message
 * @requires @/models/Item
 */
import { ref, onMounted, onUnmounted, computed, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import type { Message, MessageUser } from '@/models/Message'
import type { PaginatedMessageResponse } from '@/models/Message'
import type { ItemPreviewType } from '@/models/Item'
import {
  fetchPagedMessages,
  sendMessage,
  initializeMessaging,
  onNewMessage,
  removeMessageHandler,
  fetchConversations,
  readMessages,
} from '@/services/MessageService'
import { fetchCurrentUserId } from '@/services/UserService'
import '@/assets/styles/buttons/buttons.css'
const route = useRoute()

const messages = ref<Message[]>([])
const newMessageContent = ref('')
const otherUser = ref<MessageUser | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)
const sending = ref(false)
const messagesContainerRef = ref<HTMLElement | null>(null)
const currentUserId = ref<string | number | null>(null)
const item = ref<ItemPreviewType | null>(null)

const pageSize = 12
const currentPage = ref(0)
const totalPages = ref(1)
const fetchingOlder = ref(false)

const conversationId = computed(() => route.params.conversationId as string)


/**
 * Handles incoming messages from WebSocket connection.
 * Adds new messages to the conversation if they're relevant to the current chat
 * and not already in the message list.
 *
 * @param {Message} message - The new message received from WebSocket
 * @returns {void}
 */
const handleNewMessage = (message: Message) => {
  const isRelevantMessage =
    (message.sender.id === otherUser.value?.id || message.receiver.id === otherUser.value?.id) &&
    message.item?.id === item.value?.id

  const isNewMessage = !messages.value.some((m) => m.id === message.id)

  if (isRelevantMessage && isNewMessage) {
    messages.value.push(message)
    scrollToBottom()
  }
}

/**
 * Fetches conversation details including other user info and item details.
 * Sets error state if conversation cannot be found or has no associated item.
 *
 * @returns {Promise<boolean>} - Returns true if conversation details were loaded successfully
 */
async function getConversationDetails(): Promise<boolean> {
  try {
    const conversations = await fetchConversations()
    const currentConversation = conversations.find((c) => c.id.toString() === conversationId.value)

    if (!currentConversation) {
      error.value = 'Conversation not found.'
      item.value = null
      return false
    }

    if (currentConversation.item) {
      item.value = currentConversation.item
    } else {
      error.value = 'No item associated with this conversation.'
      item.value = null
      return false
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
 * Loads a specific page of messages for the current conversation.
 * Prepends older messages to the message list and updates pagination state.
 *
 * @param {number} page - The page number to load (zero-based)
 * @returns {Promise<void>}
 */
async function loadMessagesPage(page: number) {
  if (!item.value?.id) return

  fetchingOlder.value = true
  try {
    const response: PaginatedMessageResponse = await fetchPagedMessages(
      item.value.id,
      page,
      pageSize,
      'sentAt,desc',
    )
    const newMessages = response.content.map((m) => ({ ...m, sentDate: new Date(m.sentDate) }))
    messages.value = [...newMessages.reverse(), ...messages.value] // prepend older messages
    totalPages.value = response.totalPages
    await readMessages(newMessages)
  } catch (err) {
    console.error('Failed to load messages:', err)
    error.value = 'Could not load messages.'
  } finally {
    fetchingOlder.value = false
  }
}

/**
 * Sends a new message to the conversation partner.
 * Adds the sent message to the local message list and clears the input field.
 *
 * @returns {Promise<void>}
 */
async function handleSendMessage() {
  if (!newMessageContent.value.trim() || sending.value || !otherUser.value?.id || !item.value?.id)
    return

  sending.value = true
  error.value = null

  try {
    const recipientId = otherUser.value.id
    const tempMessage = await sendMessage(recipientId, newMessageContent.value, item.value.id)

    messages.value.push(tempMessage)
    newMessageContent.value = ''
    scrollToBottom()
  } catch (err) {
    console.error('Failed to send message:', err)
    error.value = 'Failed to send message.'
  } finally {
    sending.value = false
  }
}

/**
 * Initializes the conversation by loading details and first page of messages.
 * Resets conversation state and handles error conditions.
 *
 * @returns {Promise<void>}
 */
async function initializeConversation() {
  loading.value = true
  error.value = null
  messages.value = []
  item.value = null
  otherUser.value = null
  currentPage.value = 0

  try {
    currentUserId.value = await fetchCurrentUserId()
    const success = await getConversationDetails()
    if (!success) {
      loading.value = false
      return
    }
    await loadMessagesPage(currentPage.value)
    await nextTick()
    scrollToBottom()
    maybeLoadMoreIfNotScrollable() // 👈 Add this
  } catch (err) {
    console.error('Failed to initialize conversation:', err)
    error.value = 'Error loading conversation. Please try again.'
  } finally {
    loading.value = false
  }
}

/**
 * Loads more messages if the initial content doesn't fill the scroll container.
 * Prevents empty space when conversation doesn't have enough messages to scroll.
 *
 * @returns {void}
 */
function maybeLoadMoreIfNotScrollable() {
  const container = messagesContainerRef.value
  if (
    container &&
    container.scrollHeight <= container.clientHeight &&
    currentPage.value < totalPages.value - 1
  ) {
    currentPage.value++
    loadMessagesPage(currentPage.value)
  }
}

/**
 * Scrolls the message container to the bottom to show the latest messages.
 * Uses nextTick to ensure DOM updates have completed before scrolling.
 *
 * @returns {void}
 */
function scrollToBottom() {
  nextTick(() => {
    const container = messagesContainerRef.value
    if (container) {
      container.scrollTop = container.scrollHeight
    }
  })
}

/**
 * Determines if a message was sent by the current user.
 * Used to style messages differently based on sender.
 *
 * @param {Message} message - The message to check
 * @returns {boolean} - True if the message was sent by the current user
 */
function isMyMessage(message: Message): boolean {
  return message.sender.id.toString() === currentUserId.value?.toString()
}

/**
 * Formats a timestamp into a readable time format (hours:minutes).
 *
 * @param {string|Date} date - The timestamp to format
 * @returns {string} - Formatted time string
 */
function formatTimestamp(date: string | Date): string {
  if (!date) return ''
  const d = typeof date === 'string' ? new Date(date) : date
  return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

/**
 * Handles scroll events in the message container.
 * Loads older messages when user scrolls to the top of the container.
 *
 * @returns {void}
 */
function handleScroll() {
  const container = messagesContainerRef.value
  if (
    container &&
    container.scrollTop < 50 &&
    !fetchingOlder.value &&
    currentPage.value < totalPages.value - 1
  ) {
    currentPage.value++
    loadMessagesPage(currentPage.value)
  }
}

onMounted(async () => {
  try {
    await initializeMessaging()
    onNewMessage(handleNewMessage)
    await initializeConversation()
    if (messagesContainerRef.value) {
      messagesContainerRef.value.addEventListener('scroll', handleScroll)
    }
  } catch (err) {
    console.error('Failed to initialize messaging:', err)
    error.value = 'Connection error. Please refresh.'
  }
})

onUnmounted(() => {
  removeMessageHandler(handleNewMessage)
  if (messagesContainerRef.value) {
    messagesContainerRef.value.removeEventListener('scroll', handleScroll)
  }
})

watch(conversationId, async (newId, oldId) => {
  if (newId !== oldId) {
    await initializeConversation()
  }
})
</script>

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
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.08);
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
  color: var(--vt-c-teal-soft); /* Primary color for links */
  text-decoration: none;
  white-space: nowrap;
  flex-shrink: 0;
}

.back-link:hover {
  color: var(--vt-c-teal-dark);
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
  background: none;
}

.item-title-link:hover h3 {
  color: var(--vt-c-teal-dark); /* Teal accent */
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
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/**
 * Sent message styling (green gradient)
 */
  .message-bubble-wrapper.sent .message-bubble {
    background: linear-gradient(to bottom, var(--vt-c-teal-soft), var(--vt-c-teal-dark));
    color: var(--vt-c-white);
    border-bottom-right-radius: 5px;
  }

/**
 * Received message styling (blue gradient)
 */
  .message-bubble-wrapper.received .message-bubble {
    background: linear-gradient(to bottom, var(--vt-c-white-soft), var(--vt-c-white-mute));
    color: var(--vt-c-indigo);
    border: 1px solid var(--vt-c-teal-light);
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
    color: var(--vt-c-white-soft);
  }

/**
 * Color adjustment for timestamp on received messages
 */
  .message-bubble-wrapper.received .message-timestamp {
    color: var(--vt-c-text-light-2);
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
    outline: none; /* Remove default focus outline */
  }

  /**
   * Focus state for textarea
   */
  .message-input-area textarea:focus {
    border-color: var(--vt-c-teal-soft);
    box-shadow: 0 0 0 2px var(--vt-c-teal-divider-light);
    outline: none;
  }
/**
 * Send button hover state
 */
.message-input-area button:hover:not(:disabled) {
  background-color: var(--vt-c-teal-light); /* Darker teal for hover */
}

  .message-input-area button {
    background-color: var(--vt-c-teal-soft);
    color: var(--vt-c-white);
    border: none;
    border-radius: 20px;
    padding: 0.6rem 1.2rem;
    cursor: pointer;
    font-weight: 500;
    transition: background-color 0.2s ease;
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
