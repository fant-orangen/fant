import type { Message, ConversationPreview, MessageUser } from '@/models/Message';
import api from '@/services/api/axiosInstance'; // Keep using axios instance

// Base URL for the json-server (adjust if needed)
const JSON_SERVER_URL = 'http://localhost:3000';

// --- Hardcoded User for Dummy Data Interaction ---
// Change this ID to view conversations from a different perspective in your dummy data
const DUMMY_USER_ID = "1";
const DUMMY_USERNAME = "alice"; // Match the dummy data

/**
 * Fetches all messages and processes them into conversation previews
 * based on the perspective of the DUMMY_USER_ID.
 */
export async function fetchConversations(): Promise<ConversationPreview[]> {
  console.log(`WorkspaceConversations (Dummy Mode): Fetching all messages, perspective of User ID: ${DUMMY_USER_ID}`);
  try {
    const response = await api.get<Message[]>(`${JSON_SERVER_URL}/messages`);

    if (!Array.isArray(response.data)) {
      console.error('fetchConversations: Response data is not an array:', response.data);
      return [];
    }

    const allMessages: Message[] = response.data.map(msg => ({
      ...msg,
      sentDate: new Date(msg.sentDate)
    }));

    // Process messages to create conversation previews involving the DUMMY_USER_ID
    const conversationsMap = new Map<string | number, ConversationPreview>();
    allMessages.sort((a, b) => b.sentDate.getTime() - a.sentDate.getTime()); // Sort newest first

    allMessages.forEach(message => {
      let otherUserId: string | number | null = null;
      let otherUser: MessageUser | null = null;

      // Check if the dummy user is involved
      if (message.sender.id === DUMMY_USER_ID) {
        otherUserId = message.receiver.id;
        otherUser = message.receiver;
      } else if (message.receiver.id === DUMMY_USER_ID) {
        otherUserId = message.sender.id;
        otherUser = message.sender;
      }

      // If the dummy user is involved, and we haven't created a preview for this other user yet
      if (otherUserId && otherUser && !conversationsMap.has(otherUserId)) {
        conversationsMap.set(otherUserId, {
          id: otherUserId,
          otherUser: otherUser,
          lastMessage: message,
          unreadMessagesCount: 0,
          relatedItem: message.item
        });
      }
    });

    const previews = Array.from(conversationsMap.values());
    console.log("fetchConversations (Dummy Mode): Generated previews:", previews);
    return previews;

  } catch (error) {
    console.error('fetchConversations (Dummy Mode): Error fetching or processing messages:', error);
    return [];
  }
}

/**
 * Fetches messages for a specific conversation between DUMMY_USER_ID and another user.
 * @param otherUserId - The ID of the other user in the conversation.
 */
export async function fetchMessages(otherUserId: string | number): Promise<Message[]> {
  if (!otherUserId) {
    console.error("fetchMessages (Dummy Mode): otherUserId is missing.");
    return [];
  }

  console.log(`WorkspaceMessages (Dummy Mode): Fetching messages between ${DUMMY_USER_ID} and ${otherUserId}...`);
  try {
    // Fetch ALL messages and filter
    const response = await api.get<Message[]>(`${JSON_SERVER_URL}/messages`);

    if (!Array.isArray(response.data)) {
      console.error('fetchMessages (Dummy Mode): Response data is not an array:', response.data);
      return [];
    }

    const filteredMessages = response.data.filter(message =>
      (message.sender.id === DUMMY_USER_ID && message.receiver.id === otherUserId) ||
      (message.sender.id === otherUserId && message.receiver.id === DUMMY_USER_ID)
    ).map(msg => ({
      ...msg,
      sentDate: new Date(msg.sentDate)
    })).sort((a, b) => a.sentDate.getTime() - b.sentDate.getTime()); // Sort oldest first

    console.log(`WorkspaceMessages (Dummy Mode): Found ${filteredMessages.length} messages.`);
    return filteredMessages;

  } catch (error) {
    console.error(`WorkspaceMessages (Dummy Mode): Error fetching messages between ${DUMMY_USER_ID} and ${otherUserId}:`, error);
    return [];
  }
}

/**
 * Sends a new message *as* the DUMMY_USER_ID.
 * @param recipientId - The ID of the user receiving the message.
 * @param messageContent - The content of the message.
 */
export async function sendMessage(
  recipientId: string | number,
  messageContent: string,
): Promise<Message> {
  if (!recipientId) {
    throw new Error("sendMessage (Dummy Mode): Recipient ID is required.");
  }

  console.log(`sendMessage (Dummy Mode): Sending message from ${DUMMY_USER_ID} to ${recipientId}...`);

  // Simplified
  const recipient: MessageUser = { id: recipientId, username: `User ${recipientId}` };

  const newMessagePayload: Omit<Message, 'id'> = {
    sender: { id: DUMMY_USER_ID, username: DUMMY_USERNAME },
    receiver: recipient,

    messageContent: messageContent,
    sentDate: new Date()
  };

  try {
    const response = await api.post<Message>(`${JSON_SERVER_URL}/messages`, newMessagePayload);
    console.log("sendMessage (Dummy Mode): Message sent successfully:", response.data);
    return {
      ...response.data,
      sentDate: new Date(response.data.sentDate)
    };
  } catch (error) {
    console.error(`sendMessage (Dummy Mode): Error sending message to ${recipientId}:`, error);
    throw error;
  }
}
