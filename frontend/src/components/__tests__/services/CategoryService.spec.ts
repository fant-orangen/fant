import { describe, it, expect, vi, beforeEach } from "vitest"

vi.mock("../../../services/api/axiosInstance.ts", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}))

describe("CategoryService", () => {
  let categoryService
  let api

  const sampleCategory = {
    id: 1,
    name: "Electronics",
    description: "Devices and gadgets"
  }

  beforeEach(async () => {
    vi.clearAllMocks()
    categoryService = await import("../../../services/CategoryService.ts")
    api = (await import("../../../services/api/axiosInstance.ts")).default
  })

  it("fetches all categories", async () => {
    const categories = [sampleCategory]
    api.get.mockResolvedValue({ data: categories })

    const result = await categoryService.fetchCategories()
    expect(api.get).toHaveBeenCalledWith("/category/all")
    expect(result).toEqual(categories)
  })

  it("adds a new category", async () => {
    const createdCategory = { ...sampleCategory, id: 2 }
    api.post.mockResolvedValue({ data: createdCategory })

    const result = await categoryService.addCategory(sampleCategory)
    expect(api.post).toHaveBeenCalledWith("/admin/category", sampleCategory)
    expect(result).toEqual(createdCategory)
  })

  it("updates an existing category", async () => {
    const updatedCategory = { ...sampleCategory, name: "Updated" }
    api.put.mockResolvedValue({ data: updatedCategory })

    const result = await categoryService.updateCategory(updatedCategory)
    expect(api.put).toHaveBeenCalledWith("/admin/category", updatedCategory)
    expect(result).toEqual(updatedCategory)
  })

  it("deletes a category by ID", async () => {
    api.delete.mockResolvedValue({})

    await expect(categoryService.deleteCategory(123)).resolves.toBeUndefined()
    expect(api.delete).toHaveBeenCalledWith("/admin/category/123")
  })
})
