import { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";
import { useCart } from "@/context/CartContext";
import { formatCurrency } from "@/lib/utils";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Separator } from "@/components/ui/separator";
import { Textarea } from "@/components/ui/textarea";
import { toast } from "@/hooks/use-toast";
import { Check, Truck, MapPin, PlusCircle, Loader2 } from "lucide-react";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";
import { userService } from "@/services/userService";
import { paypalService } from "@/services/paypalService";
import { checkoutService } from "@/services/checkoutService";
import { DeliveryAddress, BillAddress, Checkout as CheckoutType } from "@/types";
import { EditShippingAddressForm } from "@/components/EditShippingAddressForm";
import { EditBillingAddressForm } from "@/components/EditBillingAddressForm";
import { EditProfileForm } from "@/components/EditProfileForm";
import { AddShippingAddressForm } from "@/components/AddShippingAddressForm";

const checkoutFormSchema = z.object({
  additionalNotes: z.string().optional(),
});

type CheckoutFormValues = z.infer<typeof checkoutFormSchema>;

const Checkout = () => {
  const { user } = useAuth();
  const { cartItems, getTotalPrice, clearCart } = useCart();
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [userProfile, setUserProfile] = useState<any>(null);
  const [shippingAddress, setShippingAddress] = useState<DeliveryAddress | null>(null);
  const [billingAddress, setBillingAddress] = useState<BillAddress | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [addressesRefreshTrigger, setAddressesRefreshTrigger] = useState(0);
  const [paymentStep, setPaymentStep] = useState<'initial' | 'processing' | 'redirecting'>('initial');
  const [paypalOrderId, setPaypalOrderId] = useState<string | null>(null);
  const [backendCheckout, setBackendCheckout] = useState<CheckoutType | null>(null);

  const subtotal = getTotalPrice();
  const shipping = subtotal > 0 ? 5.99 : 0;
  const tax = subtotal * 0.19; // 19% VAT
  const total = subtotal + shipping + tax;

  useEffect(() => {
    const loadUserData = async () => {
      if (user) {
        try {
          setIsLoading(true);
          const profile = await userService.getUserProfile(user.id);
          setUserProfile(profile);

          try {
            const deliveryAddress = await userService.getShippingAddress();
            setShippingAddress(deliveryAddress);
          } catch (error) {
            console.log("No shipping address found");
          }

          try {
            const billAddress = await userService.getBillingAddress();
            setBillingAddress(billAddress);
          } catch (error) {
            console.log("No billing address found");
          }

          localStorage.removeItem('checkoutNumber');
          if (cartItems.length > 0) {
            try {
              const checkout = await handleBackendCheckout();
              console.log("New checkout created:", checkout);
            } catch (error) {
              console.error("Error creating new checkout:", error);
              toast({
                variant: "destructive",
                title: "Checkout Error",
                description: "Failed to initialize checkout. Please try again."
              });
            }
          }

        } catch (error) {
          console.error("Error loading user data:", error);
          toast({
            variant: "destructive",
            title: "Failed to load user data",
            description: "Please try again or proceed with checkout manually",
          });
        } finally {
          setIsLoading(false);
        }
      } else {
        setIsLoading(false);
      }
    };

    loadUserData();
  }, [user, cartItems, addressesRefreshTrigger]);

  useEffect(() => {
    const searchParams = new URLSearchParams(window.location.search);
    const token = searchParams.get('token');
    const cancelled = searchParams.get('cancelled');
    
    if (cancelled === 'true') {
      toast({
        variant: "destructive",
        title: "Payment cancelled",
        description: "Your payment was cancelled. You can try again when you're ready.",
      });
      window.history.replaceState({}, document.title, "/checkout");
    }
    
    if (token) {
      capturePaypalOrder(token);
    }
  }, []);

  useEffect(() => {
    const initializeBackendCheckout = async () => {
      if (user && cartItems.length > 0) {
        try {
          const checkout = await handleBackendCheckout();
          console.log("Backend checkout initialized:", checkout);
        } catch (error) {
          console.error("Error initializing backend checkout:", error);
        }
      }
    };
    
    initializeBackendCheckout();
  }, [user, cartItems.length]);

  useEffect(() => {
    const storedCheckoutNumber = localStorage.getItem('checkoutNumber');
    if (storedCheckoutNumber && user) {
      checkoutService.getCheckout(storedCheckoutNumber)
        .then(checkout => {
          setBackendCheckout(checkout);
          console.log("Loaded existing checkout from backend:", checkout);
        })
        .catch(error => {
          console.error("Error loading existing checkout:", error);
          localStorage.removeItem('checkoutNumber');
          setBackendCheckout(null);
        });
    }
  }, [user]);

  const capturePaypalOrder = async (orderId: string) => {
    try {
      setIsSubmitting(true);
      const storedCheckoutNumber = localStorage.getItem('checkoutNumber')
      const result = await paypalService.captureOrder(orderId, storedCheckoutNumber);
      
      if (result.status === "COMPLETED") {
        clearCart();
        toast({
          title: "Payment successful",
          description: "Thank you for your purchase!",
        });
        navigate("/order-confirmation");
      } else {
        toast({
          variant: "destructive",
          title: "Payment not completed",
          description: "Your payment was not completed. Please try again.",
        });
      }
    } catch (error) {
      console.error("Error capturing PayPal order:", error);
      toast({
        variant: "destructive",
        title: "Payment verification failed",
        description: "We couldn't verify your payment. Please contact customer support.",
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  const form = useForm<CheckoutFormValues>({
    resolver: zodResolver(checkoutFormSchema),
    defaultValues: {
      additionalNotes: "",
    },
  });

  const refreshAddresses = () => {
    setAddressesRefreshTrigger(prev => prev + 1);
  };

  async function handleBackendCheckout() {
    if (!user) return null;

    const localCheckoutNumber = localStorage.getItem('checkoutNumber');
    if (localCheckoutNumber) {
      try {
        const existingCheckout = await checkoutService.getCheckout(localCheckoutNumber);
        setBackendCheckout(existingCheckout);
        return existingCheckout;
      } catch {
        localStorage.removeItem('checkoutNumber');
      }
    }

    try {
      console.log("Creating new checkout with items:", cartItems);
      
      const itemPayload = cartItems.map(item => ({
        id: item.id,
        name: item.name,
        number: item.number || "",
        description: item.description || "",
        category: item.category || "",
        color_groups: item.color_groups || [],
        price: item.price,
        amount: item.quantity || 1,
        img_src: item.img_src || "",
        object_src: item.object_src || ""
      }));
      
      const payload = {
        userid: user.id,
        items: itemPayload,
        sum: total,
        paid: false,
        date: new Date().toISOString(),
      };
      
      console.log("Sending checkout payload:", payload);
      
      const newCheckout = await checkoutService.addCheckout(payload);
      console.log("Checkout created successfully:", newCheckout);
      
      if (newCheckout && newCheckout.number) {
        localStorage.setItem('checkoutNumber', newCheckout.number);
        setBackendCheckout(newCheckout);
      } else {
        console.error("Invalid checkout response:", newCheckout);
        toast({
          variant: "destructive", 
          title: "Checkout Error",
          description: "Could not create checkout. Please try again."
        });
      }
      
      return newCheckout;
    } catch (error) {
      console.error("Error creating checkout:", error);
      toast({
        variant: "destructive", 
        title: "Checkout Error",
        description: "There was a problem saving your checkout information."
      });
      return null;
    }
  }

  async function onSubmit(data: CheckoutFormValues) {
    if (cartItems.length === 0) {
      toast({
        variant: "destructive",
        title: "Cart is empty",
        description: "Please add items to your cart before checking out.",
      });
      navigate("/");
      return;
    }

    if (!shippingAddress) {
      toast({
        variant: "destructive",
        title: "Shipping address required",
        description: "Please add a shipping address before placing your order.",
      });
      return;
    }

    if (!user) {
      toast({
        variant: "destructive",
        title: "Authentication required",
        description: "Please log in to complete your purchase.",
      });
      navigate("/login");
      return;
    }

    setIsSubmitting(true);
    setPaymentStep('processing');

    try {
      const backendCheckout = await handleBackendCheckout();
      if (!backendCheckout) throw new Error("Failed to create checkout");
      
      const paypalOrder = await paypalService.createOrder(localStorage.getItem('checkoutNumber'));
      
      setPaypalOrderId(paypalOrder.orderID);
      localStorage.setItem('checkoutNumber', backendCheckout.number);
      
      setPaymentStep('redirecting');
      window.location.href = paypalOrder.approvalUrl;
      
    } catch (error) {
      console.error("Error processing order:", error);
      toast({
        variant: "destructive",
        title: "Checkout failed",
        description: "There was a problem processing your order. Please try again.",
      });
      setPaymentStep('initial');
      setIsSubmitting(false);
    }
  }

  if (cartItems.length === 0) {
    return (
      <>
        <Navbar />
        <div className="container mx-auto px-4 py-16 text-center">
          <h1 className="text-2xl font-bold mb-4">Your cart is empty</h1>
          <p className="mb-8">You need to add items to your cart before checking out.</p>
          <Button onClick={() => navigate("/")}>Back to Shopping</Button>
        </div>
        <Footer />
      </>
    );
  }

  if (isLoading && user) {
    return (
      <>
        <Navbar />
        <div className="container mx-auto px-4 py-16 text-center">
          <h1 className="text-2xl font-bold mb-4">Loading checkout information</h1>
          <p className="mb-8">Please wait while we prepare your checkout...</p>
        </div>
        <Footer />
      </>
    );
  }

  return (
    <>
      <Navbar />
      <div className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <h1 className="text-2xl font-bold">Checkout</h1>
          <p className="text-muted-foreground">Complete your order by providing your shipping details and payment.</p>
        </div>

        <div className="grid md:grid-cols-3 gap-8">
          <div className="md:col-span-2">
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                <div className="bg-background rounded-lg border shadow-sm p-6">
                  <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-medium">Contact Information</h2>
                    {userProfile ? (
                      <EditProfileForm 
                        profile={userProfile} 
                        onProfileUpdated={() => {
                          userService.getUserProfile(user?.id || "").then(setUserProfile);
                        }} 
                      />
                    ) : null}
                  </div>
                  
                  {userProfile ? (
                    <div className="bg-muted p-4 rounded-md mb-4">
                      <div className="flex gap-2 items-start">
                        <div>
                          <p className="font-medium">{userProfile.name} {userProfile.lastname}</p>
                          <p>{userProfile.email}</p>
                          <p>{userProfile.telephone || "No phone number provided"}</p>
                        </div>
                      </div>
                    </div>
                  ) : (
                    <div className="bg-muted p-4 rounded-md mb-4">
                      <p className="text-muted-foreground">
                        Please <Link to="/login" className="text-primary underline">log in</Link> to continue with checkout.
                      </p>
                    </div>
                  )}
                </div>

                <div className="bg-background rounded-lg border shadow-sm p-6">
                  <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-medium">Shipping Address</h2>
                    {shippingAddress ? (
                      <EditShippingAddressForm 
                        address={shippingAddress} 
                        onAddressUpdated={refreshAddresses} 
                      />
                    ) : (
                      <AddShippingAddressForm onAddressAdded={refreshAddresses} />
                    )}
                  </div>
                  
                  {shippingAddress ? (
                    <div className="bg-muted p-4 rounded-md mb-4">
                      <div className="flex gap-2 items-start">
                        <MapPin className="h-5 w-5 text-muted-foreground mt-0.5" />
                        <div>
                          <p className="font-medium">{shippingAddress.name} {shippingAddress.lastname}</p>
                          <p>{shippingAddress.street} {shippingAddress.housenumber}</p>
                          <p>{shippingAddress.zipcode} {shippingAddress.county}</p>
                          <p>{shippingAddress.country}</p>
                        </div>
                      </div>
                    </div>
                  ) : (
                    <div className="bg-muted p-4 rounded-md mb-4">
                      <p className="text-muted-foreground font-medium text-amber-600">
                        Please add a shipping address to continue with checkout.
                      </p>
                    </div>
                  )}
                </div>

                <div className="bg-background rounded-lg border shadow-sm p-6">
                  <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-medium">Billing Address</h2>
                    {billingAddress ? (
                      <EditBillingAddressForm 
                        address={billingAddress} 
                        onAddressUpdated={refreshAddresses} 
                      />
                    ) : (
                      <Button 
                        variant="outline" 
                        size="sm"
                        asChild
                      >
                        <Link to="/profile/add-billing-address">
                          <PlusCircle className="h-4 w-4 mr-2" /> Add Address
                        </Link>
                      </Button>
                    )}
                  </div>
                  
                  {billingAddress ? (
                    <div className="bg-muted p-4 rounded-md mb-4">
                      <div className="flex gap-2 items-start">
                        <MapPin className="h-5 w-5 text-muted-foreground mt-0.5" />
                        <div>
                          <p className="font-medium">{billingAddress.name} {billingAddress.lastname}</p>
                          <p>{billingAddress.street} {billingAddress.housenumber}</p>
                          <p>{billingAddress.zipcode} {billingAddress.county}</p>
                          <p>{billingAddress.country}</p>
                        </div>
                      </div>
                    </div>
                  ) : (
                    <div className="bg-muted p-4 rounded-md mb-4">
                      <p className="text-muted-foreground">Continue using shipping address for billing or add a billing address.</p>
                    </div>
                  )}
                </div>
                <div className="bg-background rounded-lg border shadow-sm p-6">
                  <h2 className="text-lg font-medium mb-4">Payment Method</h2>
                  <div className="flex items-center space-x-2 border rounded-md p-4 bg-blue-50 border-blue-200">
                    <div className="flex-1 cursor-pointer">
                      <div className="flex items-center gap-2">
                        <span className="font-bold text-blue-600">Pay</span>
                        <span className="font-bold text-blue-800">Pal</span>
                      </div>
                      <p className="text-sm text-gray-600 mt-2">
                        You'll be redirected to PayPal to complete your purchase securely.
                      </p>
                    </div>
                  </div>
                </div>

                <div className="bg-background rounded-lg border shadow-sm p-6">
                  <h2 className="text-lg font-medium mb-4">Additional Information</h2>
                  <FormField
                    control={form.control}
                    name="additionalNotes"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Order Notes (Optional)</FormLabel>
                        <FormControl>
                          <Textarea
                            placeholder="Special instructions for delivery or additional information"
                            className="min-h-[100px]"
                            {...field}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>

                <div className="flex justify-end">
                  <Button 
                    type="submit" 
                    size="lg"
                    disabled={isSubmitting || !shippingAddress || !user}
                    className="w-full sm:w-auto"
                  >
                    {paymentStep === 'initial' && "Pay with PayPal"}
                    {paymentStep === 'processing' && (
                      <>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        Processing...
                      </>
                    )}
                    {paymentStep === 'redirecting' && (
                      <>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        Redirecting to PayPal...
                      </>
                    )}
                  </Button>
                </div>
              </form>
            </Form>
          </div>

          <div className="space-y-4">
            <div className="bg-background rounded-lg border shadow-sm p-6 sticky top-24">
              <h2 className="text-lg font-medium mb-4">Order Summary</h2>
              
              <div className="max-h-80 overflow-y-auto mb-4 pr-2">
                {backendCheckout
                  ? backendCheckout.items.map(item => (
                    <div key={item.id} className="flex gap-3 py-3 border-b">
                      <div className="relative w-16 h-16 rounded-md overflow-hidden bg-muted flex-shrink-0">
                        <img
                          src={item.img_src || '/placeholder.svg'}
                          alt={item.name}
                          className="object-cover w-full h-full"
                          onError={(e) => {
                            (e.target as HTMLImageElement).src = "/placeholder.svg";
                          }}
                        />
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="font-medium truncate">{item.name}</p>
                        <p className="text-sm text-muted-foreground">{item.description}</p>
                      </div>
                      <div className="font-medium">{formatCurrency(item.price)}</div>
                    </div>
                  ))
                  : cartItems.map(item => (
                    <div key={item.id} className="flex gap-3 py-3 border-b">
                      <div className="relative w-16 h-16 rounded-md overflow-hidden bg-muted flex-shrink-0">
                        <img
                          src={item.img_src || '/placeholder.svg'}
                          alt={item.name}
                          className="object-cover w-full h-full"
                          onError={(e) => {
                            (e.target as HTMLImageElement).src = "/placeholder.svg";
                          }}
                        />
                        <div className="absolute -top-1 -right-1 bg-primary text-white rounded-full w-5 h-5 flex items-center justify-center text-xs">
                          {item.quantity}
                        </div>
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="font-medium truncate">{item.name}</p>
                        <p className="text-sm text-muted-foreground">{formatCurrency(item.price)} × {item.quantity}</p>
                      </div>
                      <div className="font-medium">{formatCurrency(item.price * item.quantity)}</div>
                    </div>
                  ))
                }
              </div>

              <Separator className="my-4" />
              
              <div className="space-y-2">
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Subtotal</span>
                  <span>{formatCurrency(subtotal)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Shipping</span>
                  <div className="flex items-center gap-1">
                    <Truck className="h-3.5 w-3.5 text-muted-foreground" />
                    <span>{formatCurrency(shipping)}</span>
                  </div>
                </div>
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Tax (19% VAT)</span>
                  <span>{formatCurrency(tax)}</span>
                </div>
                
                <Separator className="my-2" />
                
                <div className="flex justify-between font-bold text-lg">
                  <span>Total</span>
                  <span>{formatCurrency(total)}</span>
                </div>
              </div>

              <div className="mt-6 bg-muted/40 rounded-md p-3 text-sm">
                <p className="flex items-center text-muted-foreground">
                  <Check className="h-4 w-4 mr-2 text-green-500" />
                  Secure checkout powered by PayPal
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
};

export default Checkout;