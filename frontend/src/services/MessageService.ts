/**
 * Message management service module.
 *
 * This service provides methods for real-time messaging functionality in the marketplace,
 * including WebSocket communication, conversation management, and message handling.
 *
 * @module MessageService
 */
import type { Message, ConversationPreview, PaginatedMessageResponse } from '@/models/Message'
import api from '@/services/api/axiosInstance' // Keep using axios instance
import { webSocketService } from '@/services/WebSocketService'
import { fetchCurrentUserId } from '@/services/UserService.ts'
import { useUserStore } from '@/stores/UserStore.ts'

/**
 * Initializes WebSocket connection for the current authenticated user.
 *
 * Sets up real-time messaging capabilities by establishing a WebSocket connection
 * using the current user's ID from the user store.
 *
 * @returns {Promise<void>} Promise that resolves when the WebSocket is successfully connected
 * @throws {Error} If connection fails or user is not authenticated
 */
export async function initializeMessaging(): Promise<void> {
  try {
    const userId = useUserStore().getUserId // Fetch the user ID from the store
    if (userId) {
      await webSocketService.connect(userId.toString())
    } else {
    }
  } catch (error) {
  }
}

/**
 * Fetches all conversations for the currently authenticated user.
 *
 * Makes a GET request to retrieve all conversation previews. The user is identified
 * through their JWT token which is automatically included by the axios interceptor.
 *
 * @returns {Promise<ConversationPreview[]>} Promise resolving to an array of conversation previews
 * @throws {Error} If the request fails due to authentication issues or network errors
 */
export async function fetchConversations(): Promise<ConversationPreview[]> {
  try {
    // The endpoint will use the JWT token to identify the user
    const response = await api.get<ConversationPreview[]>('/messaging/conversations')
    return response.data
  } catch (error) {
    throw error
  }
}

/**
 * Fetches a paginated list of messages for a specific item conversation.
 *
 * Makes a GET request to retrieve messages with pagination and sorting options.
 *
 * @param {string|number} itemId - ID of the item the conversation is about
 * @param {number} page - Page index (0-based)
 * @param {number} size - Number of messages per page
 * @param {string} [sort='sentAt,desc'] - Optional sorting instruction
 * @returns {Promise<PaginatedMessageResponse>} Promise resolving to paginated messages
 * @throws {Error} If the request fails
 */
export async function fetchPagedMessages(
  itemId: string | number,
  page: number,
  size: number,
  sort: string = 'sentAt,desc' // ✅ use the actual entity field name
): Promise<PaginatedMessageResponse> {
  try {
    const params: any = { itemId, page, size, sort };
    const response = await api.get<PaginatedMessageResponse>('/messaging/messages', { params });
    const paginated = {
      ...response.data,
      content: response.data.content.map((msg) => ({
        ...msg,
        sentDate: new Date(msg.sentDate),
      })),
    };

    return paginated;
  } catch (error) {
    throw error;
  }
}

/**
 * Marks messages as read by sending their IDs to the server.
 *
 * Makes a POST request to update message read status. Only unread messages sent
 * to the current user will be marked as read.
 *
 * @param {Message[]} messages - Array of messages to mark as read
 * @returns {Promise<void>} Promise that resolves when operation completes
 * @throws {Error} If the request fails
 */
export async function readMessages(messages: Message[]): Promise<void> {
  try {
    const unreadMessageIds = messages.map((message) => message.id)
    if (unreadMessageIds.length > 0) {
      await api.post('/messaging/readall', { messageIds: unreadMessageIds })
    }
  } catch (error) {
    throw error
  }
}

/**
 * Sends a message via WebSocket connection.
 *
 * Creates a message payload and sends it through the WebSocket service.
 * Returns a temporary message object while awaiting confirmation.
 *
 * @param {string|number} recipientId - ID of the message recipient
 * @param {string} messageContent - Text content of the message
 * @param {string|number} itemId - ID of the item the message is about
 * @returns {Promise<Message>} Promise resolving to the sent message object
 * @throws {Error} If sending fails
 */
export async function sendMessage(
  recipientId: string | number,
  messageContent: string,
  itemId: string | number,
): Promise<Message> {
  try {
    const userId = await fetchCurrentUserId() // TODO: Add a field for the current user id and only fetch it once
    const messagePayload: Partial<Message> = {
      sender: { id: userId, username: 'You' },
      receiver: { id: recipientId.toString(), username: '' },
      messageContent,
      sentDate: new Date(),
    }
    if (itemId) {
      messagePayload.item = { id: itemId, title: '' }
    }
    await webSocketService.sendMessage(messagePayload)
    return {
      id: `temp-${Date.now()}`,
      ...messagePayload,
      sender: { id: userId, username: 'You' },
      receiver: { id: recipientId.toString(), username: '' },
    } as Message
  } catch (error) {
    throw error
  }
}


/**
 * Registers a handler function for incoming messages over WebSocket.
 *
 * Uses the current user's ID to set up a message handler with the WebSocket service.
 *
 * @param {function} handler - Callback function that processes each new message
 * @returns {void}
 */
export function onNewMessage(handler: (message: Message) => void): void {
  fetchCurrentUserId()
    .then((userId) => {
      webSocketService.onMessage(userId.toString(), handler)
    })
    .catch((error) => {
    })
}

/**
 * Finds or creates a conversation for a specific item.
 *
 * Makes a POST request to locate an existing conversation or create a new one
 * about the specified item.
 *
 * @param {string|number} itemId - ID of the item to start a conversation about
 * @returns {Promise<number|string>} Promise resolving to the conversation identifier
 * @throws {Error} If the API call fails
 */
export async function initiateConversation(itemId: string | number): Promise<number | string> {
  try {
    // The backend endpoint expects itemId as a request parameter
    const response = await api.post<{ conversationId: number | string }>(
      '/messaging/conversations/initiate',
      null, // No request body needed for POST if using params
      {
        params: { itemId }, // Send itemId as a query parameter
      },
    )

    // Check if the response contains the expected data
    if (response.data && response.data.conversationId) {
      return response.data.conversationId
    } else {
      throw new Error('Invalid response received from initiate conversation endpoint.')
    }
  } catch (error) {
    throw error
  }
}

/**
 * Removes a previously registered message handler.
 *
 * Uses the current user's ID to remove a specific message handler from the WebSocket service.
 *
 * @param {function} handler - The handler function to remove
 * @returns {void}
 */
export function removeMessageHandler(handler: (message: Message) => void): void {
  fetchCurrentUserId()
    .then((userId) => {
      webSocketService.removeMessageHandler(userId.toString(), handler)
    })
    .catch((error) => {
    })
}

/**
 * Cleans up the WebSocket connection.
 *
 * Should be called when the user logs out or the application is closed
 * to properly terminate WebSocket connections.
 *
 * @returns {void}
 */
export function cleanupMessaging(): void {
  webSocketService.disconnect()
}
