import React, { createContext, useContext, useState, useEffect } from "react";
import { Item } from "@/types";
import { toast } from "@/hooks/use-toast";

export interface CartItem extends Item {
  quantity: number;
}

interface CartContextType {
  cartItems: CartItem[];
  addToCart: (item: Item, quantity?: number) => void;
  removeFromCart: (itemId: string) => void;
  updateQuantity: (itemId: string, quantity: number) => void;
  clearCart: () => void;
  getTotalItems: () => number;
  getTotalPrice: () => number;
  isInCart: (itemId: string) => boolean;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export function CartProvider({ children }: { children: React.ReactNode }) {
  const [cartItems, setCartItems] = useState<CartItem[]>([]);

  // Load cart from localStorage on initial render
  useEffect(() => {
    const savedCart = localStorage.getItem("cart");
    if (savedCart) {
      try {
        setCartItems(JSON.parse(savedCart));
      } catch (error) {
        console.error("Failed to parse cart from localStorage:", error);
        localStorage.removeItem("cart");
      }
    }
  }, []);

  // Save cart to localStorage whenever it changes
  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(cartItems));
  }, [cartItems]);

  const addToCart = (item: Item, quantity: number = 1) => {
    if (item.amount === 0) {
      toast({
        variant: "destructive",
        title: "Out of stock",
        description: `${item.name} is currently out of stock.`
      });
      return;
    }

    setCartItems(prevItems => {
      const existingItemIndex = prevItems.findIndex(cartItem => cartItem.id === item.id);
      
      if (existingItemIndex !== -1) {
        // Item already in cart, update quantity
        const updatedItems = [...prevItems];
        const newQuantity = updatedItems[existingItemIndex].quantity + quantity;
        
        // Check if requested quantity exceeds available stock
        if (newQuantity > item.amount) {
          toast({
            variant: "destructive",
            title: "Limited stock",
            description: `Only ${item.amount} units of ${item.name} are available.`
          });
          updatedItems[existingItemIndex].quantity = item.amount;
        } else {
          updatedItems[existingItemIndex].quantity = newQuantity;
        }
        
        return updatedItems;
      } else {
        // Add new item to cart
        // Check if requested quantity exceeds available stock
        const safeQuantity = Math.min(quantity, item.amount);
        if (safeQuantity < quantity) {
          toast({
            variant: "destructive",
            title: "Limited stock",
            description: `Only ${item.amount} units of ${item.name} are available.`
          });
        }
        
        toast({
          title: "Added to cart",
          description: `${item.name} has been added to your cart.`
        });
        
        return [...prevItems, { ...item, quantity: safeQuantity }];
      }
    });
  };

  const removeFromCart = (itemId: string) => {
    setCartItems(prevItems => {
      const updatedItems = prevItems.filter(item => item.id !== itemId);
      return updatedItems;
    });
  };

  const updateQuantity = (itemId: string, quantity: number) => {
    setCartItems(prevItems => {
      return prevItems.map(item => {
        if (item.id === itemId) {
          // Ensure quantity doesn't exceed available stock
          const safeQuantity = Math.min(quantity, item.amount);
          if (safeQuantity < quantity) {
            toast({
              variant: "destructive",
              title: "Limited stock",
              description: `Only ${item.amount} units of ${item.name} are available.`
            });
          }
          return { ...item, quantity: safeQuantity };
        }
        return item;
      });
    });
  };

  const clearCart = () => {
    setCartItems([]);
    localStorage.removeItem("cart");
  };

  const getTotalItems = () => {
    return cartItems.reduce((total, item) => total + item.quantity, 0);
  };

  const getTotalPrice = () => {
    return cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
  };

  const isInCart = (itemId: string) => {
    return cartItems.some(item => item.id === itemId);
  };

  return (
    <CartContext.Provider 
      value={{ 
        cartItems, 
        addToCart, 
        removeFromCart, 
        updateQuantity, 
        clearCart, 
        getTotalItems, 
        getTotalPrice,
        isInCart 
      }}
    >
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  const context = useContext(CartContext);
  if (context === undefined) {
    throw new Error("useCart must be used within a CartProvider");
  }
  return context;
}