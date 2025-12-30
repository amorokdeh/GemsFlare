import { Item } from "@/types";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { ShoppingCart, Tag, Plus, Minus } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { useNavigate } from "react-router-dom";
import { useCart } from "@/context/CartContext";
import { formatCurrency } from "@/lib/utils";
import { useState } from "react";

interface ItemCardProps {
  item: Item;
}

const ItemCard = ({ item }: ItemCardProps) => {
  const [quantity, setQuantity] = useState(1);
  const { addToCart, isInCart } = useCart();

  const navigate = useNavigate();

  const handleAddToCart = (e: React.MouseEvent) => {
    e.stopPropagation();
    addToCart(item, quantity);
  };

  const navigateToItemDetail = () => {
    navigate(`/item/${item.number}`);
  };

  const incrementQuantity = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (quantity < item.amount) {
      setQuantity(quantity + 1);
    }
  };

  const decrementQuantity = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (quantity > 1) {
      setQuantity(quantity - 1);
    }
  };

  const inCart = isInCart(item.id);

  return (
    <Card 
      className="h-full flex flex-col transition-all hover:shadow-md cursor-pointer"
      onClick={navigateToItemDetail}
    >
      <CardContent className="p-4 flex-grow">
        <div className="aspect-square relative overflow-hidden rounded-md mb-4">
          <img
            src={item.img_src}
            alt={item.name}
            className="object-cover w-full h-full transform transition-transform hover:scale-105"
            onError={(e) => {
              // Fallback image if item image fails to load
              (e.target as HTMLImageElement).src = "/placeholder.svg";
            }}
          />
          {item.amount <= 5 && item.amount > 0 && (
            <Badge className="absolute top-2 right-2 bg-red-500" variant="destructive">
              Only {item.amount} left
              </Badge>
          )}
          {item.amount === 0 && (
            <Badge className="absolute top-2 right-2 bg-gray-500">
              Out of Stock
            </Badge>
          )}
        </div>
        <div className="space-y-2">
          <h3 className="font-medium text-lg mb-1">{item.name}</h3>
          <div className="flex items-center text-sm text-muted-foreground">
            <Tag className="h-3.5 w-3.5 mr-1" />
            <span>{item.category}</span>
          </div>
          <p className="font-bold text-xl text-blue-600">
            {formatCurrency(item.price)}
          </p>
        </div>
      </CardContent>
      <CardFooter className="px-4 pb-4 pt-0">
      {item.amount > 0 && (
          <div className="w-full space-y-2">
            <div className="flex items-center justify-between">
              <div className="flex items-center border border-green-700/30 rounded-md">
                <Button
                  type="button"
                  variant="ghost"
                  size="icon"
                  className="h-8 w-8 rounded-r-none"
                  onClick={decrementQuantity}
                  disabled={quantity <= 1}
                >
                  <Minus className="h-4 w-4" />
                </Button>
                <span className="w-8 text-center">{quantity}</span>
                <Button
                  type="button"
                  variant="ghost"
                  size="icon"
                  className="h-8 w-8 rounded-l-none"
                  onClick={incrementQuantity}
                  disabled={quantity >= item.amount}
                >
                  <Plus className="h-4 w-4" />
                </Button>
              </div>
            </div>
            <Button 
              onClick={handleAddToCart} 
              className="w-full"
              disabled={item.amount === 0}
              variant={inCart ? "secondary" : "default"}
            >
              <ShoppingCart className="mr-2 h-4 w-4" /> 
              {inCart ? "Update Cart" : "Add to Cart"}
            </Button>
          </div>
        )}
        {item.amount === 0 && (
          <Button 
            disabled
            className="w-full"
          >
            Out of Stock
          </Button>
        )}
      </CardFooter>
    </Card>
  );
};

export default ItemCard;