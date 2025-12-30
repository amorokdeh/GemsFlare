import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { orderService } from "@/services/orderService";
import { useAuth } from "@/context/AuthContext";
import { Skeleton } from "@/components/ui/skeleton";
import Order from "@/components/Order";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";

const Orders = () => {
  const { user } = useAuth();
  const [page, setPage] = useState(0);
  const [openOrderId, setOpenOrderId] = useState<string | null>(null); // Track open order by ID

  const { data, isLoading, error } = useQuery({
    queryKey: ['orders', user?.id, page],
    queryFn: () => orderService.getOrdersByUserId(user.id, page),
    enabled: !!user?.id,
  });

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  const handleOrderClick = (id: string) => {
    if (openOrderId === id) {
      return; // Don't toggle when the same order is clicked
    }
    setOpenOrderId(id); // Open the new order, close the previous one
  };

  const handleCloseOrder = () => {
    setOpenOrderId(null); // Close the currently open order
  };

  return (
    <div className="flex flex-col min-h-screen">
      <Navbar />
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-6">My Orders</h1>

        {isLoading ? (
          <div className="space-y-4">
            {[...Array(3)].map((_, i) => (
              <Skeleton key={i} className="h-24 w-full rounded-md" />
            ))}
          </div>
        ) : error ? (
          <div className="text-red-500">Failed to load orders.</div>
        ) : data?.content.length === 0 ? (
          <div className="text-gray-500">No orders found.</div>
        ) : (
          <div className="space-y-6">
            {data.content.map((order: any) => (
              <Order
                key={order.id}
                {...order}
                isOpen={openOrderId === order.id} // Check if this order is open
                onClick={() => handleOrderClick(order.id)} // Handle opening/closing order
                onClose={handleCloseOrder} // Handle closing order with the X symbol
              />
            ))}

            {data.totalPages > 1 && (
              <div className="mt-4 flex justify-center space-x-4">
                <button
                  onClick={() => handlePageChange(page - 1)}
                  disabled={page === 0}
                  className="px-4 py-2 bg-gray-700 text-white rounded-lg"
                >
                  Previous
                </button>
                <button
                  onClick={() => handlePageChange(page + 1)}
                  disabled={page === data.totalPages - 1}
                  className="px-4 py-2 bg-gray-700 text-white rounded-lg"
                >
                  Next
                </button>
              </div>
            )}
          </div>
        )}
      </div>
      <Footer />
    </div>
  );
};

export default Orders;