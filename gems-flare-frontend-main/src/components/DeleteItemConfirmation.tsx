import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { itemService } from "@/services/itemService";
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
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Trash2 } from "lucide-react";

interface DeleteItemConfirmationProps {
  itemNumber: string;
}

export function DeleteItemConfirmation({ itemNumber }: DeleteItemConfirmationProps) {
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);
  const [password, setPassword] = useState("");
  const [isDeleting, setIsDeleting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleDeleteItem() {
    if (!password) {
      setError("Password is required");
      return;
    }

    setIsDeleting(true);
    setError(null);
    
    try {
      const result = await itemService.deleteItem(itemNumber, password);
      
      toast({
        title: "Item deleted",
        description: "The item has been deleted successfully",
      });
      
      // Navigate back to items list
      navigate("/");
    } catch (err) {
      console.error("Error deleting item:", err);
      setError(err instanceof Error ? err.message : "Failed to delete item. Please try again.");
      
      toast({
        variant: "destructive",
        title: "Item deletion failed",
        description: err instanceof Error ? err.message : "Failed to delete item. Please try again.",
      });
    } finally {
      setIsDeleting(false);
    }
  }

  return (
    <AlertDialog open={isOpen} onOpenChange={setIsOpen}>
      <AlertDialogTrigger asChild>
        <Button variant="destructive" size="sm" className="flex gap-1">
          <Trash2 className="h-4 w-4" />
          Delete
        </Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Are you sure you want to delete this item?</AlertDialogTitle>
          <AlertDialogDescription>
            This action cannot be undone. This will permanently delete the item and all data associated with it.
          </AlertDialogDescription>
        </AlertDialogHeader>
        
        <div className="py-4">
          <label htmlFor="password" className="block text-sm font-medium mb-1">
            Confirm your password
          </label>
          <Input 
            id="password"
            type="password"
            placeholder="Enter your password" 
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full"
          />
          {error && <p className="text-sm text-destructive mt-1">{error}</p>}
        </div>
        
        <AlertDialogFooter>
          <AlertDialogCancel>Cancel</AlertDialogCancel>
          <AlertDialogAction 
            onClick={handleDeleteItem} 
            disabled={isDeleting || !password}
            className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
          >
            {isDeleting ? "Deleting..." : "Delete Item"}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}

export default DeleteItemConfirmation;