import { describe, it, expect, vi, beforeEach } from "vitest"

vi.mock("../../../services/api/axiosInstance.ts", () => ({
  default: {
    post: vi.fn(),
    delete: vi.fn(),
    get: vi.fn(),
  },
}))

describe("FavoriteService", () => {
  let favoriteService
  let api

  beforeEach(async () => {
    vi.clearAllMocks()
    favoriteService = await import("../../../services/FavoriteService.ts")
    api = (await import("../../../services/api/axiosInstance.ts")).default
  })

  describe("addFavorite", () => {
    it("sends POST request to add item to favorites", async () => {
      api.post.mockResolvedValue({})
      await expect(favoriteService.addFavorite(123)).resolves.toBeUndefined()
      expect(api.post).toHaveBeenCalledWith("/favorite/123")
    })

    it("throws error if POST request fails", async () => {
      api.post.mockRejectedValue(new Error("Failed"))
      await expect(favoriteService.addFavorite(1)).rejects.toThrow("Failed")
    })
  })

  describe("removeFavorite", () => {
    it("sends DELETE request to remove item from favorites", async () => {
      api.delete.mockResolvedValue({})
      await expect(favoriteService.removeFavorite(456)).resolves.toBeUndefined()
      expect(api.delete).toHaveBeenCalledWith("/favorite/456")
    })

    it("throws error if DELETE request fails", async () => {
      api.delete.mockRejectedValue(new Error("Failed"))
      await expect(favoriteService.removeFavorite(1)).rejects.toThrow("Failed")
    })
  })

  describe("checkIsFavorite", () => {
    it("returns true if item is favorited", async () => {
      api.get.mockResolvedValue({ data: true })
      const result = await favoriteService.checkIsFavorite(789)
      expect(api.get).toHaveBeenCalledWith("/favorite/status/789")
      expect(result).toBe(true)
    })

    it("returns false if item is not favorited", async () => {
      api.get.mockResolvedValue({ data: false })
      const result = await favoriteService.checkIsFavorite(789)
      expect(result).toBe(false)
    })

    it("returns false if itemId is null", async () => {
      const result = await favoriteService.checkIsFavorite(null)
      expect(result).toBe(false)
      expect(api.get).not.toHaveBeenCalled()
    })

    it("returns false if itemId is undefined", async () => {
      const result = await favoriteService.checkIsFavorite(undefined)
      expect(result).toBe(false)
      expect(api.get).not.toHaveBeenCalled()
    })

    it("returns false if server returns 404", async () => {
      api.get.mockRejectedValue({ response: { status: 404 } })
      const result = await favoriteService.checkIsFavorite(999)
      expect(result).toBe(false)
    })

    it("returns false on unknown error", async () => {
      api.get.mockRejectedValue(new Error("Server error"))
      const result = await favoriteService.checkIsFavorite(123)
      expect(result).toBe(false)
    })
  })
})
