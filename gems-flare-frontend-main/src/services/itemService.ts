import { API_BASE_URL } from "./apiConfig";
import { getAuthToken } from "./authHelpers";
import { Item, ItemsResponse } from "@/types";

export const itemService = {
  // Get all items with pagination
  async getItems(page = 0, size = 20): Promise<ItemsResponse> {
    try {
      const response = await fetch(`${API_BASE_URL}/item/getAllItems?page=${page}&size=${size}`);
      
      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error("Error fetching items:", error);
      throw error;
    }
  },

  // Get all categories
  async getAllCategories(): Promise<Array<{ id: string; name: string }>> {
    try {
      const response = await fetch(`${API_BASE_URL}/item/getAllCategories`);
      
      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error("Error fetching categories:", error);
      throw error;
    }
  },

  // Get items by category with pagination
  async getItemsByCategory(category: string, page = 0, size = 20): Promise<ItemsResponse> {
    try {
      const response = await fetch(`${API_BASE_URL}/item/getItemsByCategory?category=${category}&page=${page}&size=${size}`);
      
      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error("Error fetching items:", error);
      throw error;
    }
  },

  // Get item by item number
  async getItemByNumber(itemNumber: string): Promise<Item> {
    try {
      const response = await fetch(`${API_BASE_URL}/item/getItem/${itemNumber}`);
      
      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error(`Error fetching item with number ${itemNumber}:`, error);
      throw error;
    }
  },

  // Search items by name
  async searchItemsByName(name: string, page = 0, size = 20): Promise<ItemsResponse> {
    try {
      const response = await fetch(`${API_BASE_URL}/item/getItemsByName?name=${encodeURIComponent(name)}&page=${page}&size=${size}`);
      
      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error("Error searching items:", error);
      throw error;
    }
  },

  // Check edit permission for an item
  async checkItemEditPermission(itemNumber: string): Promise<boolean> {
    try {
      const token = getAuthToken();
      if (!token) {
        return false;
      }

      const response = await fetch(`${API_BASE_URL}/permission/checkPermission?route=/item/${itemNumber}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Accept': '*/*'
        }
      });
      
      if (!response.ok) {
        return false;
      }
      
      const responseText = await response.text();
      return responseText.includes('User has Permission');
    } catch (error) {
      console.error(`Error checking permission for item ${itemNumber}:`, error);
      return false;
    }
  },

  // Check if user has permission to add items
  async checkAddItemPermission(): Promise<boolean> {
    try {
      const token = getAuthToken();
      if (!token) {
        return false;
      }

      const response = await fetch(`${API_BASE_URL}/permission/checkPermission?route=/addItem`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Accept': '*/*'
        }
      });
      
      if (!response.ok) {
        return false;
      }
      
      const responseText = await response.text();
      return responseText.includes('User has Permission');
    } catch (error) {
      console.error('Error checking add item permission:', error);
      return false;
    }
  },

  // Get user's items
  async getUserItems(page = 0, size = 20): Promise<ItemsResponse> {
    try {
      const token = getAuthToken();
      if (!token) {
        throw new Error("Authentication required");
      }
      
      const response = await fetch(`${API_BASE_URL}/item/getAllUserItems?page=${page}&size=${size}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Accept': '*/*'
        }
      });
      
      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error("Error fetching user items:", error);
      throw error;
    }
  },

  // Add a new item
  async addItem(
    itemData: {
      name: string;
      category: string;
      colorGroups: string[];
      price: number;
      amount: number;
      image?: File | null;
      object?: File | null;
    }
  ): Promise<Item> {
    try {
      const token = getAuthToken();
      if (!token) {
        throw new Error("Authentication required");
      }

      // Create URL with required parameters
      let url = new URL(`${API_BASE_URL}/item/addItem`);
      url.searchParams.append('name', itemData.name);
      url.searchParams.append('category', itemData.category);
      url.searchParams.append('price', itemData.price.toString());
      url.searchParams.append('amount', itemData.amount.toString());
      
      // Add color groups
      itemData.colorGroups.forEach(color => {
        url.searchParams.append('colorGroups', color);
      });
      
      const formData = new FormData();
      
      // Add files if they exist
      if (itemData.image) formData.append('image', itemData.image);
      if (itemData.object) formData.append('object', itemData.object);

      const response = await fetch(url.toString(), {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Accept': '*/*',
          // Don't set Content-Type here as it's automatically set with the correct boundary when using FormData
        },
        body: formData,
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `Failed to add item: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error(`Error adding item:`, error);
      throw error;
    }
  },

  // Edit an item
  async editItem(
    itemNumber: string, 
    itemData: {
      name?: string;
      category?: string;
      colorGroups?: string[];
      price?: number;
      amount?: number;
      image?: File | null;
      object?: File | null;
    }
  ): Promise<Item> {
    try {
      const token = getAuthToken();
      if (!token) {
        throw new Error("Authentication required");
      }

      const formData = new FormData();
      
      // Add all the item data to the form
      if (itemData.name) formData.append('name', itemData.name);
      if (itemData.category) formData.append('category', itemData.category);
      if (itemData.price !== undefined) formData.append('price', itemData.price.toString());
      if (itemData.amount !== undefined) formData.append('amount', itemData.amount.toString());
      
      // Handle color groups - backend expects multiple values with the same key
      if (itemData.colorGroups && itemData.colorGroups.length > 0) {
        itemData.colorGroups.forEach(color => {
          formData.append('colorGroups', color);
        });
      }
      
      // Add files if they exist
      if (itemData.image) formData.append('image', itemData.image);
      if (itemData.object) formData.append('object', itemData.object);
      
      const response = await fetch(`${API_BASE_URL}/item/editItem?itemNumber=${itemNumber}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Accept': '*/*',
          // Don't set Content-Type here as it's automatically set with the correct boundary when using FormData
        },
        body: formData,
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `Failed to edit item: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error(`Error editing item ${itemNumber}:`, error);
      throw error;
    }
  },

  async deleteItem(itemNumber: string, password: string): Promise<string> {
    try {
      const token = getAuthToken();
      if (!token) {
        throw new Error("Authentication required");
      }

      const response = await fetch(
        `${API_BASE_URL}/item/deleteItem?itemNumber=${itemNumber}&password=${password}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || "Failed to delete item");
      }

      return await response.text();
    } catch (error) {
      console.error("Error deleting item:", error);
      throw error;
    }
  },

  async deleteImageFromItem(itemNumber: string): Promise<string> {
    try {
      const token = getAuthToken();
      if (!token) {
        throw new Error("Authentication required");
      }

      const response = await fetch(
        `${API_BASE_URL}/item/deleteImageFromItem?itemNumber=${itemNumber}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || "Failed to delete image from item");
      }

      return await response.text();
    } catch (error) {
      console.error("Error deleting image from item:", error);
      throw error;
    }
  }

};