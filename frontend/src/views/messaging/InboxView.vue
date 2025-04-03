<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import type { ConversationPreview } from '@/models/Message'; // Use your defined types
// Assume you create this service and functions
import { fetchConversations } from '@/services/messageService';

const router = useRouter();
const conversations = ref<ConversationPreview[]>([]);
const loading = ref(false);
const error = ref<string | null>(null);

// Fetch conversations when component mounts
onMounted(async () => {
  loading.value = true;
  error.value = null;
  try {
    // This function needs to be implemented in messageService.ts
    conversations.value = await fetchConversations();
  } catch (err) {
    console.error("InboxView: Failed to fetch conversations:", err);
    error.value = "Could not load conversations.";
    conversations.value = []; // Ensure it's an empty array on error
  } finally {
    loading.value = false;
  }
});

// Navigate to the specific conversation view
function goToConversation(conversationId: string | number) {
  // Assumes your route is named 'messages-conversation' and takes 'conversationId' as param
  router.push({ name: 'messages-conversation', params: { conversationId: conversationId.toString() } });
}

// Helper function for truncating message preview (optional)
function truncate(text: string, length = 50) {
  if (!text) return '';
  return text.length > length ? text.substring(0, length) + '...' : text;
}

</script>

<template>
  <div class="inbox-view">
    <h1>Inbox</h1>
    <div v-if="loading" class="loading">Loading conversations...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="conversations.length === 0" class="empty-inbox">
      No conversations yet.
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
          <span v-if="convo.relatedItem" class="related-item">Item: {{ convo.relatedItem.title }}</span>
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s ease;
}
.conversation-item:hover {
  background-color: #1111;
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
.loading, .error, .empty-inbox {
  text-align: center;
  padding: 2rem;
  color: #777;
}
.error {
  color: red;
}
</style>
