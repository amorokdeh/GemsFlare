import { useParams, Link, useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { itemService } from "@/services/itemService";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { toast } from "@/hooks/use-toast";
import { ShoppingCart, Pencil, ChevronLeft, Tag, Plus, Minus } from "lucide-react";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";
import ModelViewer from "@/components/ModelViewer";
import { useState } from "react";
import { useCart } from "@/context/CartContext";
import { ColorSelector } from "@/components/ColorSelector";
import { useAuth } from "@/context/AuthContext";
import DeleteItemConfirmation from "@/components/DeleteItemConfirmation";

const ItemDetail = () => {
  const { itemNumber } = useParams<{ itemNumber: string }>();
  const navigate = useNavigate();
  const [showModel, setShowModel] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const [selectedColors, setSelectedColors] = useState<{ [key: string]: string }>({});
  const { addToCart } = useCart();
  const { user } = useAuth();

  // Fetch item details
  const { data: item, isLoading, error } = useQuery({
    queryKey: ['item', itemNumber],
    queryFn: () => itemService.getItemByNumber(itemNumber as string),
    enabled: !!itemNumber,
  });

  // Fetch edit permission
  const { data: hasEditPermission = false } = useQuery({
    queryKey: ['itemPermission', itemNumber],
    queryFn: () => itemService.checkItemEditPermission(itemNumber as string),
    enabled: !!itemNumber && !!user,
  });

  const handleAddToCart = () => {
    if (item) {
      addToCart({ ...item, selected_colors: selectedColors }, quantity);
      toast({
        title: "Added to cart",
        description: `${quantity} ${item.name} ${quantity === 1 ? 'has' : 'have'} been added to your cart.`,
      });
    }
  };

  const incrementQuantity = () => {
    if (item && quantity < item.amount) {
      setQuantity(quantity + 1);
    }
  };

  const decrementQuantity = () => {
    if (quantity > 1) {
      setQuantity(quantity - 1);
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('de-DE', {
      style: 'currency',
      currency: 'EUR',
    }).format(amount);
  };

  const toggleView = () => {
    setShowModel(!showModel);
  };

  return (
    <div className="flex flex-col min-h-screen">
      <Navbar />
      <main className="flex-grow container mx-auto px-4 py-8">
        <Button 
          variant="outline" 
          className="mb-6"
          onClick={() => navigate(-1)}
        >
          <ChevronLeft className="h-4 w-4 mr-2" /> Back
        </Button>

        {isLoading ? (
          <div className="space-y-6">
            <Skeleton className="h-12 w-3/4 max-w-md" />
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
              <Skeleton className="h-[400px] w-full rounded-lg" />
              <div className="space-y-4">
                <Skeleton className="h-8 w-1/2" />
                <Skeleton className="h-6 w-1/3" />
                <Skeleton className="h-10 w-1/4" />
                <Skeleton className="h-24 w-full" />
                <Skeleton className="h-12 w-48" />
              </div>
            </div>
          </div>
        ) : error ? (
          <div className="text-center py-20">
            <h2 className="text-2xl font-bold text-red-500">Error loading item details</h2>
            <p className="mt-2 text-gray-600">Please try again later or contact support.</p>
          </div>
        ) : item ? (
          <div>
            <div className="flex justify-between items-start mb-6">
              <h1 className="text-3xl font-bold">{item.name}</h1>
              {hasEditPermission && (
                  <div className="flex gap-2">
                  <Button 
                    variant="outline" 
                    size="sm" 
                    className="flex gap-1"
                    asChild
                  >
                    <Link to={`/item/${itemNumber}/edit`}>
                      <Pencil className="h-4 w-4" /> Edit
                    </Link>
                  </Button>
                  
                  <DeleteItemConfirmation itemNumber={itemNumber || ""} />
                </div>
              )}
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
              <div className="aspect-square rounded-lg overflow-hidden border relative">
                {showModel ? (
                  <ModelViewer modelUrl={item.object_src} fallbackImgUrl={item.img_src} />
                ) : (
                  <img 
                    src={item.img_src || "/placeholder.svg"}
                    alt={item.name}
                    className="object-contain w-full h-full"
                    onError={(e) => {
                      (e.target as HTMLImageElement).src = "/placeholder.svg";
                    }}
                  />
                )}
                <Button 
                  variant="secondary" 
                  size="sm" 
                  onClick={toggleView}
                  className="absolute bottom-3 right-3 bg-white/50 backdrop-blur-sm hover:bg-white/80"
                >
                  {showModel ? "Show Image" : "Show 3D Model"}
                </Button>
              </div>
              <Card>
                <CardContent className="p-6">
                  <div className="space-y-6">
                    <div className="flex items-center">
                      <Tag className="h-4 w-4 mr-2 text-blue-500" />
                      <span className="text-gray-600">{item.category}</span>
                    </div>
                    
                    {item.color_groups.length > 0 && (
                      <div className="space-y-4 mt-4">
                        <h3 className="font-medium">Color Options</h3>
                        <div className="flex flex-wrap gap-3">
                          {item.color_groups.map((group) => (
                            <ColorSelector
                              key={group}
                              colorGroup={group}
                              selectedColor={selectedColors[group]}
                              onColorSelect={(color) => {
                                setSelectedColors(prev => {
                                  const newColors = { ...prev };
                                  if (color) {
                                    newColors[group] = color;
                                  } else {
                                    delete newColors[group];
                                  }
                                  return newColors;
                                });
                              }}
                            />
                          ))}
                        </div>
                      </div>
                    )}
                    
                    <p className="text-3xl font-bold text-blue-600">
                      {formatCurrency(item.price)}
                    </p>
                    
                    <div className="py-2">
                      <p className="text-sm mb-1">Item Number: {item.number}</p>
                      <p className={`font-medium ${item.amount <= 5 ? 'text-red-500' : 'text-green-600'}`}>
                        {item.amount > 0 
                          ? `${item.amount} in stock` 
                          : 'Out of stock'}
                      </p>
                    </div>

                    {item.amount > 0 && (
                      <div className="space-y-4">
                        <div className="flex items-center justify-between">
                          <span className="text-sm font-medium">Quantity:</span>
                          <div className="flex items-center border rounded-md">
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
                            <span className="w-12 text-center">{quantity}</span>
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
                          size="lg"
                          disabled={item.amount === 0}
                        >
                          <ShoppingCart className="mr-2 h-5 w-5" /> 
                          Add to Cart
                        </Button>
                      </div>
                    )}

                    {item.amount === 0 && (
                      <Button 
                        className="w-full"
                        size="lg"
                        disabled
                      >
                        <ShoppingCart className="mr-2 h-5 w-5" /> 
                        Out of Stock
                      </Button>
                    )}

                    {item.description && (
                      <div className="prose max-w-none">
                        <h3 className="text-lg font-medium mb-2">Description</h3>
                        <p className="text-gray-600 whitespace-pre-line">{item.description}</p>
                      </div>
                    )}
                  </div>
                  
                </CardContent>
              </Card>
            </div>
          </div>
        ) : (
          <div className="text-center py-20">
            <h2 className="text-2xl font-bold">Item not found</h2>
          </div>
        )}
      </main>
      <Footer />
    </div>
  );
};

export default ItemDetail;