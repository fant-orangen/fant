import { describe, it, expect, vi, beforeEach } from "vitest"

vi.mock("../../../services/api/axiosInstance.ts", () => ({
  default: {
    post: vi.fn(),
    delete: vi.fn(),
  },
}))

describe("ImageService", () => {
  let imageService
  let api

  beforeEach(async () => {
    vi.clearAllMocks()
    imageService = await import("../../../services/ImageService.ts")
    api = (await import("../../../services/api/axiosInstance.ts")).default
  })

  describe("uploadImages", () => {
    it("uploads images successfully with status 200", async () => {
      api.post.mockResolvedValue({ status: 200 })

      const mockFile = new File(["mock content"], "image.png", { type: "image/png" })
      await expect(imageService.uploadImages([mockFile], 123)).resolves.toBeUndefined()

      expect(api.post).toHaveBeenCalledWith(
        "/images/add",
        expect.any(FormData),
        { headers: { "Content-Type": "multipart/form-data" } }
      )
    })

    it("throws error if response status is not 200", async () => {
      api.post.mockResolvedValue({ status: 500 })
      const mockFile = new File(["fail"], "fail.png", { type: "image/png" })

      await expect(imageService.uploadImages([mockFile], 1)).rejects.toThrow("Unexpected response status: 500")
    })

    it("throws if upload request fails", async () => {
      api.post.mockRejectedValue(new Error("Upload failed"))
      const mockFile = new File(["fail"], "fail.png", { type: "image/png" })

      await expect(imageService.uploadImages([mockFile], 1)).rejects.toThrow("Upload failed")
    })
  })

  describe("deleteImage", () => {
    it("deletes an image successfully with status 200", async () => {
      api.delete.mockResolvedValue({ status: 200 })
      await expect(imageService.deleteImage(123, "https://cloud/image.png")).resolves.toBeUndefined()

      expect(api.delete).toHaveBeenCalledWith("/images/delete", {
        params: {
          itemId: 123,
          imageUrl: "https://cloud/image.png"
        }
      })
    })

    it("throws error if delete response is not 200", async () => {
      api.delete.mockResolvedValue({ status: 404 })
      await expect(imageService.deleteImage(1, "bad-url")).rejects.toThrow("Unexpected response status: 404")
    })

    it("throws if delete request fails", async () => {
      api.delete.mockRejectedValue(new Error("Network error"))
      await expect(imageService.deleteImage(1, "bad-url")).rejects.toThrow("Network error")
    })
  })
})
