
import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import { SearchProvider } from "./context/SearchContext";
import PrivateRoute from "./components/PrivateRoute";
import Index from "./pages/Index";
import Login from "./pages/Login";
import Profile from "./pages/Profile";
import ItemDetail from "./pages/ItemDetail";
import EditItem from "./pages/EditItem";
import NotFound from "./pages/NotFound";
import MyItems from "./pages/MyItems";
import AddItem from "./pages/AddItem";
import AdminDashboard from "./pages/AdminDashboard";
import { CartProvider } from "./context/CartContext";
import Cart from "./pages/Cart";
import Checkout from "./pages/Checkout";
import OrderConfirmation from "./pages/OrderConfirmation";
import Orders from "./pages/Orders";

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <BrowserRouter>
        <AuthProvider>
          <SearchProvider>
            <CartProvider>
                <Toaster />
                <Sonner />
                <Routes>
                  <Route path="/" element={<Index />} />
                  <Route path="/login" element={<Login />} />
                  <Route path="/profile" element={
                    <PrivateRoute>
                      <Profile />
                    </PrivateRoute>
                  } />
                  <Route path="/item/:itemNumber" element={<ItemDetail />} />
                  <Route path="/item/:itemNumber/edit" element={
                    <PrivateRoute>
                      <EditItem />
                    </PrivateRoute>
                  } />
                  <Route path="/my-items" element={
                    <PrivateRoute>
                      <MyItems />
                    </PrivateRoute>
                  } />
                  <Route path="/add-item" element={
                    <PrivateRoute>
                      <AddItem />
                    </PrivateRoute>
                  } />
                  <Route path="/admin" element={
                    <PrivateRoute>
                      <AdminDashboard />
                    </PrivateRoute>
                  } />
                  <Route path="/cart" element={<Cart />} />
                  <Route path="/checkout" element={
                  <PrivateRoute>
                    <Checkout />
                  </PrivateRoute>
                } />
                  <Route path="/order-confirmation" element={<OrderConfirmation />} />
                  <Route path="/return" element={<OrderConfirmation />} />
                  <Route path="/cancel" element={<Checkout />} />
                  <Route path="/orders" element={
                    <PrivateRoute>
                      <Orders />
                    </PrivateRoute>
                  } />
                  {/* ADD ALL CUSTOM ROUTES ABOVE THE CATCH-ALL "*" ROUTE */}
                  <Route path="*" element={<NotFound />} />
                </Routes>
            </CartProvider>
          </SearchProvider>
        </AuthProvider>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
