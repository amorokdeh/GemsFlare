import { API_BASE_URL } from "./apiConfig";

export const invoiceService = {
    getInvoiceByOrderNumber: async (orderNumber: string) => {
      const response = await fetch(
        `${API_BASE_URL}/invoice/getInvoicePdfByOrderNumber/${orderNumber}`,
        {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${localStorage.getItem("token") || ""}`,
            "Accept": "application/pdf",
          },
        }
      );
  
      if (!response.ok) {
        throw new Error("Failed to fetch invoice");
      }
  
      return response.blob();
    },
  };  
