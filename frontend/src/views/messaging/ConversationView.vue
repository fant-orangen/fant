<script setup lang="ts">
import { ref, onMounted, computed, watch, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import type { Message, MessageUser } from '@/models/Message'; // Use your defined types
// Uses the simplified service
import { fetchMessages, sendMessage } from '@/services/MessageService.ts';
// Removed UserStore import

const route = useRoute();
// Removed userStore definition

const messages = ref<Message[]>([]);
const newMessageContent = ref('');
const otherUser = ref<MessageUser | null>(null); // To display who you're talking to
const loading = ref(false);
const error = ref<string | null>(null);
const sending = ref(false);
const messagesContainerRef = ref<HTMLElement | null>(null); // For auto-scrolling

// Get conversationId (which is the other user's ID in this context) from route params
const conversationId = computed(() => route.params.conversationId as string);

// Define the "current user" perspective using the hardcoded ID
const currentUserId = "1"; // Represents "alice" in dummy data

// Fetch messages when component mounts or conversationId changes
async function loadMessages() {
  if (!conversationId.value) {
    error.value = "Invalid conversation ID.";
    return;
  }
  loading.value = true;
  error.value = null;
  try {
    // FIX: Call fetchMessages with only one argument (the other user's ID)
    messages.value = await fetchMessages(conversationId.value);

    // Attempt to set otherUser based on fetched messages
    if (messages.value.length > 0) {
      const firstMessage = messages.value[0];
      // This logic still needs currentUserId, which should be hardcoded "1" here
      const currentUserId = "1"; // Define the hardcoded ID here or keep from component scope
      otherUser.value = firstMessage.sender.id === currentUserId
        ? firstMessage.receiver
        : firstMessage.sender;
    } else {
      // Fallback if no messages
      otherUser.value = { id: conversationId.value, username: `User ${conversationId.value}` };
    }

    scrollToBottom();

  } catch (err) {
    console.error(`ConversationView: Failed to fetch messages for ${conversationId.value}:`, err);
    error.value = "Could not load messages.";
    messages.value = [];
  } finally {
    loading.value = false;
  }
}

// Send a new message (uses hardcoded sender via sendMessage)
async function handleSendMessage() {
  if (!newMessageContent.value.trim() || sending.value || !conversationId.value) return;
  sending.value = true;
  error.value = null;
  try {
    const recipientId = conversationId.value; // The other user is the recipient
    const sentMessage = await sendMessage(recipientId, newMessageContent.value);
    messages.value.push(sentMessage);
    newMessageContent.value = '';
    scrollToBottom();
  } catch (err) {
    console.error(`ConversationView: Failed to send message:`, err);
    error.value = "Failed to send message.";
  } finally {
    sending.value = false;
  }
}

// Determine if a message was sent by the "current" hardcoded user
function isMyMessage(message: Message): boolean {
  // Now compares message sender ID ("1", "2", etc.) with the hardcoded currentUserId ("1")
  return message.sender.id === currentUserId;
}

// Format timestamp (example - unchanged)
function formatTimestamp(date: string | Date): string {
  if (!date) return '';
  const d = typeof date === 'string' ? new Date(date) : date;
  return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

// Auto-scroll to bottom of messages (unchanged)
function scrollToBottom() {
  nextTick(() => {
    const container = messagesContainerRef.value;
    if (container) {
      container.scrollTop = container.scrollHeight;
    }
  });
}

// Watch for route changes (unchanged)
watch(conversationId, (newId, oldId) => {
  if (newId && newId !== oldId) {
    loadMessages(); // Reload messages for the new conversation
  }
});

// Initial load (unchanged)
onMounted(() => {
  loadMessages();
});
</script>

<template>
  <div class="conversation-view">
    <div class="header">
      <router-link to="/messages">Back to Inbox</router-link>
      <h2 v-if="otherUser">Chat with {{ otherUser.username }}</h2>
    </div>

    <div v-if="loading" class="loading">Loading messages...</div>
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
         placeholder="Type your message..."
         @keyup.enter.prevent="handleSendMessage"
         :disabled="sending"
       ></textarea>
      <button @click="handleSendMessage" :disabled="!newMessageContent.trim() || sending">
        {{ sending ? 'Sending...' : 'Send' }}
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
  background-color: #20a830;
  color: white;
  border-bottom-right-radius: 5px;
}
.message-bubble-wrapper.received .message-bubble {
  background-color: #72b1d6;
  color: black;
  border-bottom-left-radius: 5px;
}
.message-content {
  margin: 0;
  word-wrap: break-word;
}
.message-timestamp {
  font-size: 0.7em;
  color: #999;
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

.loading, .error {
  text-align: center;
  padding: 2rem;
  color: #777;
}
.error {
  color: red;
}
</style>
