import { describe, it, expect, vi, beforeEach } from "vitest"

vi.mock("../../../axiosConfig", () => ({
  default: {
    post: vi.fn(),
  },
}))

describe("authService", () => {
  let authService
  let axiosInstance

  beforeEach(async () => {
    vi.clearAllMocks()
    authService = await import("../../../services/api/authService.ts")
    axiosInstance = (await import("../../../axiosConfig")).default
  })

  it("sends correct request to login endpoint", async () => {
    const mockResponse = { data: { token: "abc123" } }
    axiosInstance.post.mockResolvedValue(mockResponse)

    const result = await authService.fetchToken({
      username: "test@example.com",
      password: "Password123"
    })

    expect(axiosInstance.post).toHaveBeenCalledWith(
      "/auth/login",
      JSON.stringify({ email: "test@example.com", password: "Password123" }),
      {
        headers: {
          "Content-Type": "application/json"
        }
      }
    )
    expect(result).toEqual(mockResponse)
  })

  it("throws error if request fails", async () => {
    axiosInstance.post.mockRejectedValue(new Error("Invalid credentials"))

    await expect(authService.fetchToken({
      username: "fail@example.com",
      password: "wrong"
    })).rejects.toThrow("Invalid credentials")
  })
})
