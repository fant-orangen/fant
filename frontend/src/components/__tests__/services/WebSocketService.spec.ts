import { describe, it, expect, vi, beforeEach } from "vitest"

let onConnectCallback = () => {}

// ✅ Define shared STOMP mock with connect callback injection
const mockStompClient = {
  connected: true,
  activate: vi.fn(() => {
    // Simulate delayed connection (simulate WebSocket open event)
    setTimeout(() => onConnectCallback(), 10)
  }),
  deactivate: vi.fn(),
  subscribe: vi.fn((destination, callback) => ({
    id: destination,
    unsubscribe: vi.fn(),
  })),
  publish: vi.fn(),
}

// ✅ Mock STOMP client constructor
vi.mock("@stomp/stompjs", () => ({
  Client: vi.fn((config) => {
    onConnectCallback = config.onConnect
    return mockStompClient
  }),
}))

// ✅ Mock SockJS
vi.mock("sockjs-client", () => ({
  default: vi.fn(() => ({})),
}))

// ✅ Mock token store, overrideable
let currentToken = "mock_token"
vi.mock("../../../stores/UserStore.ts", () => ({
  useUserStore: () => ({
    token: currentToken,
  }),
}))

describe("WebSocketService", () => {
  let service

  beforeEach(async () => {
    vi.clearAllMocks()
    currentToken = "mock_token"
    service = (await import("../../../services/WebSocketService")).webSocketService
  })

  it("connects to WebSocket with valid token and subscribes", async () => {
    await expect(service.connect("42")).resolves.toBeUndefined()
    expect(mockStompClient.activate).toHaveBeenCalled()
  })

  it("rejects connect if token is missing", async () => {
    currentToken = null
    const localService = (await import("../../../services/WebSocketService")).webSocketService
    await expect(localService.connect("42")).rejects.toThrow("No authentication token available")
  })

  it("disconnects the WebSocket client", () => {
    mockStompClient.connected = true
    service.disconnect()
    expect(mockStompClient.deactivate).toHaveBeenCalled()
  })

  it("sends a message if connected", async () => {
    mockStompClient.connected = true
    const message = { messageContent: "Hello" }

    await expect(service.sendMessage(message)).resolves.toBeUndefined()
    expect(mockStompClient.publish).toHaveBeenCalledWith({
      destination: "/app/chat.send",
      body: JSON.stringify(message),
    })
  })

  it("rejects sending a message if not connected", async () => {
    mockStompClient.connected = false
    await expect(service.sendMessage({ messageContent: "Fail" })).rejects.toThrow("WebSocket not connected")
  })

  it("registers a message handler", () => {
    const handler = vi.fn()
    service.onMessage("42", handler)
    service.onMessage("42", handler)

    const handlers = service["messageHandlers"]["42"]
    expect(handlers).toContain(handler)
    expect(handlers.length).toBe(2)
  })

  it("removes a specific message handler", () => {
    const handler1 = vi.fn()
    const handler2 = vi.fn()

    service.onMessage("42", handler1)
    service.onMessage("42", handler2)

    service.removeMessageHandler("42", handler1)

    const handlers = service["messageHandlers"]["42"]
    expect(handlers).toContain(handler2)
    expect(handlers).not.toContain(handler1)
  })
})
