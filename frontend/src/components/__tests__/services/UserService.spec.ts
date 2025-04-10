import { describe, it, expect, vi, beforeEach } from "vitest"

vi.mock("../../../services/api/axiosInstance", () => ({
  default: {
    get: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}))

vi.mock("../../../axiosConfig", () => ({
  default: {
    post: vi.fn(),
  },
}))

describe("UserService", () => {
  let userService
  let api
  let axiosInstance

  beforeEach(async () => {
    vi.clearAllMocks()
    userService = await import("../../../services/UserService.ts")
    api = (await import("../../../services/api/axiosInstance")).default
    axiosInstance = (await import("../../../axiosConfig")).default
  })

  it("fetches current user ID", async () => {
    api.get.mockResolvedValue({ data: 123 })
    const result = await userService.fetchCurrentUserId()
    expect(api.get).toHaveBeenCalledWith("/users/id")
    expect(result).toBe(123)
  })

  it("registers a user", async () => {
    const userData = {
      email: "test@example.com",
      password: "Password123",
      displayName: "TestUser",
      firstName: "Test",
      lastName: "User",
      phone: "12345678"
    }
    axiosInstance.post.mockResolvedValue({ data: "registered" })

    const result = await userService.register(userData)
    expect(axiosInstance.post).toHaveBeenCalledWith("/auth/register", userData)
    expect(result.data).toBe("registered")
  })

  it("fetches paginated admin users", async () => {
    const mockData = {
      content: [],
      totalPages: 1,
      totalElements: 0,
      size: 10,
      number: 0,
      first: true,
      last: false,
      empty: false,
    }
    api.get.mockResolvedValue({ data: mockData })

    const result = await userService.fetchAdminUsers(0, 10, { search: "query" })
    expect(api.get).toHaveBeenCalledWith("/admin/users", {
      params: { page: 0, size: 10, search: "query" },
    })
    expect(result).toEqual(mockData)
  })

  it("updates admin user info", async () => {
    const userData = {
      email: "updated@example.com",
      displayName: "UpdatedUser",
      firstName: "Updated",
      lastName: "User",
      phone: "98765432"
    }

    const updatedUser = { ...userData, id: 1, role: "USER", createdAt: "", updatedAt: "" }
    api.put.mockResolvedValue({ data: updatedUser })

    const result = await userService.updateAdminUser(1, userData)
    expect(api.put).toHaveBeenCalledWith("/admin/users/1", userData)
    expect(result).toEqual(updatedUser)
  })

  it("deletes an admin user", async () => {
    api.delete.mockResolvedValue({})
    await expect(userService.deleteAdminUser(99)).resolves.toBeUndefined()
    expect(api.delete).toHaveBeenCalledWith("/admin/users/99")
  })

  it("fetches admin user by ID", async () => {
    const backendUser = {
      id: 7,
      email: "user@example.com",
      displayName: "User",
      role: "USER",
      firstName: null,
      lastName: null,
      phone: null,
      createdAt: "2024-01-01T00:00:00",
      updatedAt: "2024-01-01T00:00:00"
    }

    api.get.mockResolvedValue({ data: backendUser })
    const result = await userService.fetchAdminUserById(7)

    expect(api.get).toHaveBeenCalledWith("/admin/users/7")
    expect(result).toEqual({
      id: 7,
      email: "user@example.com",
      displayName: "User",
      role: "USER",
      firstName: null,
      lastName: null,
      phone: null,
      createdAt: "2024-01-01T00:00:00",
      updatedAt: "" // Overwritten manually in your service
    })
  })
})
