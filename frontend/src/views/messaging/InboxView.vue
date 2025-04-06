<script setup lang="ts">
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
const conversations = ref<ConversationPreview[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

const handleNewMessage = (message: Message) => {
  console.log('New message received:', message.messageContent)
  refreshConversations()
}

function goToConversation(conversationId: string | number) {
  router.push(`/messages/${conversationId}`)
}

function truncate(text: string | undefined | null, maxLength = 50): string {
  if (!text) return 'No content';
  return text.length > maxLength ? `${text.substring(0, maxLength)}...` : text;
}

async function refreshConversations() {
  loading.value = true
  error.value = null
  try {
    conversations.value = await fetchConversations() // TODO: remove test prints
    for (const convo in conversations.value) {
      console.log('Conversation:', conversations.value[convo])
    }
  } catch (err) {
    console.error('InboxView: Failed to fetch conversations:', err)
    error.value = 'Could not load conversations.'
    conversations.value = []
  } finally {
    loading.value = false
  }
}

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

onUnmounted(() => {
  removeMessageHandler(handleNewMessage)
})
</script>

<template>
  <div class="inbox-view">
    <h1>{{ $t('INBOX') }}</h1>
    <div v-if="loading" class="loading">Loading conversations...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="conversations.length === 0" class="empty-inbox">
      {{ $t('NO_CONVERSATIONS_YET') }}
    </div>
    <ul v-else class="conversation-list">
      <li
        v-for="convo in conversations"
        :key="convo.id"
        @click="goToConversation(convo.id)"
        class="conversation-item"
        :class="{ 'has-unread': convo.unreadMessagesCount > 0 }"
      >
        <div class="convo-details">
          <span class="other-user">{{ convo.otherUser.username }}</span>
          <span v-if="convo.relatedItem" class="related-item"
            >Item: {{ convo.relatedItem.title }}</span
          >
          <p class="last-message-snippet">
            {{ convo.lastMessage ? truncate(convo.lastMessage.messageContent) : 'No messages yet' }}
          </p>
        </div>
        <div v-if="convo.unreadMessagesCount > 0" class="unread-badge">
          {{ convo.unreadMessagesCount }}
        </div>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.inbox-view {
  padding: 0.7rem;
}

.conversation-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.conversation-item {
  background: linear-gradient(to bottom, #ffffff, #dedede);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-top: 1px solid snow;
  border-bottom: 1px solid gray;
  border-radius: 20px;
  cursor: pointer;
  transition: background-color 0.5s ease;
  max-width: 600px;
}

.conversation-item:hover {
  background: linear-gradient(to bottom, #ffffff, #b5b5b5);
}

.convo-details {
  flex-grow: 1;
  margin-right: 1rem;
}

.other-user {
  font-weight: bold;
  margin-right: 0.5rem;
}

.related-item {
  font-size: 0.9em;
  color: #555;
}

.last-message-snippet {
  font-size: 0.9em;
  color: #777;
  margin: 0.25rem 0 0 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

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
}

.conversation-item.has-unread .other-user,
.conversation-item.has-unread .last-message-snippet {
  font-weight: bold;
}

.loading,
.error,
.empty-inbox {
  text-align: center;
  padding: 2rem;
  color: #777;
}

.error {
  color: red;
}
</style>
