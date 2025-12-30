import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useQuery, useMutation } from "@tanstack/react-query";
import { itemService } from "@/services/itemService";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { ChevronLeft, Upload, Save, X, Plus, Trash2, ImageOff } from "lucide-react";
import { useToast } from "@/components/ui/use-toast";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Badge } from "@/components/ui/badge";
import { Item } from "@/types";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";

// Schema for form validation
const formSchema = z.object({
  name: z.string().min(1, "Name is required"),
  category: z.string().min(1, "Category is required"),
  price: z.coerce.number().positive("Price must be positive"),
  amount: z.coerce.number().int().nonnegative("Amount cannot be negative"),
  colorGroups: z.array(z.string()).min(1, "At least one color group is required"),
});

type FormValues = z.infer<typeof formSchema>;

const EditItem = () => {
  const { itemNumber } = useParams<{ itemNumber: string }>();
  const navigate = useNavigate();
  const { toast } = useToast();

  const [colorInput, setColorInput] = useState("");
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [objectFile, setObjectFile] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [isRemoveImageDialogOpen, setIsRemoveImageDialogOpen] = useState(false);

  // Fetch item details
  const { data: item, isLoading, error, refetch } = useQuery({
    queryKey: ['item', itemNumber],
    queryFn: () => itemService.getItemByNumber(itemNumber as string),
    enabled: !!itemNumber,
  });

  // Form setup
  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: "",
      category: "",
      price: 0,
      amount: 0,
      colorGroups: [],
    },
  });

  // Populate form when item data is available
  useEffect(() => {
    if (item) {
      form.reset({
        name: item.name,
        category: item.category,
        price: item.price,
        amount: item.amount,
        colorGroups: item.color_groups,
      });
      
      // Set image preview if available
      if (item.img_src) {
        setImagePreview(item.img_src);
      }
    }
  }, [item, form]);

  // Setup mutation for saving item
  const mutation = useMutation({
    mutationFn: (formData: FormValues) => {
      if (!itemNumber) throw new Error("Item number is required");
      
      return itemService.editItem(itemNumber, {
        name: formData.name,
        category: formData.category,
        price: formData.price,
        amount: formData.amount,
        colorGroups: formData.colorGroups,
        image: imageFile,
        object: objectFile,
      });
    },
    onSuccess: (data: Item) => {
      toast({
        title: "Item updated",
        description: `${data.name} has been successfully updated.`,
      });
      navigate(`/item/${data.number}`);
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: `Failed to update item: ${error.message}`,
        variant: "destructive",
      });
    },
  });

  // Setup mutation for removing image
  const removeImageMutation = useMutation({
    mutationFn: () => {
      if (!itemNumber) throw new Error("Item number is required");
      return itemService.deleteImageFromItem(itemNumber);
    },
    onSuccess: () => {
      toast({
        title: "Image removed",
        description: "The image has been successfully removed from the item.",
      });
      setImagePreview(null);
      setImageFile(null);
      refetch(); // Refresh the item data
      setIsRemoveImageDialogOpen(false);
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: `Failed to remove image: ${error.message}`,
        variant: "destructive",
      });
      setIsRemoveImageDialogOpen(false);
    },
  });

  // Image file handlers
  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setImageFile(file);
      
      // Create preview
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };
  
  const handleObjectChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setObjectFile(e.target.files[0]);
      toast({
        title: "3D model selected",
        description: `${e.target.files[0].name} has been selected.`,
      });
    }
  };

  // Color group handlers
  const addColorGroup = () => {
    if (colorInput.trim() === "") return;
    
    const currentColors = form.getValues("colorGroups");
    if (!currentColors.includes(colorInput.trim())) {
      form.setValue("colorGroups", [...currentColors, colorInput.trim()]);
      setColorInput("");
    }
  };
  
  const removeColorGroup = (color: string) => {
    const currentColors = form.getValues("colorGroups");
    form.setValue(
      "colorGroups",
      currentColors.filter((c) => c !== color)
    );
  };

  // Image removal handler
  const handleRemoveImage = () => {
    setIsRemoveImageDialogOpen(true);
  };

  const confirmRemoveImage = () => {
    removeImageMutation.mutate();
  };

  // Form submission
  const onSubmit = (values: FormValues) => {
    mutation.mutate(values);
  };

  // Handle cancel
  const handleCancel = () => {
    navigate(-1);
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

        <h1 className="text-3xl font-bold mb-6">Edit Item</h1>

        {isLoading ? (
          <div className="text-center py-20">
            <p className="text-xl">Loading item details...</p>
          </div>
        ) : error ? (
          <div className="text-center py-20">
            <h2 className="text-2xl font-bold text-red-500">Error loading item details</h2>
            <p className="mt-2 text-gray-600">Please try again later or contact support.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {/* Form Section */}
            <Card className="md:col-span-2">
              <CardHeader>
                <CardTitle>Item Information</CardTitle>
              </CardHeader>
              <CardContent>
                <Form {...form}>
                  <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
                    <FormField
                      control={form.control}
                      name="name"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Name</FormLabel>
                          <FormControl>
                            <Input placeholder="Item name" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <FormField
                      control={form.control}
                      name="category"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Category</FormLabel>
                          <FormControl>
                            <Input placeholder="Category" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                      <FormField
                        control={form.control}
                        name="price"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Price (€)</FormLabel>
                            <FormControl>
                              <Input type="number" step="0.01" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="amount"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Stock Amount</FormLabel>
                            <FormControl>
                              <Input type="number" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    <FormField
                      control={form.control}
                      name="colorGroups"
                      render={() => (
                        <FormItem>
                          <FormLabel>Color Groups</FormLabel>
                          <div className="flex flex-wrap gap-2 mb-2">
                            {form.watch("colorGroups").map((color) => (
                              <Badge key={color} className="flex items-center gap-1 px-2 py-1">
                                {color}
                                <button
                                  type="button"
                                  onClick={() => removeColorGroup(color)}
                                  className="ml-1 rounded-full hover:bg-red-500 hover:text-white p-0.5"
                                >
                                  <X className="h-3 w-3" />
                                </button>
                              </Badge>
                            ))}
                          </div>
                          <div className="flex gap-2">
                            <Input
                              placeholder="Add color group"
                              value={colorInput}
                              onChange={(e) => setColorInput(e.target.value)}
                              onKeyPress={(e) => {
                                if (e.key === 'Enter') {
                                  e.preventDefault();
                                  addColorGroup();
                                }
                              }}
                            />
                            <Button
                              type="button"
                              variant="outline"
                              onClick={addColorGroup}
                              className="flex-shrink-0"
                            >
                              <Plus className="h-4 w-4" />
                            </Button>
                          </div>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                      <div>
                        <FormLabel>Upload Image</FormLabel>
                        <div className="mt-1">
                          <label className="block">
                            <span className="sr-only">Choose image</span>
                            <Input
                              type="file"
                              accept="image/*"
                              onChange={handleImageChange}
                              className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border file:border-gray-300 file:text-sm file:font-semibold hover:file:bg-gray-100"
                            />
                          </label>
                        </div>
                      </div>
                      
                      <div>
                        <FormLabel>Upload 3D Model</FormLabel>
                        <div className="mt-1">
                          <label className="block">
                            <span className="sr-only">Choose 3D model</span>
                            <Input
                              type="file"
                              accept=".obj,.glb,.gltf"
                              onChange={handleObjectChange}
                              className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border file:border-gray-300 file:text-sm file:font-semibold hover:file:bg-gray-100"
                            />
                          </label>
                        </div>
                      </div>
                    </div>

                    <div className="flex gap-2 pt-4">
                      <Button type="submit" disabled={mutation.isPending}>
                        {mutation.isPending ? "Saving..." : "Save Changes"}
                      </Button>
                      <Button type="button" variant="outline" onClick={handleCancel}>
                        Cancel
                      </Button>
                    </div>
                  </form>
                </Form>
              </CardContent>
            </Card>
            
            {/* Preview Section */}
            <Card>
              <CardHeader>
                <CardTitle>Preview</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="aspect-square bg-gray-100 rounded-lg overflow-hidden border flex items-center justify-center mb-4 relative">
                  {imagePreview ? (
                    <>
                    <img
                      src={imagePreview}
                      alt="Item preview"
                      className="object-contain w-full h-full"
                      onError={(e) => {
                        (e.target as HTMLImageElement).src = "/placeholder.svg";
                      }}
                    />
                    <Button
                      type="button"
                      size="sm"
                      variant="destructive"
                      className="absolute top-2 right-2"
                      onClick={handleRemoveImage}
                    >
                      <ImageOff className="h-4 w-4 mr-1" />
                      Remove Image
                    </Button>
                  </>
                  ) : (
                    <div className="text-gray-400 flex flex-col items-center">
                      <Upload className="h-12 w-12 mb-2" />
                      <p>No image available</p>
                    </div>
                  )}
                </div>
                <div className="space-y-2">
                  <div className="font-medium">{form.watch("name") || "Item Name"}</div>
                  <div>Category: {form.watch("category") || "N/A"}</div>
                  <div>Price: {form.watch("price") ? `€${form.watch("price").toFixed(2)}` : "€0.00"}</div>
                  <div>In stock: {form.watch("amount") || 0}</div>
                </div>
              </CardContent>
            </Card>
          </div>
        )}
      </main>
      <Footer />
      
      {/* Remove Image Confirmation Dialog */}
      <Dialog open={isRemoveImageDialogOpen} onOpenChange={setIsRemoveImageDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Remove Image</DialogTitle>
            <DialogDescription>
              Are you sure you want to remove the image from this item? This action cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button 
              variant="outline" 
              onClick={() => setIsRemoveImageDialogOpen(false)}
            >
              Cancel
            </Button>
            <Button 
              variant="destructive" 
              onClick={confirmRemoveImage}
              disabled={removeImageMutation.isPending}
            >
              {removeImageMutation.isPending ? "Removing..." : "Remove Image"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default EditItem;