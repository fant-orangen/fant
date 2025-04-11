import { describe, it, expect, vi, beforeEach } from "vitest"

vi.mock("../../../services/api/axiosInstance", () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}))

describe("ItemService", () => {
  let itemService
  let api

  const dummyItem = {
    title: "Test Item",
    description: "A test item",
    price: 100,
    category: "TestCategory",
    location: "Testville",
  }

  beforeEach(async () => {
    vi.clearAllMocks()
    itemService = await import("../../../services/ItemService.ts")
    api = (await import("../../../services/api/axiosInstance")).default
  })

  it("creates an item", async () => {
    api.post.mockResolvedValue({ data: 42 })
    const result = await itemService.createItem(dummyItem)
    expect(api.post).toHaveBeenCalledWith("/items", dummyItem)
    expect(result).toBe(42)
  })

  it("updates an item", async () => {
    api.put.mockResolvedValue({ data: dummyItem })
    const result = await itemService.updateItem(1, dummyItem)
    expect(api.put).toHaveBeenCalledWith("/items/1", dummyItem)
    expect(result).toEqual(dummyItem)
  })

  it("deletes an item", async () => {
    api.delete.mockResolvedValue({})
    await expect(itemService.deleteItem(1)).resolves.toBeUndefined()
    expect(api.delete).toHaveBeenCalledWith("/items/1")
  })

  it("fetches preview items", async () => {
    const preview = { items: [], totalItems: 0 }
    api.get.mockResolvedValue({ data: preview })
    const result = await itemService.fetchPreviewItems()
    expect(api.get).toHaveBeenCalledWith("/items/all")
    expect(result).toBe(preview)
  })

  it("searches for items with filtered params", async () => {
    const resultData = { items: [], totalItems: 0 }
    const searchParams = {
      searchTerm: "bike",
      minPrice: 100,
      maxPrice: 500,
      categoryName: "",
      page: 0,
      size: 10,
    }
    api.get.mockResolvedValue({ data: resultData })

    const result = await itemService.searchItems(searchParams)
    expect(api.get).toHaveBeenCalledWith("/items/search", {
      params: {
        searchTerm: "bike",
        minPrice: 100,
        maxPrice: 500,
        page: 0,
        size: 10,
      },
    })
    expect(result).toEqual(resultData)
  })

  it("fetches item details", async () => {
    const itemDetails = { id: 1, title: "Test" }
    api.get.mockResolvedValue({ data: itemDetails })
    const result = await itemService.fetchItem(1)
    expect(api.get).toHaveBeenCalledWith("/items/details/1")
    expect(result).toEqual(itemDetails)
  })

  it("fetches items by recommendation distribution", async () => {
    const recommendation = { distribution: { A: 0.5, B: 0.5 } }
    const items = { items: [], totalItems: 0 }
    api.post.mockResolvedValue({ data: items })

    const result = await itemService.fetchItemsByDistribution(recommendation, 5)
    expect(api.post).toHaveBeenCalledWith("/items/view/recommended_items", {
      distribution: recommendation.distribution,
      limit: 5,
    })
    expect(result).toEqual(items)
  })

  it("records item view", async () => {
    api.post.mockResolvedValue({ status: 204 })
    const result = await itemService.recordItemView(1)
    expect(api.post).toHaveBeenCalledWith("/items/view/post/1")
    expect(result).toEqual({ status: 204 })
  })

  it("records item view with error fallback", async () => {
    api.post.mockRejectedValue(new Error("Network Error"))
    const result = await itemService.recordItemView(1)
    expect(result).toEqual({ status: 500 })
  })

  it("fetches paginated favorite items", async () => {
    const response = { items: [], totalItems: 0 }
    api.get.mockResolvedValue({ data: response })
    const result = await itemService.fetchPagedFavoriteItems(0, 10, "createdAt,desc")
    expect(api.get).toHaveBeenCalledWith("/favorite", {
      params: { page: 0, size: 10, sort: "createdAt,desc" },
    })
    expect(result).toBe(response)
  })

  it("fetches user's paginated items", async () => {
    const response = { items: [], totalItems: 0 }
    api.get.mockResolvedValue({ data: response })
    const result = await itemService.fetchMyPagedItems(0, 5, "createdAt,desc")
    expect(api.get).toHaveBeenCalledWith("/items/my", {
      params: { page: 0, size: 5, sort: "createdAt,desc" },
    })
    expect(result).toEqual(response)
  })

  it("admin deletes an item", async () => {
    api.delete.mockResolvedValue({})
    await expect(itemService.adminDeleteItem(123)).resolves.toBeUndefined()
    expect(api.delete).toHaveBeenCalledWith("/admin/item/123")
  })
})
