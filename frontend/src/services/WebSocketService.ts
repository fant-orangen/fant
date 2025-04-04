import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { ref } from 'vue'
import type { Message } from '@/models/Message'

// Base WebSocket URL (adjust to match your Spring Boot configuration)
const SOCKET_URL = 'http://localhost:8080/ws'

/**
 * WebSocket service for real-time messaging
 */
export class WebSocketService {
  private client: Client | null = null
  private connected = ref(false)
  private subscriptions: { [key: string]: any } = {}
  private messageHandlers: { [key: string]: ((message: Message) => void)[] } = {}

  /**
   * Initialize and connect to the WebSocket server
   */
  connect(userId: string): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        // Create STOMP client over SockJS
        this.client = new Client({
          webSocketFactory: () => new SockJS(SOCKET_URL),
          reconnectDelay: 5000,
          heartbeatIncoming: 4000,
          heartbeatOutgoing: 4000,
          onConnect: () => {
            console.log('WebSocket connected')
            this.connected.value = true

            // Subscribe to personal message topic
            this.subscribeToPersonalMessages(userId)
            resolve()
          },
          onStompError: (frame) => {
            console.error('STOMP error:', frame)
            reject(new Error(`STOMP error: ${frame.headers?.message || 'Unknown error'}`))
          },
          onWebSocketError: (event) => {
            console.error('WebSocket error:', event)
            reject(new Error('WebSocket connection error'))
          },
        })

        this.client.activate()
      } catch (error) {
        console.error('Error initializing WebSocket:', error)
        reject(error)
      }
    })
  }

  /**
   * Disconnect from the WebSocket server
   */
  disconnect(): void {
    if (this.client && this.client.connected) {
      this.client.deactivate()
      this.connected.value = false
      console.log('WebSocket disconnected')
    }

    // Clear all subscriptions and handlers
    this.subscriptions = {}
    this.messageHandlers = {}
  }

  /**
   * Subscribe to personal messages
   */
  private subscribeToPersonalMessages(userId: string): void {
    if (!this.client || !this.client.connected) {
      console.error('Cannot subscribe: WebSocket not connected')
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
        console.error('Error processing received message:', error)
      }
    })
  }

  /**
   * Send a message through WebSocket
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
        console.error('Error sending message:', error)
        reject(error)
      }
    })
  }

  /**
   * Register a handler for incoming messages
   */
  onMessage(userId: string, handler: (message: Message) => void): void {
    if (!this.messageHandlers[userId]) {
      this.messageHandlers[userId] = []
    }
    this.messageHandlers[userId].push(handler)
  }

  /**
   * Remove a message handler
   */
  removeMessageHandler(userId: string, handler: (message: Message) => void): void {
    if (this.messageHandlers[userId]) {
      this.messageHandlers[userId] = this.messageHandlers[userId].filter((h) => h !== handler)
    }
  }

  /**
   * Check if WebSocket is connected
   */
  isConnected(): boolean {
    return this.connected.value
  }
}

// Create and export a singleton instance
export const webSocketService = new WebSocketService()
