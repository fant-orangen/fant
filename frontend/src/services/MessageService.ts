import type { Message, ConversationPreview, MessageUser } from '@/models/Message';
import api from '@/services/api/axiosInstance';

// Base URL for the API
const API_URL = 'http://localhost:3000';

// Current user ID from authentication context
// In a real app, this would come from auth state
const CURRENT_USER_ID = "1";
const CURRENT_USERNAME = "alice";

/**
 * Fetches conversation previews for the current user
 */
export async function fetchConversations(): Promise<ConversationPreview[]> {
  try {
    const response = await api.get<Message[]>(`${API_URL}/messages`);

    if (!Array.isArray(response.data)) {
      console.error('Invalid response format');
      return [];
    }

    // Process messages into conversation previews
    const allMessages = response.data.map(msg => ({
      ...msg,
      sentDate: new Date(msg.sentDate)
    }));

    // Group messages by conversation
    const conversationsMap = new Map<string | number, ConversationPreview>();

    // Sort by newest first for processing
    allMessages.sort((a, b) => b.sentDate.getTime() - a.sentDate.getTime());

    allMessages.forEach(message => {
      // Determine if current user is involved and who the other user is
      let otherUserId: string | number | null = null;
      let otherUser: MessageUser | null = null;

      if (message.sender.id === CURRENT_USER_ID) {
        otherUserId = message.receiver.id;
        otherUser = message.receiver;
      } else if (message.receiver.id === CURRENT_USER_ID) {
        otherUserId = message.sender.id;
        otherUser = message.sender;
      }

      // If this is a relevant conversation and we haven't processed it yet
      if (otherUserId && otherUser && !conversationsMap.has(otherUserId)) {
        conversationsMap.set(otherUserId, {
          id: otherUserId,
          otherUser: otherUser,
          lastMessage: message,
          unreadMessagesCount: 0, // Implement real unread count if needed
          relatedItem: message.item
        });
      }
    });

    return Array.from(conversationsMap.values());
  } catch (error) {
    console.error('Failed to fetch conversations:', error);
    throw error; // Let component handle the error
  }
}

/**
 * Fetches messages for a conversation with another user
 */
export async function fetchMessages(otherUserId: string | number): Promise<Message[]> {
  try {
    const response = await api.get<Message[]>(`${API_URL}/messages`);

    // Filter messages between current user and the other user
    const conversationMessages = response.data
    .filter(message =>
      (message.sender.id === CURRENT_USER_ID && message.receiver.id === otherUserId) ||
      (message.sender.id === otherUserId && message.receiver.id === CURRENT_USER_ID)
    )
    .map(msg => ({
      ...msg,
      sentDate: new Date(msg.sentDate)
    }))
    .sort((a, b) => a.sentDate.getTime() - b.sentDate.getTime()); // Oldest first

    return conversationMessages;
  } catch (error) {
    console.error(`Failed to fetch messages with user ${otherUserId}:`, error);
    throw error;
  }
}

/**
 * Sends a new message to another user
 */
export async function sendMessage(
  recipientId: string | number,
  messageContent: string
): Promise<Message> {
  const newMessage = {
    sender: { id: CURRENT_USER_ID, username: CURRENT_USERNAME },
    receiver: { id: recipientId, username: `User ${recipientId}` }, // Simplified
    messageContent,
    sentDate: new Date()
  };

  try {
    const response = await api.post<Message>(`${API_URL}/messages`, newMessage);
    return {
      ...response.data,
      sentDate: new Date(response.data.sentDate)
    };
  } catch (error) {
    console.error('Failed to send message:', error);
    throw error;
  }
}
