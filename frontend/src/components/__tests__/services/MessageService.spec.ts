import { describe, it, expect, vi, beforeEach } from 'vitest'

vi.mock('../../../services/api/axiosInstance.ts', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}))

vi.mock('../../../services/WebSocketService', () => ({
  webSocketService: {
    connect: vi.fn(),
    sendMessage: vi.fn(),
    onMessage: vi.fn(),
    removeMessageHandler: vi.fn(),
    disconnect: vi.fn(),
  }
}))

vi.mock('../../../services/UserService.ts', () => ({
  fetchCurrentUserId: vi.fn().mockResolvedValue(42),
}))

vi.mock('../../../stores/UserStore.ts', () => ({
  useUserStore: () => ({
    getUserId: 42,
  }),
}))

describe('MessageService', () => {
  let messageService
  let api
  let webSocketService
  let fetchCurrentUserId

  beforeEach(async () => {
    vi.clearAllMocks()
    messageService = await import('../../../services/MessageService.ts')
    api = (await import('../../../services/api/axiosInstance.ts')).default
    webSocketService = (await import('../../../services/WebSocketService')).webSocketService
    fetchCurrentUserId = (await import('../../../services/UserService.ts')).fetchCurrentUserId
  })

  it('initializes WebSocket messaging', async () => {
    await expect(messageService.initializeMessaging()).resolves.toBeUndefined()
    expect(webSocketService.connect).toHaveBeenCalledWith('42')
  })

  it('fetches conversations', async () => {
    const mockConversations = [{ id: 1, lastMessage: 'Hello' }]
    api.get.mockResolvedValue({ data: mockConversations })

    const result = await messageService.fetchConversations()
    expect(api.get).toHaveBeenCalledWith('/messaging/conversations')
    expect(result).toEqual(mockConversations)
  })

  it('fetches paginated messages and parses sentDate', async () => {
    const msgDate = new Date().toISOString()
    const response = {
      data: {
        content: [{ id: 1, messageContent: 'Hey', sentDate: msgDate }],
        totalPages: 1,
        size: 1,
        number: 0,
        totalElements: 1,
      }
    }
    api.get.mockResolvedValue(response)

    const result = await messageService.fetchPagedMessages(1, 0, 10)
    expect(api.get).toHaveBeenCalledWith('/messaging/messages', {
      params: { itemId: 1, page: 0, size: 10, sort: 'sentAt,desc' }
    })
    expect(result.content[0].sentDate).toBeInstanceOf(Date)
  })

  it('marks messages as read if any exist', async () => {
    const messages = [{ id: 1 }, { id: 2 }]
    api.post.mockResolvedValue({})
    await expect(messageService.readMessages(messages)).resolves.toBeUndefined()
    expect(api.post).toHaveBeenCalledWith('/messaging/readall', { messageIds: [1, 2] })
  })

  it('does not call API if no messages to mark as read', async () => {
    await messageService.readMessages([])
    expect(api.post).not.toHaveBeenCalled()
  })

  it('sends a message via WebSocket', async () => {
    const result = await messageService.sendMessage(7, 'Hello world', 99)
    expect(fetchCurrentUserId).toHaveBeenCalled()
    expect(webSocketService.sendMessage).toHaveBeenCalledWith(expect.objectContaining({
      messageContent: 'Hello world',
      item: { id: 99, title: '' },
    }))
    expect(result.id).toMatch(/^temp-/)
  })

  it('registers a new WebSocket message handler', () => {
    const mockHandler = vi.fn()
    messageService.onNewMessage(mockHandler)
    expect(webSocketService.onMessage).toHaveBeenCalledWith('42', mockHandler)
  })

  it('initiates a conversation and returns ID', async () => {
    api.post.mockResolvedValue({ data: { conversationId: 123 } })
    const result = await messageService.initiateConversation(55)
    expect(api.post).toHaveBeenCalledWith('/messaging/conversations/initiate', null, { params: { itemId: 55 } })
    expect(result).toBe(123)
  })

  it('throws if conversation response is invalid', async () => {
    api.post.mockResolvedValue({ data: {} })
    await expect(messageService.initiateConversation(77)).rejects.toThrow('Invalid response received from initiate conversation endpoint.')
  })

  it('removes message handler via WebSocketService', async () => {
    const mockHandler = vi.fn()
    await messageService.removeMessageHandler(mockHandler)
    expect(webSocketService.removeMessageHandler).toHaveBeenCalledWith('42', mockHandler)
  })

  it('disconnects WebSocket on cleanup', () => {
    messageService.cleanupMessaging()
    expect(webSocketService.disconnect).toHaveBeenCalled()
  })
})
