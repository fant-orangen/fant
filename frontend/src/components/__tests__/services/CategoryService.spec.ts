import { describe, it, expect, vi, beforeEach } from "vitest";

// Mock the API instance
vi.mock("../../../services/api/axiosInstance.ts", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

// Import the service and types AFTER mocking
import { Category } from "@/models/Category";
// Correctly import the specific service functions being tested
import { fetchCategories, addCategory, updateCategory, deleteCategory } from "../../../services/CategoryService";

describe("CategoryService", () => {
  let api;

  // Sample Category object (as returned by API or used internally)
  const sampleCategory: Category = {
    id: 1,
    name: "Electronics",
    imageUrl: "http://example.com/electronics.jpg",
    parent: null
  };

  beforeEach(async () => {
    vi.clearAllMocks();
    // Dynamically import mocks after resetting
    api = (await import("../../../services/api/axiosInstance")).default;
  });

  it("fetches all categories", async () => {
    const categories = [sampleCategory];
    api.get.mockResolvedValue({ data: categories });

    const result = await fetchCategories(); // Use imported function
    expect(api.get).toHaveBeenCalledWith("/category/all");
    expect(result).toEqual(categories);
  });

  it("adds a new category", async () => {
    // Data structure representing the payload SENT to the backend
    const newCategoryData = {
      name: "New Gadgets",
      imageUrl: "http://example.com/gadgets.jpg",
      parentId: null // Matches CategoryRequestPayload
    };
    // Expected result structure RETURNED from the backend
    const createdCategoryResult = { ...newCategoryData, id: 2, parent: null };

    api.post.mockResolvedValue({ data: createdCategoryResult });

    // Call the service function with the payload data
    const result = await addCategory(newCategoryData); // Use imported function

    // **** CORRECTED ASSERTION ****
    // Assert the API call was made with the correct PAYLOAD structure (`newCategoryData`)
    expect(api.post).toHaveBeenCalledWith("/admin/category", newCategoryData);
    // **** END CORRECTION ****

    // Assert the result matches the expected structure returned by backend
    expect(result).toEqual(createdCategoryResult);
  });


  it("updates an existing category", async () => {
    // Full Category object passed to the service function
    const categoryToUpdate: Category = {
      id: 1, // Must have ID for update
      name: "Updated Electronics",
      imageUrl: "http://example.com/updated.jpg",
      parent: null
    };

    // The payload structure *actually sent* to the backend API
    const expectedPayload = {
      name: categoryToUpdate.name,
      imageUrl: categoryToUpdate.imageUrl,
      parentId: categoryToUpdate.parent?.id ?? null
    };

    // The expected result returned from the backend API
    const updatedCategoryResult = { ...categoryToUpdate };

    api.put.mockResolvedValue({ data: updatedCategoryResult });

    // Call the service function
    const result = await updateCategory(categoryToUpdate); // Use imported function

    // The correct URL for the update request
    const expectedUrl = `/admin/category/${categoryToUpdate.id}`;

    // **** CORRECTED ASSERTION ****
    // Assert the API call was made with the correct URL and PAYLOAD structure (`expectedPayload`)
    expect(api.put).toHaveBeenCalledWith(expectedUrl, expectedPayload);
    // **** END CORRECTION ****

    // Assert the result matches the expected structure returned by backend
    expect(result).toEqual(updatedCategoryResult);
  });


  it("deletes a category by ID", async () => {
    api.delete.mockResolvedValue({});

    await expect(deleteCategory(123)).resolves.toBeUndefined(); // Use imported function
    // Assert the API call was made with the correct endpoint
    expect(api.delete).toHaveBeenCalledWith("/admin/category/123");
  });
});
