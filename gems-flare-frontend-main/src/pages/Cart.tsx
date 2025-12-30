import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useCart } from "@/context/CartContext";
import { formatCurrency } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Separator } from "@/components/ui/separator";
import { 
  ShoppingCart, 
  Trash2, 
  Plus, 
  Minus, 
  ArrowLeft,
  CreditCard
} from "lucide-react";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";

const Cart = () => {
  const { 
    cartItems, 
    removeFromCart, 
    updateQuantity, 
    getTotalItems, 
    getTotalPrice,
    clearCart
  } = useCart();
  const navigate = useNavigate();
  const [couponCode, setCouponCode] = useState("");

  const totalItems = getTotalItems();
  const subtotal = getTotalPrice();
  const shipping = subtotal > 0 ? 0 : 0;
  const total = subtotal + shipping;

  const handleApplyCoupon = (e: React.FormEvent) => {
    e.preventDefault();
    // In a real app, you would validate the coupon code here
    alert(`Coupon ${couponCode} applied!`);
  };

  const handleCheckout = () => {
    navigate("/checkout");
  };

  if (cartItems.length === 0) {
    return (
      <>
        <Navbar />
        <div className="container mx-auto px-4 py-8 min-h-[calc(100vh-200px)]">
          <div className="text-center max-w-lg mx-auto py-16">
            <ShoppingCart className="h-24 w-24 mx-auto mb-6 text-muted-foreground" />
            <h1 className="text-2xl font-bold mb-4">Your cart is empty</h1>
            <p className="text-muted-foreground mb-8">
              Looks like you haven't added any items to your cart yet.
            </p>
            <Button asChild size="lg">
              <Link to="/">Continue Shopping</Link>
            </Button>
          </div>
        </div>
        <Footer />
      </>
    );
  }

  return (
    <>
      <Navbar />
      <div className="container mx-auto px-4 py-8 min-h-[calc(100vh-200px)]">
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-2xl font-bold">Shopping Cart</h1>
          <div className="flex items-center gap-2">
            <Button 
              variant="ghost" 
              onClick={() => navigate(-1)}
              className="hidden sm:flex items-center gap-1"
            >
              <ArrowLeft className="h-4 w-4" />
              Continue Shopping
            </Button>
            <Button 
              variant="destructive" 
              size="sm"
              onClick={clearCart}
              className="flex items-center gap-1"
            >
              <Trash2 className="h-4 w-4" />
              <span className="hidden sm:inline">Clear Cart</span>
            </Button>
          </div>
        </div>

        <div className="grid md:grid-cols-3 gap-8">
          <div className="md:col-span-2 space-y-4">
            <div className="bg-background rounded-lg border shadow-sm">
              <div className="p-4 sm:p-6">
                <div className="hidden sm:grid grid-cols-6 gap-4 mb-4 font-medium text-muted-foreground">
                  <span className="col-span-3">Product</span>
                  <span className="text-center">Price</span>
                  <span className="text-center">Quantity</span>
                  <span className="text-right">Total</span>
                </div>

                <Separator className="mb-4" />

                {cartItems.map(item => (
                  <div key={item.id} className="py-4">
                    <div className="grid sm:grid-cols-6 gap-4 items-center">
                      <div className="col-span-3 flex gap-4 items-center">
                        <div className="h-20 w-20 rounded overflow-hidden bg-muted flex-shrink-0">
                          <img 
                            src={item.img_src || '/placeholder.svg'} 
                            alt={item.name} 
                            className="h-full w-full object-cover"
                            onError={(e) => {
                              (e.target as HTMLImageElement).src = "/placeholder.svg";
                            }}
                          />
                        </div>
                        <div>
                          <Link
                            to={`/item/${item.number}`}
                            className="font-medium hover:text-primary line-clamp-2"
                          >
                            {item.name}
                          </Link>
                          <div className="text-sm text-muted-foreground mt-1">
                            {item.category}
                            {item.selected_colors && Object.entries(item.selected_colors).length > 0 && (
                              <div className="mt-1">
                                {Object.entries(item.selected_colors).map(([group, color]) => (
                                  <span key={group} className="block">
                                    {group}: {color}
                                  </span>
                                ))}
                              </div>
                            )}
                          </div>
                          <div className="mt-2 sm:hidden">
                            <span className="font-medium">
                              {formatCurrency(item.price)}
                            </span>
                          </div>
                        </div>
                      </div>

                      <div className="hidden sm:flex justify-center">
                        <span>{formatCurrency(item.price)}</span>
                      </div>

                      <div className="flex items-center justify-center space-x-2">
                        <div className="flex items-center border rounded-md">
                          <Button
                            variant="ghost"
                            size="icon"
                            className="h-8 w-8 rounded-r-none"
                            onClick={() => updateQuantity(item.id, Math.max(1, item.quantity - 1))}
                            disabled={item.quantity <= 1}
                          >
                            <Minus className="h-4 w-4" />
                          </Button>
                          <span className="w-10 text-center">{item.quantity}</span>
                          <Button
                            variant="ghost"
                            size="icon"
                            className="h-8 w-8 rounded-l-none"
                            onClick={() => updateQuantity(item.id, item.quantity + 1)}
                            disabled={item.quantity >= item.amount}
                          >
                            <Plus className="h-4 w-4" />
                          </Button>
                        </div>
                      </div>

                      <div className="flex items-center justify-between sm:justify-end">
                        <span className="font-medium sm:ml-auto">
                          {formatCurrency(item.price * item.quantity)}
                        </span>
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-8 w-8 text-muted-foreground hover:text-destructive ml-4"
                          onClick={() => removeFromCart(item.id)}
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>
                    <Separator className="mt-4" />
                  </div>
                ))}
              </div>
            </div>
            
            {/* Continue Shopping - Mobile only */}
            <div className="sm:hidden">
              <Button 
                variant="outline" 
                className="w-full"
                onClick={() => navigate(-1)}
              >
                <ArrowLeft className="h-4 w-4 mr-2" />
                Continue Shopping
              </Button>
            </div>
          </div>

          <div className="space-y-4">
            <div className="bg-background rounded-lg border shadow-sm p-6">
              <h2 className="text-lg font-semibold mb-4">Order Summary</h2>

              <div className="space-y-2">
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Subtotal ({totalItems} items)</span>
                  <span>{formatCurrency(subtotal)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Shipping</span>
                  <span>{shipping > 0 ? formatCurrency(shipping) : "Free"}</span>
                </div>
                <Separator className="my-4" />
                <div className="flex justify-between font-bold">
                  <span>Total</span>
                  <span>{formatCurrency(total)}</span>
                </div>
              </div>

              <Button
                className="w-full mt-6"
                size="lg"
                onClick={handleCheckout}
              >
                <CreditCard className="h-4 w-4 mr-2" />
                Proceed to Checkout
              </Button>
            </div>

            <div className="bg-background rounded-lg border shadow-sm p-6">
              <h3 className="text-md font-semibold mb-3">Have a coupon?</h3>
              <form onSubmit={handleApplyCoupon} className="flex gap-2">
                <Input
                  type="text"
                  placeholder="Enter code"
                  value={couponCode}
                  onChange={(e) => setCouponCode(e.target.value)}
                  className="flex-1"
                />
                <Button type="submit" variant="outline">Apply</Button>
              </form>
            </div>
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
};

export default Cart;