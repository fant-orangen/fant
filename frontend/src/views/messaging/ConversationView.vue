<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { Message, MessageUser, ConversationPreview } from '@/models/Message'
import {
  fetchMessages,
  sendMessage,
  initializeMessaging,
  onNewMessage,
  removeMessageHandler,
  fetchConversations
} from '@/services/MessageService'
import { fetchCurrentUserId } from '@/services/UserService.ts'

const route = useRoute()
const router = useRouter()
const messages = ref<Message[]>([])
const newMessageContent = ref('')
const otherUser = ref<MessageUser | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)
const sending = ref(false)
const messagesContainerRef = ref<HTMLElement | null>(null)
const currentUserId = ref<string | number | null>(null)
const itemId = ref<string | number | null>(null)

const conversationId = computed(() => route.params.conversationId as string)

// Handler for new messages
const handleNewMessage = (message: Message) => {
  // Only add message if it's part of this conversation
  const isRelevantMessage =
    (message.sender.id === otherUser.value?.id || message.receiver.id === otherUser.value?.id) &&
    message.item?.id === itemId.value

  const isNewMessage = !messages.value.some((m) => m.id === message.id)

  if (isRelevantMessage && isNewMessage) {
    messages.value.push(message)
    scrollToBottom()
  }
}

// First fetch the conversation details to get the itemId
async function getConversationDetails(): Promise<boolean> {
  try {
    const conversations = await fetchConversations()
    const currentConversation = conversations.find(c => c.id.toString() === conversationId.value)

    if (!currentConversation) {
      error.value = 'Conversation not found.'
      return false
    }

    // Set the item ID from the conversation
    if (currentConversation.relatedItem?.id) {
      itemId.value = currentConversation.relatedItem.id
    } else if (currentConversation.item?.id) {
      itemId.value = currentConversation.item.id
    } else {
      error.value = 'No item associated with this conversation.'
      return false
    }

    // Set the other user
    otherUser.value = currentConversation.otherUser

    return true
  } catch (err) {
    console.error('Failed to fetch conversation details:', err)
    error.value = 'Could not load conversation details.'
    return false
  }
}

// Fetch messages using the itemId
async function loadMessages() {
  if (!itemId.value) {
    error.value = 'No item associated with this conversation.'
    return
  }

  loading.value = true
  error.value = null

  try {
    messages.value = await fetchMessages(itemId.value)
    console.log("Fetched messages for itemId:", itemId.value)
    scrollToBottom()
  } catch (err) {
    console.error(`Failed to fetch messages:`, err)
    error.value = 'Could not load messages.'
    messages.value = []
  } finally {
    loading.value = false
  }
}

async function handleSendMessage() {
  if (!newMessageContent.value.trim() || sending.value || !otherUser.value?.id || !itemId.value) return

  sending.value = true
  error.value = null

  try {
    const recipientId = otherUser.value.id
    const tempMessage = await sendMessage(recipientId, newMessageContent.value, itemId.value)

    // Add temporary message to UI immediately
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

// Initialize everything
async function initializeConversation() {
  try {
    currentUserId.value = await fetchCurrentUserId()

    // Get conversation details first
    const success = await getConversationDetails()
    if (!success) return

    // Then load messages using the item ID
    await loadMessages()
  } catch (err) {
    console.error('Failed to initialize conversation:', err)
    error.value = 'Error loading conversation. Please try again.'
  }
}

onMounted(async () => {
  try {
    // Initialize WebSocket first
    await initializeMessaging()

    // Register handler for new messages
    onNewMessage(handleNewMessage)

    // Initialize conversation data
    await initializeConversation()
  } catch (err) {
    console.error('Failed to initialize messaging:', err)
    error.value = 'Connection error. Please refresh.'
  }
})

onUnmounted(() => {
  removeMessageHandler(handleNewMessage)
})

// Watch for route changes
watch(conversationId, async () => {
  await initializeConversation()
})

function isMyMessage(message: Message): boolean {
  return message.sender.id.toString() === currentUserId.value?.toString()
}

function formatTimestamp(date: string | Date): string {
  if (!date) return ''
  const d = typeof date === 'string' ? new Date(date) : date
  return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

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
    <div class="header">
      <router-link to="/messages">{{ $t('BACK_TO_INBOX') }}</router-link>
      <h2 v-if="otherUser">{{ $t('CHAT_WITH') }}{{ otherUser.username }}</h2>
    </div>

    <div v-if="loading" class="loading">{{ $t('LOADING_MESSAGES') }}</div>
    <div v-else-if="error" class="error">{{ error }}</div>

    <div v-else class="messages-container" ref="messagesContainerRef">
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

    <div class="message-input-area">
      <textarea
        v-model="newMessageContent"
        :placeholder="$t('TYPE_MESSAGE')"
        @keyup.enter.prevent="handleSendMessage"
        :disabled="sending"
      ></textarea>
      <button @click="handleSendMessage" :disabled="!newMessageContent.trim() || sending">
        {{ $t('SEND') }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.conversation-view {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
  border: 1px solid #eee;
  margin: 1rem;
  border-radius: 8px;
  overflow: hidden;
  max-width: 800px;
}

.header {
  padding: 0.5rem 1rem;
  border-bottom: 1px solid #eee;
  background-color: #f9f9f9;
}

.header h2 {
  margin: 0;
  font-size: 1.1em;
  text-align: center;
}

.messages-container {
  flex-grow: 1;
  overflow-y: auto;
  padding: 1rem;
  background-color: #f0f0f0;
}

.message-bubble-wrapper {
  display: flex;
  margin-bottom: 0.5rem;
}

.message-bubble-wrapper.sent {
  justify-content: flex-end;
}

.message-bubble-wrapper.received {
  justify-content: flex-start;
}

.message-bubble {
  max-width: 70%;
  padding: 0.4rem 0.6rem;
  border-radius: 10px;
  position: relative;
}

.message-bubble-wrapper.sent .message-bubble {
  background: linear-gradient(to bottom, #20a830, #187f27);
  color: white;
  border-bottom-right-radius: 5px;
}

.message-bubble-wrapper.received .message-bubble {
  background: linear-gradient(to bottom, #72b1d6, #5d9bb6);
  color: black;
  border-bottom-left-radius: 5px;
}

.message-content {
  margin: 0;
  word-wrap: break-word;
}

.message-timestamp {
  font-size: 0.7em;
  color: #021b1c;
  display: block;
  text-align: right;
  margin-top: 0.2rem;
}

.message-bubble-wrapper.sent .message-timestamp {
  color: #e0e0e0;
}

.message-input-area {
  display: flex;
  padding: 0.5rem;
  border-top: 1px solid #eee;
  background-color: #f9f9f9;
}

.message-input-area textarea {
  flex-grow: 1;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 15px;
  resize: none;
  margin-right: 0.5rem;
  min-height: 40px;
  max-height: 100px;
}

.message-input-area button {
  padding: 0.5rem 1rem;
  border-radius: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}

.message-input-area button:hover {
  background-color: #0056b3;
  transition: background-color 0.5s ease;
}

.message-input-area button:disabled {
  background-color: #a0a0a0;
  cursor: not-allowed;
}

.loading,
.error {
  text-align: center;
  padding: 2rem;
  color: #777;
}

.error {
  color: red;
}
</style>
