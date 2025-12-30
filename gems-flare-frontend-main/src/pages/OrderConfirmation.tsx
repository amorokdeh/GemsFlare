import { useNavigate, useLocation } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { CheckCircle, Home, Package, Loader2 } from "lucide-react";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";
import { useEffect, useState, useRef } from "react";
import { toast } from "@/hooks/use-toast";
import { useCart } from "@/context/CartContext";
import { paypalService } from "@/services/paypalService";
import { useAuth } from "@/context/AuthContext";

const OrderConfirmation = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { clearCart } = useCart();
  const { user } = useAuth();
  const [isProcessing, setIsProcessing] = useState(false);
  const [orderId, setOrderId] = useState("");
  const paymentProcessed = useRef(false);
  
  // Generate a random order number if we don't have one from PayPal
  const orderNumber = orderId || `GF-${Math.floor(100000 + Math.random() * 900000)}`;
  
  useEffect(() => {

    if (paymentProcessed.current) {
      return;
    }

    const searchParams = new URLSearchParams(location.search);
    const token = searchParams.get('token');
    const payerId = searchParams.get('PayerID');
    const checkoutNumber = localStorage.getItem('checkoutNumber');
    
    // If we have a token in the URL, it means we're coming from PayPal
    if (token && payerId && user) {
      setIsProcessing(true);
      paymentProcessed.current = true;
      
      const processPayment = async () => {
        try {
          const result = await paypalService.captureOrder(token, checkoutNumber);
          
          if (result.status === "COMPLETED") {
            setOrderId(result.orderId);
            clearCart();
            toast({
              title: "Payment successful",
              description: "Thank you for your purchase!",
            });
            
            // Clear the URL parameters after processing
            window.history.replaceState({}, document.title, "/order-confirmation");
          } else {
            toast({
              variant: "destructive",
              title: "Payment not completed",
              description: "Your payment was not completed. Please try again.",
            });
            navigate("/checkout");
          }
        } catch (error) {
          console.error("Error capturing PayPal order:", error);
          toast({
            variant: "destructive",
            title: "Payment verification failed",
            description: "We couldn't verify your payment. Please contact customer support.",
          });
          navigate("/checkout");
        } finally {
          setIsProcessing(false);
        }
      };
      
      processPayment();
    } else {
      if (location.search) {
        window.history.replaceState({}, document.title, "/order-confirmation");
      }
      // If no token, retrieve any pending order info from localStorage
      const pendingOrder = localStorage.getItem('pendingOrder');
      if (pendingOrder) {
        const orderData = JSON.parse(pendingOrder);
        setOrderId(orderData.orderID || "");
      // Clean up localStorage to prevent re-use
      localStorage.removeItem('pendingOrder');
    }
  }
  
  // This cleanup function runs when component unmounts or when dependencies change
  return () => {
    // Reset the payment processed flag when navigating away
    paymentProcessed.current = false;
  };
}, [user]);
  
  if (isProcessing) {
    return (
      <>
        <Navbar />
        <div className="container mx-auto px-4 py-16 text-center max-w-2xl">
          <div className="bg-background rounded-lg border shadow-md p-8">
            <Loader2 className="h-16 w-16 animate-spin mx-auto mb-6 text-primary" />
            <h1 className="text-3xl font-bold mb-4">Processing Your Order</h1>
            <p className="text-muted-foreground mb-6">
              Please wait while we confirm your payment with PayPal...
            </p>
          </div>
        </div>
        <Footer />
      </>
    );
  }
  
  return (
    <>
      <Navbar />
      <div className="container mx-auto px-4 py-16 text-center max-w-2xl">
        <div className="bg-background rounded-lg border shadow-md p-8">
          <div className="flex justify-center mb-6">
            <CheckCircle className="h-20 w-20 text-green-500" />
          </div>
          
          <h1 className="text-3xl font-bold mb-4">Order Confirmed!</h1>
          
          <p className="text-muted-foreground mb-6">
            Thank you for your purchase. Your order has been received and is being processed.
          </p>
          
          <div className="bg-muted p-6 rounded-lg mb-8">
            <div className="text-sm text-muted-foreground mb-2">Order Number</div>
            <div className="text-2xl font-mono font-bold">{orderNumber}</div>
            <div className="text-sm text-muted-foreground mt-4">
              A confirmation email has been sent to your email address.
            </div>
          </div>
          
          <div className="grid gap-2 sm:grid-cols-2 mb-8">
            <div className="border rounded-lg p-4 text-left">
              <h3 className="font-medium mb-2">Estimated Delivery</h3>
              <p className="text-muted-foreground">
                {new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toLocaleDateString('en-US', { 
                  weekday: 'long', 
                  month: 'long', 
                  day: 'numeric' 
                })}
              </p>
            </div>
            <div className="border rounded-lg p-4 text-left">
              <h3 className="font-medium mb-2">Payment Method</h3>
              <p className="text-muted-foreground">PayPal</p>
            </div>
          </div>
          
          <div className="flex flex-col sm:flex-row gap-3 justify-center">
            <Button onClick={() => navigate("/")} variant="default" className="flex items-center gap-2">
              <Home className="h-4 w-4" />
              Back to Home
            </Button>
            <Button onClick={() => navigate("/orders")} variant="outline" className="flex items-center gap-2">
              <Package className="h-4 w-4" />
              View My Orders
            </Button>
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
};

export default OrderConfirmation;