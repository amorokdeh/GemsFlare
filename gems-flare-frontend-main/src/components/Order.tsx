import { useState } from "react";
import { OrderItem, Order as order } from "@/types";
import { ChevronDown, X } from "lucide-react";
import { invoiceService } from "@/services/invoiceService";
import { orderService } from "@/services/orderService";
import { toast } from "@/hooks/use-toast";
import { CancelOrderConfirmation } from "./CancelOrderConfirmation";

const Order = ({
  number,
  date,
  sum,
  items,
  state,
  transaction,
  isOpen,
  onClick,
  onClose,
}: order & {
  isOpen: boolean;
  onClick: () => void;
  onClose: () => void;
}) => {
  const [isCancelling, setIsCancelling] = useState(false); // ✅ State to handle loading

  const handleArrowClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    onClick();
  };

  const handleCloseClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    onClose();
  };

  const handleDownloadInvoice = async (orderNumber: string) => {
    try {
      const invoiceBlob = await invoiceService.getInvoiceByOrderNumber(orderNumber);

      const url = URL.createObjectURL(invoiceBlob);

      const link = document.createElement("a");
      link.href = url;
      link.download = `Invoice_${orderNumber}.pdf`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      toast({
        title: "Invoice downloaded",
        description: `Invoice for order ${orderNumber} has been downloaded as PDF.`,
      });
    } catch (error) {
      console.error("Failed to download invoice:", error);
      toast({
        title: "Error",
        description: "Failed to download invoice. Please try again.",
        variant: "destructive",
      });
    }
  };

  return (
    <div
      onClick={onClick}
      className={`border border-green-700/30 p-6 rounded-2xl bg-gray-900 space-y-4 shadow-md transition-all duration-300 relative ${
        isOpen ? "" : "hover:bg-gray-800 cursor-pointer"
      } ${isOpen ? "ring-2 ring-green-400/50" : ""}`}
    >
      {/* Close or Toggle Button */}
      <div
        className="absolute top-4 right-4 text-green-400 hover:text-green-300 transition-colors cursor-pointer z-10"
        onClick={(e) => {
          e.stopPropagation();
          isOpen ? handleCloseClick(e) : handleArrowClick(e);
        }}
      >
        {isOpen ? <X size={24} /> : <ChevronDown size={24} />}
      </div>

      {/* Top Section */}
      <div className="flex justify-between items-start">
        <div>
          <h2 className="text-xl font-bold text-white mb-1">Order #{number}</h2>
          <p className="text-sm text-gray-400">{new Date(date).toLocaleString()}</p>
          <p className="text-sm text-green-500 mt-1">{state}</p>
        </div>

        <div className="text-right">
          <p className="text-lg font-semibold text-white">Total: ${sum.toFixed(2)}</p>
          <p className="text-sm text-gray-400">{items.length} item(s)</p>

          {/* Transaction ID */}
          <p className="text-xs text-gray-400 mt-2">Payment ID:</p>
          <p className="text-xs text-gray-300 break-all">{transaction}</p>
        </div>
      </div>

      {/* Items Section */}
      {isOpen && (
        <>
          {/* Invoice & Cancel Buttons */}
          <div className="flex justify-end gap-4 mb-4">
            <button
              onClick={(e) => {
                e.stopPropagation();
                handleDownloadInvoice(number);
              }}
              className="bg-green-600 hover:bg-green-500 text-white font-semibold py-2 px-4 rounded-lg transition"
            >
              Download Invoice
            </button>

            {state === "Waiting" && (
              <CancelOrderConfirmation 
                orderNumber={number} 
                onCancelled={onClose} // Optional: close the panel after cancelling
              />
            )}

          </div>

          {/* Items List */}
          <div
            className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-4"
            onClick={(e) => e.stopPropagation()}
          >
            {items.map((item) => (
              <div
                key={item.id}
                className="flex bg-gray-800 rounded-lg overflow-hidden shadow hover:bg-gray-700 transition-colors"
              >
                <div className="w-24 h-24 flex-shrink-0">
                  <img
                    src={item.img_src}
                    alt={item.name}
                    className="w-full h-full object-cover"
                  />
                </div>
                <div className="p-3 flex-1">
                  <h3 className="text-md font-semibold text-white">{item.name}</h3>
                  <p className="text-sm text-gray-400">#{item.number}</p>
                  <p className="text-sm text-gray-400">{item.category}</p>
                  <div className="text-sm text-gray-300 mt-1">
                    {item.amount} × ${item.price.toFixed(2)}
                  </div>
                  <div className="flex flex-wrap gap-1 mt-1">
                    {item.color_groups.map((color, idx) => (
                      <span
                        key={idx}
                        className="text-xs bg-green-700/30 px-2 py-1 rounded-full text-green-300"
                      >
                        {color}
                      </span>
                    ))}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
};

export default Order;