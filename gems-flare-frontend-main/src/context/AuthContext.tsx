import React, { createContext, useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { authService } from "@/services/authService";
import { toast } from "@/hooks/use-toast";

type User = {
  id: string;
  username: string;
  name: string;
  lastname: string;
  token: string;
  role?: string;
};

interface AuthContextType {
  user: User | null;
  login: (usernameOrEmail: string, password: string) => Promise<void>;
  register: (username: string, name: string, lastName: string, email: string, telephone: string, password: string) => Promise<void>;
  logout: () => void;
  isLoading: boolean;
  error: string | null;
  checkRole: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true); // Start with loading true
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  // Check token validity on initial load and set up timer to check periodically
  useEffect(() => {
    const validateUserSession = async () => {
      setIsLoading(true);
      const storedUser = localStorage.getItem("user");
      
      if (storedUser) {
        const parsedUser = JSON.parse(storedUser);
        
        try {
          // Check if token is valid
          const isValid = await authService.checkTokenValidity();
          
          if (isValid) {
            setUser(parsedUser);
            
            // Check user role if not already set
            if (!parsedUser.role) {
              checkRole();
            }
          } else {
            // Token is invalid - log the user out
            console.log("Invalid or expired token detected. Logging out.");
            logout();
          }
        } catch (error) {
          console.error("Error validating token:", error);
          logout();
        } finally {
          setIsLoading(false);
        }
      } else {
        setIsLoading(false);
      }
    };

    validateUserSession();
    
    // Set up periodic token validation (every 15 minutes)
    const tokenCheckInterval = setInterval(async () => {
      if (user) {
        try {
          const isValid = await authService.checkTokenValidity();
          if (!isValid) {
            console.log("Token expired during session. Logging out.");
            logout();
          }
        } catch (error) {
          console.error("Error during periodic token validation:", error);
        }
      }
    }, 15 * 60 * 1000); // Check every 15 minutes
    
    return () => clearInterval(tokenCheckInterval);
  }, []);

  // Check and update user role
  const checkRole = async () => {
    if (!user) return;
    
    try {
      const role = await authService.getUserRole();
      if (role) {
        const updatedUser = { ...user, role };
        setUser(updatedUser);
        localStorage.setItem("user", JSON.stringify(updatedUser));
      }
    } catch (err) {
      console.error("Error checking user role:", err);
    }
  };

  const login = async (usernameOrEmail: string, password: string) => {
    setIsLoading(true);
    setError(null);
    
    try {
      const userData = await authService.login(usernameOrEmail, password);
      
      // Save user data to state and localStorage
      setUser(userData);
      localStorage.setItem("user", JSON.stringify(userData));
      
      // Show success notification
      toast({
        title: "Login successful",
        description: `Welcome back, ${userData.name}!`
      });
      
      // Redirect to home page
      navigate("/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "An unknown error occurred");
      
      // Show error notification
      toast({
        title: "Login failed",
        description: err instanceof Error ? err.message : "An unknown error occurred",
        variant: "destructive"
      });
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (username: string, name: string, lastName: string, email: string, telephone: string, password: string) => {
    setIsLoading(true);
    setError(null);
    
    try {
      const userData = await authService.register(username, name, lastName, email, telephone, password);
      
      // Save user data to state and localStorage
      setUser(userData);
      localStorage.setItem("user", JSON.stringify(userData));
      
      // Show success notification
      toast({
        title: "Registration successful",
        description: `Welcome, ${userData.name}!`
      });
      
      // Redirect to home page
      navigate("/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "An unknown error occurred");
      
      // Show error notification
      toast({
        title: "Registration failed",
        description: err instanceof Error ? err.message : "An unknown error occurred",
        variant: "destructive"
      });
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem("user");
    
    // Show notification
    toast({
      title: "Logged out",
      description: "You have been logged out successfully"
    });
    
    navigate("/");
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, isLoading, error, checkRole }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
