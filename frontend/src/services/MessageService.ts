import type { Message, ConversationPreview } from '@/models/Message'
import api from '@/services/api/axiosInstance' // Keep using axios instance
import { webSocketService } from '@/services/WebSocketService'
import { fetchCurrentUserId } from '@/services/UserService.ts'

// Base URL for the json-server (adjust if needed)
//const JSON_SERVER_URL = 'http://localhost:3000'

// --- Hardcoded User for Dummy Data Interaction ---
// Change this ID to view conversations from a different perspective in your dummy data
//const DUMMY_USER_ID = '1'
//const DUMMY_USERNAME = 'alice' // Match the dummy data

/**
 * Initialize WebSocket connection
 */
// src/services/MessageService.ts - Fix the initializeMessaging function
export async function initializeMessaging(): Promise<void> {
  try {
    const userId = await fetchCurrentUserId() // Add await here
    if (userId) {
      await webSocketService.connect(userId.toString())
      console.log('WebSocket initialized for user:', userId)
    } else {
      console.warn('Cannot initialize messaging: User not authenticated')
    }
  } catch (error) {
    console.error('Failed to initialize WebSocket:', error)
  }
}

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
    const response = await api.get<ConversationPreview[]>('/messaging/conversations')
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
 * Sends a message via WebSocket
 */
export async function sendMessage(
  recipientId: string | number,
  messageContent: string,
  itemId?: string | number,
): Promise<Message> {
  try {
    const userId = await fetchCurrentUserId()

    // Create message payload
    const messagePayload: Partial<Message> = {
      sender: { id: userId, username: 'You' },
      receiver: { id: recipientId.toString(), username: '' },
      messageContent,
      sentDate: new Date(),
    }

    if (itemId) {
      messagePayload.item = { id: itemId, title: '' }
    }

    // Send via WebSocket
    await webSocketService.sendMessage(messagePayload)

    // Return temporary message (real one will come through WebSocket)
    return {
      id: `temp-${Date.now()}`,
      ...messagePayload,
      sender: { id: userId, username: 'You' },
      receiver: { id: recipientId.toString(), username: '' },
    } as Message
  } catch (error) {
    console.error('Error sending message:', error)
    throw error
  }
}

/**
 * Register handler for new messages
 */
export function onNewMessage(handler: (message: Message) => void): void {
  fetchCurrentUserId()
    .then((userId) => {
      webSocketService.onMessage(userId.toString(), handler)
    })
    .catch((error) => {
      console.error('Error registering message handler:', error)
    })
}

/**
 * Remove a message handler
 */
export function removeMessageHandler(handler: (message: Message) => void): void {
  fetchCurrentUserId()
    .then((userId) => {
      webSocketService.removeMessageHandler(userId.toString(), handler)
    })
    .catch((error) => {
      console.error('Error removing message handler:', error)
    })
}

/**
 * Cleanup WebSocket connection
 */
export function cleanupMessaging(): void {
  webSocketService.disconnect()
}
