import { describe, it, expect, vi, beforeEach } from "vitest"
import api from "@/services/api/axiosInstance"
import {
  placeBid,
  fetchPagedUserBids,
  fetchBidsForItem,
  acceptBid,
  rejectBid,
  deleteMyBid,
  updateMyBid
} from "@/services/api/bidService"

vi.mock("@/services/api/axiosInstance", () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
    delete: vi.fn(),
    put: vi.fn(),
  },
}))

describe("bidService", () => {
  const bidPayload = { itemId: 1, amount: 500, comment: "Nice offer" }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it("places a bid successfully", async () => {
    api.post.mockResolvedValue({ status: 201 })
    const result = await placeBid(bidPayload)
    expect(api.post).toHaveBeenCalledWith("/orders/bid", bidPayload)
    expect(result).toEqual({ status: 201 })
  })

  it("throws error when placing a bid fails", async () => {
    api.post.mockRejectedValue({ response: { status: 400 }, message: "Invalid bid" })
    await expect(placeBid(bidPayload)).rejects.toThrow("Failed to place bid (Status: 400): Invalid bid")
  })

  it("fetches paginated user bids successfully", async () => {
    const data = { content: [], totalPages: 1 }
    api.get.mockResolvedValue({ data })
    const result = await fetchPagedUserBids(0, 10, "createdAt,desc")
    expect(api.get).toHaveBeenCalled()
    expect(result).toBe(data)
  })

  it("throws error on failed user bids fetch", async () => {
    api.get.mockRejectedValue({ message: "Server error" })
    await expect(fetchPagedUserBids(0, 10)).rejects.toThrow("Server error")
  })

  it("fetches bids for an item successfully", async () => {
    const mockData = [{ bidderId: 1, amount: 100 }]
    api.get.mockResolvedValue({ data: mockData })
    const result = await fetchBidsForItem(42)
    expect(api.get).toHaveBeenCalledWith("/orders/item/42")
    expect(result).toEqual(mockData)
  })

  it("handles 403 fetch bids error", async () => {
    api.get.mockRejectedValue({ response: { status: 403 }, message: "Forbidden" })
    await expect(fetchBidsForItem(42)).rejects.toThrow("You are not authorized to view bids for this item.")
  })

  it("handles 404 fetch bids error", async () => {
    api.get.mockRejectedValue({ response: { status: 404 }, message: "Not Found" })
    await expect(fetchBidsForItem(42)).rejects.toThrow("Item or bids not found.")
  })

  it("accepts a bid successfully", async () => {
    api.post.mockResolvedValue({ status: 200 })
    const result = await acceptBid(1, 2)
    expect(api.post).toHaveBeenCalledWith("/orders/accept", null, { params: { itemId: 2, bidderId: 1 } })
    expect(result).toEqual({ status: 200 })
  })

  it("rejects a bid successfully", async () => {
    api.post.mockResolvedValue({ status: 204 })
    const result = await rejectBid(1, 2)
    expect(api.post).toHaveBeenCalledWith("/orders/reject", null, { params: { itemId: 2, bidderId: 1 } })
    expect(result).toEqual({ status: 204 })
  })

  it("deletes a bid successfully", async () => {
    api.delete.mockResolvedValue({ status: 200 })
    const result = await deleteMyBid(5)
    expect(api.delete).toHaveBeenCalledWith("/orders/delete/5")
    expect(result).toEqual({ status: 200 })
  })

  it("updates a bid successfully", async () => {
    api.put.mockResolvedValue({ status: 200 })
    const updatePayload = { itemId: 3, amount: 300, comment: "Updated bid" }
    const result = await updateMyBid(updatePayload)
    expect(api.put).toHaveBeenCalledWith("/orders/update/3", {
      amount: 300,
      comment: "Updated bid"
    })
    expect(result).toEqual({ status: 200 })
  })

  it("handles update bid failure due to non-pending status", async () => {
    api.put.mockRejectedValue({
      response: { status: 400, data: "Bid is not in PENDING status" },
      message: "Bad Request"
    })
    await expect(updateMyBid({ itemId: 3 })).rejects.toThrow("Cannot update this bid as it is no longer pending.")
  })
})
