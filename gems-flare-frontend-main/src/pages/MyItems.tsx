import { useEffect, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { itemService } from "@/services/itemService";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { useToast } from "@/components/ui/use-toast";
import { Plus, Package } from "lucide-react";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";
import ItemGrid from "@/components/ItemGrid";
import { useAuth } from "@/context/AuthContext";
import { useNavigate } from "react-router-dom";
import { useSearch } from "@/context/SearchContext";
import { Item } from "@/types";

const MyItems = () => {
  const { user } = useAuth();
  const { toast } = useToast();
  const navigate = useNavigate();
  const [currentPage, setCurrentPage] = useState(0);
  const PAGE_SIZE = 12;

  const { debouncedSearchTerm } = useSearch();
  const [filteredItems, setFilteredItems] = useState<Item[]>([]);

  // Check if user has permission to add items
  const { data: canAddItem } = useQuery({
    queryKey: ['addItemPermission'],
    queryFn: () => itemService.checkAddItemPermission(),
    enabled: !!user,
  });

  // Fetch user's items
  const {
    data: itemsData,
    isLoading,
    error,
    refetch
  } = useQuery({
    queryKey: ['userItems', currentPage],
    queryFn: () => itemService.getUserItems(currentPage, PAGE_SIZE),
    enabled: !!user,
  });

  // Filter items based on search term
  useEffect(() => {
    if (itemsData?.content && debouncedSearchTerm) {
      const filtered = itemsData.content.filter((item) =>
        item.name.toLowerCase().includes(debouncedSearchTerm.toLowerCase())
      );
      setFilteredItems(filtered);
    } else if (itemsData?.content) {
      setFilteredItems(itemsData.content);
    }
  }, [debouncedSearchTerm, itemsData]);

  const handleAddItem = () => {
    navigate('/add-item');
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  if (!user) {
    return (
      <div className="flex flex-col min-h-screen">
        <Navbar />
        <main className="flex-grow container mx-auto px-4 py-8">
          <div className="text-center py-20">
            <h2 className="text-2xl font-bold">Please login to view your items</h2>
            <Button onClick={() => navigate('/login')} className="mt-4">
              Login
            </Button>
          </div>
        </main>
        <Footer />
      </div>
    );
  }

  // Prepare data for ItemGrid component
  const displayItems = debouncedSearchTerm ? filteredItems : (itemsData?.content || []);
  const totalPages = itemsData?.totalPages || 1;

  return (
    <div className="flex flex-col min-h-screen">
      <Navbar />
      <main className="flex-grow container mx-auto px-4 py-8">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-3xl font-bold flex items-center gap-2">
              <Package className="h-8 w-8" />
              My Items
            </h1>
            <p className="text-gray-600 mt-2">
              Manage your products and inventory
              {debouncedSearchTerm && (
                <span className="ml-2 text-green-600">
                  Searching: "{debouncedSearchTerm}"
                </span>
              )}
            </p>
          </div>
          
          {canAddItem && (
            <Button onClick={handleAddItem} className="flex items-center gap-2">
              <Plus className="h-4 w-4" />
              Add New Item
            </Button>
          )}
        </div>

        {isLoading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {Array.from({ length: 8 }).map((_, index) => (
              <Card key={index} className="h-[340px] animate-pulse">
                <div className="h-[200px] bg-gray-200 rounded-t-lg" />
                <div className="p-4 space-y-3">
                  <div className="h-5 bg-gray-200 rounded w-3/4" />
                  <div className="h-4 bg-gray-200 rounded w-1/2" />
                  <div className="h-6 bg-gray-200 rounded w-1/3" />
                </div>
              </Card>
            ))}
          </div>
        ) : error ? (
          <Card className="p-8 text-center">
            <CardHeader>
              <CardTitle className="text-red-500">Error loading your items</CardTitle>
              <CardDescription>
                An error occurred while loading your items. Please try again.
              </CardDescription>
            </CardHeader>
            <Button onClick={() => refetch()} className="mt-4">
              Retry
            </Button>
          </Card>
        ) : displayItems && displayItems.length > 0 ? (
          <div>
            <ItemGrid 
              items={displayItems}
              currentPage={currentPage}
              totalPages={debouncedSearchTerm ? Math.ceil(displayItems.length / PAGE_SIZE) : totalPages}
              onPageChange={handlePageChange}
            />
          </div>
        ) : (
          <Card className="p-8 text-center">
            <CardHeader>
            <CardTitle>
                {debouncedSearchTerm 
                  ? `No items found matching "${debouncedSearchTerm}"` 
                  : "No items found"}
              </CardTitle>
              <CardDescription>
              {debouncedSearchTerm 
                  ? "Try a different search term" 
                  : "You haven't added any items yet. Start by adding your first item."}
              </CardDescription>
            </CardHeader>
            {canAddItem && !debouncedSearchTerm && (
              <Button onClick={handleAddItem} className="mt-4 flex items-center gap-2">
                <Plus className="h-4 w-4" />
                Add Your First Item
              </Button>
            )}
          </Card>
        )}
      </main>
      <Footer />
    </div>
  );
};

export default MyItems;