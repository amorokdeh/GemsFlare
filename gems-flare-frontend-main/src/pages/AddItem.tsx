import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useMutation } from "@tanstack/react-query";
import { itemService } from "@/services/itemService";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { toast } from "@/components/ui/use-toast";
import { ChevronLeft, Upload, Plus, X } from "lucide-react";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";
import { Badge } from "@/components/ui/badge";
import { useAuth } from "@/context/AuthContext";

const AddItem = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [name, setName] = useState("");
  const [category, setCategory] = useState("");
  const [price, setPrice] = useState("");
  const [amount, setAmount] = useState("");
  const [colorGroups, setColorGroups] = useState<string[]>([]);
  const [currentColor, setCurrentColor] = useState("");
  const [image, setImage] = useState<File | null>(null);
  const [object, setObject] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string | null>(null);

  // Add item mutation
  const addItemMutation = useMutation({
    mutationFn: itemService.addItem,
    onSuccess: (data) => {
      toast({
        title: "Item added successfully",
        description: `${name} has been added to your inventory.`,
      });
      navigate(`/item/${data.number}`);
    },
    onError: (error) => {
      toast({
        title: "Failed to add item",
        description: error instanceof Error ? error.message : "An unknown error occurred.",
        variant: "destructive",
      });
    },
  });

  // Redirect if not logged in
  if (!user) {
    navigate("/login");
    return null;
  }

  const handleAddColor = () => {
    if (currentColor.trim() && !colorGroups.includes(currentColor.trim())) {
      setColorGroups([...colorGroups, currentColor.trim()]);
      setCurrentColor("");
    }
  };

  const handleRemoveColor = (color: string) => {
    setColorGroups(colorGroups.filter(c => c !== color));
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setImage(file);
      
      // Create preview
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleObjectChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setObject(file);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validate form
    if (!name || !category || !price || !amount || colorGroups.length === 0) {
      toast({
        title: "Missing required fields",
        description: "Please fill out all required fields.",
        variant: "destructive",
      });
      return;
    }

    // Convert price and amount to numbers
    const numPrice = parseFloat(price);
    const numAmount = parseInt(amount, 10);

    if (isNaN(numPrice) || isNaN(numAmount)) {
      toast({
        title: "Invalid values",
        description: "Price and amount must be valid numbers.",
        variant: "destructive",
      });
      return;
    }

    // Submit the form
    addItemMutation.mutate({
      name,
      category,
      colorGroups,
      price: numPrice,
      amount: numAmount,
      image,
      object,
    });
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

        <h1 className="text-3xl font-bold mb-6">Add New Item</h1>

        <Card className="p-6">
          <form onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              {/* Left Column - Item Details */}
              <div className="space-y-6">
                <div className="space-y-4">
                  <h2 className="text-xl font-semibold">Item Information</h2>
                  
                  <div className="space-y-2">
                    <Label htmlFor="name">Item Name *</Label>
                    <Input
                      id="name"
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      required
                      placeholder="Enter item name"
                    />
                  </div>
                  
                  <div className="space-y-2">
                    <Label htmlFor="category">Category *</Label>
                    <Input
                      id="category"
                      value={category}
                      onChange={(e) => setCategory(e.target.value)}
                      required
                      placeholder="Enter category"
                    />
                  </div>
                  
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="price">Price (€) *</Label>
                      <Input
                        id="price"
                        type="number"
                        step="0.01"
                        min="0"
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                        required
                        placeholder="0.00"
                      />
                    </div>
                    
                    <div className="space-y-2">
                      <Label htmlFor="amount">Quantity *</Label>
                      <Input
                        id="amount"
                        type="number"
                        min="0"
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)}
                        required
                        placeholder="0"
                      />
                    </div>
                  </div>
                </div>

                <Separator />

                <div className="space-y-4">
                  <h2 className="text-xl font-semibold">Color Groups *</h2>
                  <p className="text-sm text-gray-500">
                    Add color groups that best describe this item
                  </p>
                  
                  <div className="flex items-center gap-2">
                    <Input
                      value={currentColor}
                      onChange={(e) => setCurrentColor(e.target.value)}
                      placeholder="Add a color group"
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          e.preventDefault();
                          handleAddColor();
                        }
                      }}
                    />
                    <Button 
                      type="button" 
                      onClick={handleAddColor}
                      disabled={!currentColor.trim()}
                    >
                      <Plus className="h-4 w-4" />
                    </Button>
                  </div>
                  
                  <div className="flex flex-wrap gap-2 mt-2">
                    {colorGroups.length === 0 && (
                      <p className="text-sm text-gray-500 italic">No color groups added yet</p>
                    )}
                    
                    {colorGroups.map((color, index) => (
                      <Badge key={index} className="px-3 py-1 flex items-center gap-1">
                        {color}
                        <button
                          type="button"
                          onClick={() => handleRemoveColor(color)}
                          className="ml-1 hover:text-red-500"
                        >
                          <X className="h-3 w-3" />
                        </button>
                      </Badge>
                    ))}
                  </div>
                </div>
              </div>

              {/* Right Column - Images */}
              <div className="space-y-6">
                <h2 className="text-xl font-semibold">Item Images</h2>
                
                <div className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="image">Product Image</Label>
                    <div className="border-2 border-dashed border-gray-300 rounded-lg p-4 text-center">
                      {imagePreview ? (
                        <div className="relative">
                          <img 
                            src={imagePreview} 
                            alt="Preview" 
                            className="max-h-[200px] mx-auto object-contain"
                          />
                          <Button
                            type="button"
                            variant="outline"
                            size="sm"
                            className="absolute top-0 right-0 mt-2 mr-2"
                            onClick={() => {
                              setImage(null);
                              setImagePreview(null);
                            }}
                          >
                            <X className="h-4 w-4" />
                          </Button>
                        </div>
                      ) : (
                        <div className="py-8">
                          <Upload className="mx-auto h-12 w-12 text-gray-400" />
                          <p className="mt-2 text-sm text-gray-500">
                            Click to upload or drag and drop
                          </p>
                          <p className="text-xs text-gray-400">
                            PNG, JPG up to 10MB
                          </p>
                        </div>
                      )}
                      <Input
                        id="image"
                        type="file"
                        accept="image/*"
                        className={imagePreview ? "hidden" : "absolute inset-0 opacity-0 cursor-pointer"}
                        onChange={handleImageChange}
                      />
                    </div>
                  </div>
                  
                  <div className="space-y-2">
                    <Label htmlFor="object">3D Model</Label>
                    <div className="border-2 border-dashed border-gray-300 rounded-lg p-4 text-center">
                      {object ? (
                        <div className="flex items-center justify-between">
                          <span>{object.name}</span>
                          <Button
                            type="button"
                            variant="ghost"
                            size="sm"
                            onClick={() => setObject(null)}
                          >
                            <X className="h-4 w-4" />
                          </Button>
                        </div>
                      ) : (
                        <label htmlFor="object" className="block cursor-pointer py-8">
                          <Upload className="mx-auto h-12 w-12 text-gray-400" />
                          <p className="mt-2 text-sm text-gray-500">
                            Click to upload a 3D model
                          </p>
                          <p className="text-xs text-gray-400">
                            OBJ, glTF, or other 3D formats
                          </p>
                          <Input
                            id="object"
                            type="file"
                            accept=".obj,.gltf,.glb,.stl"
                            className="hidden"
                            onChange={handleObjectChange}
                          />
                        </label>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div className="mt-8 flex justify-end gap-4">
              <Button
                type="button"
                variant="outline"
                onClick={() => navigate('/my-items')}
              >
                Cancel
              </Button>
              <Button 
                type="submit" 
                disabled={addItemMutation.isPending}
              >
                {addItemMutation.isPending ? "Adding..." : "Add Item"}
              </Button>
            </div>
          </form>
        </Card>
      </main>
      <Footer />
    </div>
  );
};

export default AddItem;