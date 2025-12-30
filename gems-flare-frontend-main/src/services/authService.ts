import { API_BASE_URL } from "./apiConfig";
import { getAuthToken } from "./authHelpers";
import { authenticatedFetch } from "./fetchHelpers";
import { TokenInfo } from "@/types";

export const authService = {
  // Check token validity
  async checkTokenValidity(): Promise<boolean> {
    try {
      const token = getAuthToken();
      if (!token) {
        return false;
      }

      const response = await fetch(`${API_BASE_URL}/user/checkToken`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Accept': '*/*'
        }
      });
      
      if (!response.ok) {
        return false;
      }
      
      const tokenInfo: TokenInfo = await response.json();
      return tokenInfo.isValid;
    } catch (error) {
      console.error("Error checking token validity:", error);
      return false;
    }
  },

  // Login
  async login(usernameOrEmail: string, password: string) {
    // Create form data for sending as URL encoded form
    const formData = new URLSearchParams();
    formData.append('usernameOrEmail', usernameOrEmail);
    formData.append('password', password);
    
    const response = await fetch(`${API_BASE_URL}/user/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: formData,
    });
    
    if (!response.ok) {
      const errorData = await response.json().catch(() => null);
      throw new Error(errorData || "Login failed. Please check your credentials.");
    }
    
    const userData = await response.json();
    
    // Get user profile data after login to get the role
    try {
      const userResponse = await fetch(`${API_BASE_URL}/user/getUserById/${userData.id}`, {
        headers: {
          'Authorization': `Bearer ${userData.token}`,
          'Accept': '*/*'
        }
      });
      
      if (userResponse.ok) {
        const userProfile = await userResponse.json();
        userData.role = userProfile.role?.toUpperCase();
      }
    } catch (error) {
      console.error("Error fetching user profile:", error);
    }
    
    return userData;
  },
  
  // Register
  async register(username: string, name: string, lastName: string, email: string, telephone: string, password: string) {
    // Create URL with all parameters
    const url = new URL(`${API_BASE_URL}/user/addNewUser`);
    url.searchParams.append('username', username);
    url.searchParams.append('name', name);
    url.searchParams.append('lastName', lastName);
    url.searchParams.append('email', email);
    if (telephone) url.searchParams.append('telephone', telephone);
    url.searchParams.append('password', password);
    
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Accept': '*/*',
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });
    
    if (!response.ok) {
      const errorData = await response.text();
      throw new Error(errorData || "Registration failed. Please try again.");
    }
    
    return response.json();
  },
  
  // Get current user profile
  async getCurrentUser() {
    return await authenticatedFetch(`${API_BASE_URL}/user/profile`);
  },

  // Get user role by fetching user profile
  async getUserRole(): Promise<string> {
    try {
      const token = getAuthToken();
      if (!token) {
        return "";
      }

      const user = localStorage.getItem("user");
      if (!user) {
        return "";
      }
      
      const { id } = JSON.parse(user);
      
      const response = await fetch(`${API_BASE_URL}/user/getUserById/${id}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Accept': '*/*'
        }
      });
      
      if (!response.ok) {
        return "";
      }
      
      const userProfile = await response.json();
      return userProfile.role?.toUpperCase() || "";
    } catch (error) {
      console.error("Error fetching user role:", error);
      return "";
    }
  }
  
};