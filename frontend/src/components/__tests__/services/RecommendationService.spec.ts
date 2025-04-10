import { describe, it, expect, vi, beforeEach } from "vitest"

vi.mock("../../../services/api/axiosInstance.ts", () => ({
  default: {
    get: vi.fn(),
  },
}))

describe("RecommendationService", () => {
  let recommendationService
  let api

  beforeEach(async () => {
    vi.clearAllMocks()
    recommendationService = await import("../../../services/RecommendationService.ts")
    api = (await import("../../../services/api/axiosInstance.ts")).default
  })

  describe("fetchCategoryRecommendations", () => {
    it("returns category recommendation data on success", async () => {
      const mockData = {
        distribution: { electronics: 0.6, books: 0.4 }
      }
      api.get.mockResolvedValue({ data: mockData })

      const result = await recommendationService.fetchCategoryRecommendations()
      expect(api.get).toHaveBeenCalledWith("/recommendations/categories")
      expect(result).toEqual(mockData)
    })

    it("throws and logs error on failure", async () => {
      const error = new Error("Network error")
      api.get.mockRejectedValue(error)

      await expect(recommendationService.fetchCategoryRecommendations()).rejects.toThrow("Network error")
    })
  })

  describe("fetchUserViewCount", () => {
    it("returns total view count on success", async () => {
      api.get.mockResolvedValue({ data: { totalViews: 42 } })

      const result = await recommendationService.fetchUserViewCount()
      expect(api.get).toHaveBeenCalledWith("/recommendations/views/count")
      expect(result).toBe(42)
    })

    it("throws and logs error on failure", async () => {
      const error = new Error("Server error")
      api.get.mockRejectedValue(error)

      await expect(recommendationService.fetchUserViewCount()).rejects.toThrow("Server error")
    })
  })
})
