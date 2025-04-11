<template>
  <div class="listing-form-container">
    <div class="listing-form">
      <h1 class="form-title">{{ $t('INBOX_NAME') }}</h1>
      <div class="form-divider"></div>

      <!-- Loading state -->
      <div v-if="loading" class="loading">{{ $t('LOADING_MESSAGES') }}</div>

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
          <div class="item-image-container">
            <img
              v-if="convo.item?.imageUrl"
              :src="convo.item.imageUrl"
              alt="Item image"
              class="item-preview-image"
            />
            <div v-else class="item-image-placeholder">?</div>
          </div>

          <div class="convo-details">
            <span class="other-user">{{ convo.otherUser.displayName }}</span>
            <span v-if="convo.item" class="related-item">{{ $t('ITEM') }} {{ convo.item.title || 'N/A' }}</span>
            <p class="last-message-snippet">
              {{ convo.lastMessage ? truncate(convo.lastMessage.content || convo.lastMessage.messageContent) : 'No messages yet' }}
            </p>
          </div>

          <div v-if="convo.unreadMessagesCount > 0" class="unread-badge">
            {{ convo.unreadMessagesCount }}
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

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

function truncate(text: string | undefined | null, maxLength = 50): string {
  if (!text) return 'No content'
  return text.length > maxLength ? `${text.substring(0, maxLength)}...` : text
}

function goToConversation(conversationId: string | number) {
  router.push(`/messages/${conversationId}`)
}

const handleNewMessage = (message: Message) => {
  console.log('New message received:', message.messageContent)
  refreshConversations()
}

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

<style scoped>
@import '@/assets/styles/buttons/buttons.css';
@import '@/assets/styles/input/input.css';
@import '@/assets/styles/responsiveStyles.css';

.listing-form-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 20px;
}

.listing-form {
  width: 100%;
  max-width: 800px;
  background: var(--color-background, white);
  border-radius: 12px;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.20);
  padding: 35px;
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.form-title {
  color: var(--color-heading, #2c3e50);
  font-size: 26px;
  font-weight: 600;
  margin: 0 0 10px 0;
  text-align: center;
}

.form-divider {
  width: 100%;
  height: 2px;
  background-color: var(--color-border, #e0e0e0);
  margin-bottom: 15px;
}

.conversation-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.conversation-item {
  background: linear-gradient(to bottom, var(--vt-c-white), var(--vt-c-white-soft));
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-radius: 10px;
  border: 1px solid var(--color-border, #e0e0e0); /* ✅ Light grey border */
  cursor: pointer;
  transition: background-color 0.5s ease;
}

.conversation-item:hover {
  background: var(--vt-c-white-mute);
  border-left: 3px solid var(--vt-c-teal-soft);
}

.conversation-item.has-unread {
  border-left: 3px solid var(--vt-c-teal-soft);
}
.conversation-item.has-unread .other-user {
  font-weight: bold;
  color: var(--vt-c-teal-dark);
}

.item-image-container {
  flex-shrink: 0;
  margin-right: 1rem;
}

.item-preview-image,
.item-image-placeholder {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
  background-color: #eee;
  display: flex;
  align-items: center;
  justify-content: center;
}

.item-image-placeholder {
  background-color: var(--vt-c-teal-soft);
  color: var(--vt-c-white);
  font-size: 1.5em;
}

.convo-details {
  flex-grow: 1;
  overflow: hidden;
}

.other-user {
  font-weight: bold;
  display: block;
  margin-bottom: 0.25rem;
}

.related-item {
  font-size: 0.9em;
  color: var(--vt-c-text-light-2);
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.last-message-snippet {
  font-size: 0.9em;
  color: var(--vt-c-text-light-1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.unread-badge {
  background-color: var(--vt-c-teal-dark);
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

.loading,
.error,
.empty-inbox {
  text-align: center;
  color: var(--vt-c-text-light-1);
  font-size: 15px;
}
.error {
  color: var(--vt-c-red-dark);
}
</style>
