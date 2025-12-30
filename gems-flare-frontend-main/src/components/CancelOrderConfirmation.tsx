import { useState } from "react";
import { toast } from "@/hooks/use-toast";
import { 
  AlertDialog, 
  AlertDialogTrigger, 
  AlertDialogContent, 
  AlertDialogHeader, 
  AlertDialogTitle, 
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogCancel,
  AlertDialogAction
} from "@/components/ui/alert-dialog";
import { Button } from "@/components/ui/button";
import { orderService } from "@/services/orderService";

interface CancelOrderConfirmationProps {
  orderNumber: string;
  onCancelled?: () => void;
}

export function CancelOrderConfirmation({ orderNumber, onCancelled }: CancelOrderConfirmationProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [isCancelling, setIsCancelling] = useState(false);

  const handleCancelOrder = async () => {
    setIsCancelling(true);
    try {
      const message = await orderService.cancelOrder(orderNumber);
  
      toast({
        title: "Order Cancelled",
        description: message,
      });
  
      onCancelled?.();
      setIsOpen(false);
    } catch (error) {
      console.error("Error cancelling order:", error);
      toast({
        title: "Cancellation Failed",
        description:
          error instanceof Error
            ? error.message
            : "An unexpected error occurred. Please try again.",
        variant: "destructive",
      });
    } finally {
      setIsCancelling(false);
    }
  };  

  return (
    <AlertDialog open={isOpen} onOpenChange={setIsOpen}>
      <AlertDialogTrigger asChild>
        <Button
          variant="destructive"
          className="ml-4"
          onClick={(e) => e.stopPropagation()}
        >
          Cancel Order
        </Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Are you sure you want to cancel this order?</AlertDialogTitle>
          <AlertDialogDescription>
            This action cannot be undone. The order will be permanently cancelled.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Never mind</AlertDialogCancel>
          <AlertDialogAction
            onClick={handleCancelOrder}
            disabled={isCancelling}
            className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
          >
            {isCancelling ? "Cancelling..." : "Yes, cancel order"}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
