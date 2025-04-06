<script setup lang="ts">
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
  fetchConversations
} from '@/services/MessageService'
import { fetchCurrentUserId } from '@/services/UserService'

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

const conversationId = computed(() => route.params.conversationId as string)

// Handler for new messages
const handleNewMessage = (message: Message) => {
  const isRelevantMessage =
    (message.sender.id === otherUser.value?.id || message.receiver.id === otherUser.value?.id) &&
    message.item?.id === item.value?.id;

  const isNewMessage = !messages.value.some((m) => m.id === message.id)

  if (isRelevantMessage && isNewMessage) {
    messages.value.push(message)
    scrollToBottom()
  }
}

// First fetch the conversation details to get the item
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

// Fetch messages using the item ID
async function loadMessages() {
  if (!item.value || !item.value.id) {
    error.value = 'No item associated with this conversation.'
    return
  }

  loading.value = true
  error.value = null

  try {
    const fetchedMessages = await fetchMessages(item.value.id)
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

async function handleSendMessage() {
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

async function initializeConversation() {
  loading.value = true;
  error.value = null;
  messages.value = [];
  item.value = null;
  otherUser.value = null;

  try {
    currentUserId.value = await fetchCurrentUserId()

    const success = await getConversationDetails()
    if (!success) {
      loading.value = false;
      return
    }

    await loadMessages()
  } catch (err) {
    console.error('Failed to initialize conversation:', err)
    error.value = 'Error loading conversation. Please try again.'
  } finally {
    loading.value = false;
  }
}

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

onUnmounted(() => {
  removeMessageHandler(handleNewMessage)
})

watch(conversationId, async (newId, oldId) => {
  if (newId !== oldId) {
    await initializeConversation()
  }
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
      <div class="title-bar">
        <router-link to="/messages" class="back-link">
          &lt; {{ $t('BACK_TO_INBOX') }} </router-link>
        <h2 v-if="otherUser" class="chat-partner-name">
          {{ $t('CHAT_WITH') }}{{ otherUser.displayName }}
        </h2>
        <span class="title-spacer"></span> </div>
      <div v-if="item" class="item-preview-header">
        <img
          v-if="item.imageUrl"
          :src="item.imageUrl"
          alt="Item image"
          class="item-preview-image-header"
        />
        <div v-else class="item-image-placeholder-header">?</div>

        <div class="item-info-header">
          <router-link :to="{ name: 'item-detail', params: { id: item.id } }" class="item-title-link">
            <h3>{{ item.title }}</h3>
          </router-link>
          <p class="item-price-header">{{ $t('PRICE') }}: {{ item.price }} kr</p>
        </div>
      </div>
    </div>

    <div v-if="loading && !messages.length" class="loading">{{ $t('LOADING_MESSAGES') }}</div>
    <div v-if="error" class="error">{{ error }}</div>
    <div class="messages-container" ref="messagesContainerRef">
      <div v-if="!loading && messages.length === 0 && !error" class="empty-chat">Start the conversation!</div>
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
        :disabled="sending || loading || !!error"
      ></textarea>
      <button @click="handleSendMessage" :disabled="!newMessageContent.trim() || sending || loading || !!error">
        {{ sending ? $t('SENDING') : $t('SEND') }}
      </button>
    </div>
  </div>
</template>

<style scoped>
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

.header {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #eee;
  background-color: #f9f9f9;
}

.title-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 0.75rem;
}

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

.chat-partner-name {
  margin: 0;
  font-size: 1.2em;
  font-weight: bold;
  text-align: center;
  flex-grow: 1;

  padding: 0 0.5rem;
}

.title-spacer {
  flex-basis: 80px;
  flex-shrink: 0;
  visibility: hidden;
}

.item-preview-header {
  display: flex;
  align-items: center;
  padding-top: 0.75rem;
  border-top: 1px solid #eee;
}

.item-preview-image-header {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 1rem;
  background-color: #eee;
  flex-shrink: 0;
}

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

.item-info-header {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex-grow: 1;
  overflow: hidden;
}

.item-title-link {
  text-decoration: none;
  color: #333;
}

.item-title-link:hover h3 {
  color: #007bff;
  text-decoration: underline;
}

.item-info-header h3 {
  margin: 0 0 0.2rem 0;
  font-size: 1em;
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-price-header {
  margin: 0;
  font-size: 0.9em;
  color: #555;
}

.messages-container {
  flex-grow: 1;
  overflow-y: auto;
  padding: 1rem;
  background-color: #f0f0f0;
}

.empty-chat {
  text-align: center;
  color: #888;
  padding: 2rem;
  font-style: italic;
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
  padding: 0.5rem 0.8rem;
  border-radius: 15px;
  position: relative;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
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
  margin: 0 0 0.2rem 0;
  word-wrap: break-word;
  white-space: pre-wrap;
  font-size: 0.95em;
}

.message-timestamp {
  font-size: 0.7em;
  opacity: 0.8;
  display: block;
  text-align: right;
  margin-top: 0.2rem;
  clear: both;
}

.message-bubble-wrapper.sent .message-timestamp {
  color: #e0e0e0;
}
.message-bubble-wrapper.received .message-timestamp {
  color: #555;
}

.message-input-area {
  display: flex;
  align-items: center;
  padding: 0.75rem;
  border-top: 1px solid #eee;
  background-color: #f9f9f9;
}

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

.message-input-area button:hover:not(:disabled) {
  background-color: #0056b3;
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
  font-weight: bold;
}
</style>
