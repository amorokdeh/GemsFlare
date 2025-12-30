import { API_BASE_URL } from "./apiConfig";
import { getAuthToken } from "./authHelpers";

export const orderService = {
  getOrdersByUserId: async (userId: string, page = 0, size = 20) => {
    const response = await fetch(
      `${API_BASE_URL}/order/getOrdersByUserid/${userId}?page=${page}&size=${size}`,
      {
        headers: {
          "Authorization": `Bearer ${localStorage.getItem("token") || ""}`,
          "Accept": "*/*",
        },
      }
    );

    if (!response.ok) {
      throw new Error("Failed to fetch orders");
    }

    return await response.json();
  },

  cancelOrder: async (orderNumber: string) => {
    const token = getAuthToken();
    if (!token) {
      throw new Error("Authentication required");
    }
  
    const response = await fetch(
      `${API_BASE_URL}/order/cancelOrder/${orderNumber}`,
      {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Accept": "*/*",
        },
      }
    );
  
    const text = await response.text();
  
    if (!response.ok) {
      throw new Error(text || "Failed to cancel order");
    }
  
    return text;
  },
};
