/**
 * /**
 *  * WebSocket service module for real-time messaging.
 *  *
 *  * This service provides a WebSocket client implementation using STOMP over SockJS
 *  * for real-time messaging functionality in the marketplace.
 *  *
 *  * @module WebSocketService
 *  */
import { Client } from '@stomp/stompjs'
import type { StompSubscription } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { ref } from 'vue'
import type { Message } from '@/models/Message'
import { useUserStore } from '@/stores/UserStore.ts'

/** Base WebSocket URL for connecting to the Spring Boot server */
const SOCKET_URL = 'http://localhost:8080/ws'

/**
 * WebSocket service for real-time messaging
 */
export class WebSocketService {
  private client: Client | null = null
  private connected = ref(false)
  private subscriptions: { [key: string]: StompSubscription } = {}
  private messageHandlers: { [key: string]: ((message: Message) => void)[] } = {}

  /**
   * Initialize and connect to the WebSocket server
   */
  connect(userId: string): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        // Get token from user store
        const userStore = useUserStore()
        const token = userStore.token

        if (!token) {
          reject(new Error('No authentication token available'))
          return
        }

        // Create STOMP client over SockJS with authentication
        this.client = new Client({
          // Pass token as URL parameter
          webSocketFactory: () => new SockJS(`${SOCKET_URL}?token=${token}`),
          // Also include token in STOMP headers for added security
          connectHeaders: {
            Authorization: `Bearer ${token}`, // TODO: consider placing the authorisation header elsewhere
          },
          reconnectDelay: 5000,
          heartbeatIncoming: 4000,
          heartbeatOutgoing: 4000,
          onConnect: () => {
            this.connected.value = true
            this.subscribeToPersonalMessages(userId)
            resolve()
          },
          // Rest of the config remains the same
          onStompError: (frame) => {
            reject(new Error(`STOMP error: ${frame.headers?.message || 'Unknown error'}`))
          },
          onWebSocketError: (event) => {
            reject(new Error('WebSocket connection error'))
          },
        })

        this.client.activate()
      } catch (error) {
        reject(error)
      }
    })
  }

  /**
   * Disconnects from the WebSocket server.
   *
   * Terminates the connection and cleans up all subscriptions and handlers.
   * Should be called when the user logs out or navigates away from messaging screens.
   *
   * @returns {void}
   */
  disconnect(): void {
    if (this.client && this.client.connected) {
      this.client.deactivate()
      this.connected.value = false
    }

    // Clear all subscriptions and handlers
    this.subscriptions = {}
    this.messageHandlers = {}
  }

  /**
   * Subscribes to personal messages for a specific user.
   *
   * Creates a subscription to the user's personal message topic and sets up
   * message processing to notify all registered handlers.
   *
   * @param {string} userId - ID of the user to subscribe for
   * @returns {void}
   * @private
   */
  private subscribeToPersonalMessages(userId: string): void {
    if (!this.client || !this.client.connected) {
      return
    }

    const destination = `/topic/messages/${userId}`

    if (this.subscriptions[destination]) {
      // Already subscribed
      return
    }

    this.subscriptions[destination] = this.client.subscribe(destination, (message) => {
      try {
        const receivedMessage = JSON.parse(message.body) as Message
        // Convert string date to Date object
        receivedMessage.sentDate = new Date(receivedMessage.sentDate)

        // Notify all registered handlers
        if (this.messageHandlers[userId]) {
          this.messageHandlers[userId].forEach((handler) => handler(receivedMessage))
        }
      } catch (error) {
      }
    })
  }

  /**
   * Sends a message through the WebSocket connection.
   *
   * Publishes a message to the server-side message handling endpoint.
   *
   * @param {Partial<Message>} message - Message object to send
   * @returns {Promise<void>} Promise that resolves when message is sent
   * @throws {Error} If the WebSocket is not connected or sending fails
   */
  sendMessage(message: Partial<Message>): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!this.client || !this.client.connected) {
        reject(new Error('WebSocket not connected'))
        return
      }

      try {
        this.client.publish({
          destination: '/app/chat.send',
          body: JSON.stringify(message),
        })
        resolve()
      } catch (error) {
        reject(error)
      }
    })
  }

  /**
   * Registers a handler function for incoming messages.
   *
   * Adds a callback function that will be invoked whenever a message
   * is received for the specified user.
   *
   * @param {string} userId - ID of the user to receive messages for
   * @param {function} handler - Callback function to handle incoming messages
   * @returns {void}
   */
  onMessage(userId: string, handler: (message: Message) => void): void {
    if (!this.messageHandlers[userId]) {
      this.messageHandlers[userId] = []
    }
    this.messageHandlers[userId].push(handler)
  }

  /**
   * Removes a previously registered message handler.
   *
   * Unregisters a specific callback function for a user to prevent
   * memory leaks when components are unmounted.
   *
   * @param {string} userId - ID of the user to remove handler for
   * @param {function} handler - The handler function to remove
   * @returns {void}
   */
  removeMessageHandler(userId: string, handler: (message: Message) => void): void {
    if (this.messageHandlers[userId]) {
      this.messageHandlers[userId] = this.messageHandlers[userId].filter((h) => h !== handler)
    }
  }

  /**
   * Checks if WebSocket connection is currently established.
   *
   * @returns {boolean} True if connected, false otherwise
   */
  isConnected(): boolean {
    return this.connected.value
  }
}

// Create and export a singleton instance
export const webSocketService = new WebSocketService()
