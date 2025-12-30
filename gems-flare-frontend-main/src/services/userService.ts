import { API_BASE_URL } from "./apiConfig";
import { getAuthToken } from "./authHelpers";
import { authenticatedFetch } from "./fetchHelpers";
import { UserProfile, DeliveryAddress, BillAddress } from "@/types";

export const userService = {
  // Get user profile
  async getUserProfile(userId: string): Promise<UserProfile> {
    return await authenticatedFetch(`${API_BASE_URL}/user/getUserById/${userId}`);
  },

  // Edit user profile
  async editUserProfile(profileData: {
    username?: string;
    name?: string;
    lastName?: string;
    email?: string;
    telephone?: string;
  }) {
    const token = getAuthToken();
    if (!token) {
      throw new Error("Authentication required");
    }

    let urlString = `${API_BASE_URL}/user/editMyProfile`;
    const params = new URLSearchParams();

    if (profileData.username) params.append('username', profileData.username);
    if (profileData.name) params.append('name', profileData.name);
    if (profileData.lastName) params.append('lastName', profileData.lastName);
    if (profileData.email) params.append('email', profileData.email);
    if (profileData.telephone) params.append('telephone', profileData.telephone);

    const finalUrl = `${urlString}?${params.toString()}`;

    const response = await fetch(finalUrl, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Accept': '*/*',
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || `Failed to update profile: ${response.status}`);
    }

    return await response.text();
  },

  // Delete user profile
  async deleteMyUser(password: string) {
    const token = getAuthToken();
    if (!token) {
      throw new Error("Authentication required");
    }
  
    let urlString = `${API_BASE_URL}/user/deleteMyUser`;
    const params = new URLSearchParams();
    params.append("password", password);
    const finalUrl = `${urlString}?${params.toString()}`;
  
    const response = await fetch(finalUrl, {
      method: "DELETE",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Accept": "*/*",
        "Content-Type": "application/json",
      },
    });
  
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || `Failed to delete account: ${response.status}`);
    }
  
    return await response.text();
  },

  //change password
  async changePassword(oldPassword: string, newPassword: string) {
    const token = getAuthToken();
    if (!token) {
      throw new Error("Authentication required");
    }

    const response = await fetch(`${API_BASE_URL}/user/changeMyPassword?oldPassword=${oldPassword}&newPassword=${newPassword}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || `Failed to change password: ${response.status}`);
    }

    return await response.text();
  },

  // Add shipping address
  async addShippingAddress(addressData: DeliveryAddress) {
    const token = getAuthToken();
    if (!token) {
      throw new Error("Authentication required");
    }

    const response = await fetch(`${API_BASE_URL}/deliveryAddress/addMyDeliveryAddress`, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json",
        "Accept": "*/*",
      },
      body: JSON.stringify(addressData),
    });

    if (!response.ok) {
      const errorText = await response.text().catch(() => null);
      throw new Error(errorText || `Failed to add shipping address: ${response.status}`);
    }

    return await response.text().catch(() => "Shipping address added successfully");
  },

  // Get shipping address
  async getShippingAddress(): Promise<DeliveryAddress> {
    return await authenticatedFetch(`${API_BASE_URL}/deliveryAddress/getMyDeliveryAddress`);
  },

  // Edit shipping address
  async editShippingAddress(addressData: DeliveryAddress) {
    const token = getAuthToken();
    if (!token) {
      throw new Error("Authentication required");
    }

    const response = await fetch(`${API_BASE_URL}/deliveryAddress/editMyDeliveryAddress`, {
      method: "PUT",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json",
        "Accept": "*/*",
      },
      body: JSON.stringify(addressData),
    });

    if (!response.ok) {
      const errorText = await response.text().catch(() => null);
      throw new Error(errorText || `Failed to edit shipping address: ${response.status}`);
    }

    return await response.text().catch(() => "Shipping address updated successfully");
  },

  // Remove shipping address
  async removeShippingAddress() {
    const token = getAuthToken();
    if (!token) {
      throw new Error("Authentication required");
    }

    let url = `${API_BASE_URL}/deliveryAddress/removeMyDeliveryAddress`;
    const response = await fetch(url, {
      method: "DELETE",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Accept": "*/*",
        "Content-Type": "application/json",
      },
    });
  
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || `Failed to remove delivery address: ${response.status}`);
    }
  
    return await response.text();
  },

  // Add billing address
  async addBillingAddress(addressData: BillAddress) {
    const token = getAuthToken();
    if (!token) {
      throw new Error("Authentication required");
    }

    const response = await fetch(`${API_BASE_URL}/billAddress/addMyBillAddress`, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json",
        "Accept": "*/*",
      },
      body: JSON.stringify(addressData),
    });

    if (!response.ok) {
      const errorText = await response.text().catch(() => null);
      throw new Error(errorText || `Failed to add billing address: ${response.status}`);
    }

    return await response.text().catch(() => "Billing address added successfully");
  },

  // Get billing address
  async getBillingAddress(): Promise<BillAddress> {
    return await authenticatedFetch(`${API_BASE_URL}/billAddress/getMyBillAddress`);
  },

  // Edit billing address
  async editBillingAddress(addressData: BillAddress) {
    const token = getAuthToken();
    if (!token) {
      throw new Error("Authentication required");
    }

    const response = await fetch(`${API_BASE_URL}/billAddress/editMyBillAddress`, {
      method: "PUT",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json",
        "Accept": "*/*",
      },
      body: JSON.stringify(addressData),
    });

    if (!response.ok) {
      const errorText = await response.text().catch(() => null);
      throw new Error(errorText || `Failed to edit billing address: ${response.status}`);
    }

    return await response.text().catch(() => "Billing address updated successfully");
  },

  // Remove billing address
  async removeBillingAddress() {
    const token = getAuthToken();
    if (!token) {
      throw new Error("Authentication required");
    }

    let url = `${API_BASE_URL}/billAddress/removeMyBillAddress`;
    const response = await fetch(url, {
      method: "DELETE",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Accept": "*/*",
        "Content-Type": "application/json",
      },
    });
  
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || `Failed to remove bill address: ${response.status}`);
    }
  
    return await response.text();
  }
  
};