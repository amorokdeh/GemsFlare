import { API_BASE_URL } from "./apiConfig";
import { getAuthToken } from "./authHelpers";
import { Checkout } from "@/types";

export const checkoutService = {
  addCheckout: async (checkoutData: Partial<Checkout>): Promise<Checkout> => {
    
    try {
      const token = getAuthToken();
      if (!token) {
        throw new Error("Authentication required");
      }

      const response = await fetch(`${API_BASE_URL}/checkout/addCheckout`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
          'Accept': '*/*'
        },
        body: JSON.stringify(checkoutData)
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        console.error('Checkout error response:', errorText);
        throw new Error(`Failed to create checkout: ${response.status} ${response.statusText}`);
      }
      
      return response.json();

    } catch (error) {
      console.error(`Error capturing checkout:`, error);
      throw error;
    }

  },
  
  getCheckout: async (checkoutNumber: string): Promise<Checkout> => {
    const response = await fetch(`${API_BASE_URL}/checkout/getCheckout/${checkoutNumber}`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    
    if (!response.ok) {
      throw new Error('Failed to retrieve checkout');
    }
    
    return response.json();
  }
}