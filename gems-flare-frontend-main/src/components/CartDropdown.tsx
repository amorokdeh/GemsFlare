import { useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useCart } from "@/context/CartContext";
import { useOnClickOutside } from "@/hooks/use-click-outside";
import { formatCurrency } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { Plus, Minus, X, ShoppingCart, ShoppingBag } from "lucide-react";

export function CartDropdown() {
  const [isOpen, setIsOpen] = useState(false);
  const cartDropdownRef = useRef<HTMLDivElement>(null);
  const { 
    cartItems, 
    removeFromCart, 
    updateQuantity, 
    getTotalItems, 
    getTotalPrice 
  } = useCart();
  const navigate = useNavigate();

  useOnClickOutside(cartDropdownRef, () => setIsOpen(false));

  const totalItems = getTotalItems();
  const totalPrice = getTotalPrice();

  const handleCheckout = () => {
    setIsOpen(false);
    navigate("/checkout");
  };

  return (
    <div className="relative" ref={cartDropdownRef}>
      <Button 
        variant="outline" 
        size="icon" 
        className="relative border-green-700/50 bg-gray-900 text-gray-200 hover:bg-gray-800"
        onClick={() => setIsOpen(!isOpen)}
      >
        <ShoppingCart size={20} />
        {totalItems > 0 && (
          <span className="absolute -top-2 -right-2 bg-green-500 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">
            {totalItems}
          </span>
        )}
      </Button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 bg-gray-900 border border-green-700/30 rounded-lg shadow-lg z-50">
          <div className="p-4">
            <div className="flex justify-between items-center mb-4">
              <h3 className="font-semibold text-white">Your Cart</h3>
              <Button 
                variant="ghost" 
                size="icon" 
                className="h-8 w-8 text-gray-400 hover:text-white"
                onClick={() => setIsOpen(false)}
              >
                <X size={16} />
              </Button>
            </div>

            {cartItems.length === 0 ? (
              <div className="py-8 text-center">
                <ShoppingBag className="mx-auto mb-4 h-12 w-12 text-gray-400" />
                <p className="text-gray-400">Your cart is empty</p>
                <Button
                  variant="outline"
                  className="mt-4 border-green-700/50 text-green-500 hover:bg-gray-800"
                  onClick={() => {
                    setIsOpen(false);
                    navigate("/");
                  }}
                >
                  Continue Shopping
                </Button>
              </div>
            ) : (
              <>
                <ScrollArea className="max-h-[60vh]">
                  <div className="space-y-4">
                    {cartItems.map(item => (
                      <div key={item.id} className="flex gap-2">
                        <div className="h-16 w-16 rounded overflow-hidden flex-shrink-0">
                          <img 
                            src={item.img_src || '/placeholder.svg'} 
                            alt={item.name} 
                            className="h-full w-full object-cover"
                            onError={(e) => {
                              (e.target as HTMLImageElement).src = "/placeholder.svg";
                            }}
                          />
                        </div>
                        <div className="flex-1 flex flex-col justify-between">
                          <div className="flex justify-between">
                            <Link
                              to={`/item/${item.number}`}
                              className="text-sm font-medium text-white hover:text-green-400 line-clamp-2"
                              onClick={() => setIsOpen(false)}
                            >
                              {item.name}
                              {item.selected_colors && Object.entries(item.selected_colors).length > 0 && (
                                <div className="text-xs text-gray-400 mt-1">
                                  {Object.entries(item.selected_colors).map(([group, color]) => (
                                    <span key={group} className="block">
                                      {group}: {color}
                                    </span>
                                  ))}
                                </div>
                              )}
                            </Link>
                            <Button
                              variant="ghost"
                              size="icon"
                              className="h-6 w-6 text-gray-400 hover:text-white"
                              onClick={() => removeFromCart(item.id)}
                            >
                              <X size={14} />
                            </Button>
                          </div>
                          <div className="flex justify-between items-center">
                            <div className="flex items-center border border-green-700/30 rounded-md">
                              <Button
                                variant="ghost"
                                size="icon"
                                className="h-7 w-7 text-gray-400 hover:text-white rounded-r-none"
                                onClick={() => updateQuantity(item.id, Math.max(1, item.quantity - 1))}
                                disabled={item.quantity <= 1}
                              >
                                <Minus size={14} />
                              </Button>
                              <span className="w-8 text-center text-sm text-white">
                                {item.quantity}
                              </span>
                              <Button
                                variant="ghost"
                                size="icon"
                                className="h-7 w-7 text-gray-400 hover:text-white rounded-l-none"
                                onClick={() => updateQuantity(item.id, item.quantity + 1)}
                                disabled={item.quantity >= item.amount}
                              >
                                <Plus size={14} />
                              </Button>
                            </div>
                            <span className="text-sm font-medium text-white">
                              {formatCurrency(item.price * item.quantity)}
                            </span>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </ScrollArea>

                <Separator className="my-4 bg-green-700/30" />
                
                <div className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-400">Subtotal</span>
                    <span className="text-white">{formatCurrency(totalPrice)}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-400">Shipping</span>
                    <span className="text-white">Calculated at checkout</span>
                  </div>
                  <Separator className="my-2 bg-green-700/30" />
                  <div className="flex justify-between font-semibold">
                    <span className="text-white">Total</span>
                    <span className="text-white">{formatCurrency(totalPrice)}</span>
                  </div>
                  
                  <div className="grid grid-cols-2 gap-2 mt-4">
                    <Button
                      variant="outline"
                      className="w-full border-green-700/50 text-white hover:bg-gray-800"
                      onClick={() => {
                        setIsOpen(false);
                        navigate("/cart");
                      }}
                    >
                      View Cart
                    </Button>
                    <Button
                      className="w-full bg-green-600 hover:bg-green-700 text-white"
                      onClick={handleCheckout}
                    >
                      Checkout
                    </Button>
                  </div>
                </div>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
}