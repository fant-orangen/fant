import type { Message, ConversationPreview, MessageUser } from '@/models/Message'
import api from '@/services/api/axiosInstance' // Keep using axios instance

// Base URL for the json-server (adjust if needed)
const JSON_SERVER_URL = 'http://localhost:3000'

// --- Hardcoded User for Dummy Data Interaction ---
// Change this ID to view conversations from a different perspective in your dummy data
const DUMMY_USER_ID = '1'
const DUMMY_USERNAME = 'alice' // Match the dummy data

/**
 * Fetches all conversations for the currently authenticated user.
 * The user is identified through their JWT token which is automatically
 * included in the request by the axios interceptor.
 *
 * @returns A promise that resolves to an array of conversation previews
 */
export async function fetchConversations(): Promise<ConversationPreview[]> {
  try {
    // The endpoint will use the JWT token to identify the user
    const response = await api.get<ConversationPreview[]>('/messages/conversations')
    return response.data
  } catch (error) {
    console.error('Error fetching conversations:', error)
    throw error
  }
}

/**
 * Fetches all messages related to a specific item.
 *
 * @param itemId - The ID of the item to fetch messages for
 * @returns A promise that resolves to an array of messages
 * @throws {Error} If the request fails
 */
export async function fetchMessages(itemId: string | number): Promise<Message[]> {
  try {
    const response = await api.get<Message[]>('/messaging/messages', {
      params: { itemId },
    })

    // Transform dates from strings to Date objects
    return response.data.map((message) => ({
      ...message,
      sentDate: new Date(message.sentDate),
    }))
  } catch (error) {
    console.error('Error fetching messages for item:', error)
    throw error
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
    throw new Error('sendMessage (Dummy Mode): Recipient ID is required.')
  }

  console.log(
    `sendMessage (Dummy Mode): Sending message from ${DUMMY_USER_ID} to ${recipientId}...`,
  )

  // Simplified
  const recipient: MessageUser = { id: recipientId, username: `User ${recipientId}` }

  const newMessagePayload: Omit<Message, 'id'> = {
    sender: { id: DUMMY_USER_ID, username: DUMMY_USERNAME },
    receiver: recipient,

    messageContent: messageContent,
    sentDate: new Date(),
  }

  try {
    const response = await api.post<Message>(`${JSON_SERVER_URL}/messages`, newMessagePayload)
    console.log('sendMessage (Dummy Mode): Message sent successfully:', response.data)
    return {
      ...response.data,
      sentDate: new Date(response.data.sentDate),
    }
  } catch (error) {
    console.error(`sendMessage (Dummy Mode): Error sending message to ${recipientId}:`, error)
    throw error
  }
}
