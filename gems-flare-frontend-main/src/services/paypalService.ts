import { API_BASE_URL } from "./apiConfig";
import { getAuthToken } from "./authHelpers";

export const paypalService = {
    // Create a PayPal order
    async createOrder(checkoutNumber: string): Promise<{orderID: string, approvalUrl: string, status: string}> {
      try {
        const token = getAuthToken();
        if (!token) {
          throw new Error("Authentication required");
        }
  
        const baseUrl = window.location.origin;
        const returnUrl = `${baseUrl}/return`;
        const cancelUrl = `${baseUrl}/cancel`;
  
        const response = await fetch(`${API_BASE_URL}/api/paypal/create-order?checkoutNumber=${checkoutNumber}&returnUrl=${encodeURIComponent(returnUrl)}&cancelUrl=${encodeURIComponent(cancelUrl)}`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Accept': '*/*'
          }
        });
        
        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(errorText || `Failed to create PayPal order: ${response.status}`);
        }
        
        return await response.json();
      } catch (error) {
        console.error(`Error creating PayPal order:`, error);
        throw error;
      }
    },
  
    // Capture (confirm) a PayPal order
    async captureOrder(orderId: string, checkoutNumber: string): Promise<{payer: any, status: string, orderId: string}> {
      try {
        const token = getAuthToken();
        if (!token) {
          throw new Error("Authentication required");
        }
  
        const response = await fetch(`${API_BASE_URL}/api/paypal/capture-order?orderId=${orderId}&checkoutNumber=${checkoutNumber}`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Accept': '*/*'
          }
        });
        
        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(errorText || `Failed to capture PayPal order: ${response.status}`);
        }
        
        return await response.json();
      } catch (error) {
        console.error(`Error capturing PayPal order:`, error);
        throw error;
      }
    }
  };