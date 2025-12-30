import { useState } from "react";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";
import { useQuery } from "@tanstack/react-query";
import { itemService } from "@/services/itemService";
import ItemGrid from "@/components/ItemGrid";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Skeleton } from "@/components/ui/skeleton";
import { useSearch } from "@/context/SearchContext";

const Index = () => {
  const [currentPage, setCurrentPage] = useState(0);
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);
  const { debouncedSearchTerm } = useSearch();

  // Fetch categories
  const { 
    data: categories,
    isLoading: categoriesLoading 
  } = useQuery({
    queryKey: ['categories'],
    queryFn: () => itemService.getAllCategories(),
  });

  // Fetch items based on selected category or search term
  const { 
    data: itemsResponse, 
    isLoading: itemsLoading, 
    error: itemsError 
  } = useQuery({
    queryKey: ['items', currentPage, selectedCategory, debouncedSearchTerm],
    queryFn: () => {
      if (debouncedSearchTerm && debouncedSearchTerm.length >= 2) {
        return itemService.searchItemsByName(debouncedSearchTerm, currentPage);
      }
      if (selectedCategory) {
        return itemService.getItemsByCategory(selectedCategory, currentPage);
      }
      return itemService.getItems(currentPage);
    },
    retry: 1,
    refetchOnWindowFocus: false,
  });

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  return (
    <div className="flex flex-col min-h-screen">
      <Navbar />
      <main className="flex-grow container mx-auto px-4 py-8">
        <Tabs 
          defaultValue="all" 
          className="mb-8"
          onValueChange={(value) => setSelectedCategory(value === 'all' ? null : value)}
        >
          <TabsList>
            <TabsTrigger value="all">All Items</TabsTrigger>
            {!categoriesLoading && categories?.map((category) => (
              <TabsTrigger key={category.id} value={category.name}>
                {category.name}
              </TabsTrigger>
            ))}
          </TabsList>
          
          <TabsContent value="all" className="mt-6">
            <h1 className="text-3xl font-bold mb-8">
              {debouncedSearchTerm && debouncedSearchTerm.length >= 2 
                ? `Search Results for "${debouncedSearchTerm}"` 
                : "All Items"}
            </h1>
            
            {itemsLoading ? (
              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                {Array.from({ length: 8 }).map((_, i) => (
                  <div key={i} className="space-y-4">
                    <Skeleton className="h-[200px] w-full rounded-md" />
                    <Skeleton className="h-4 w-[250px]" />
                    <Skeleton className="h-4 w-[200px]" />
                    <Skeleton className="h-10 w-full" />
                  </div>
                ))}
              </div>
            ) : itemsError ? (
              <div className="p-8 text-center">
                <p className="text-gray-500">Error loading items</p>
              </div>
            ) : itemsResponse && itemsResponse.content.length > 0 ? (
              <ItemGrid 
                items={itemsResponse.content} 
                currentPage={itemsResponse.pageable.pageNumber}
                totalPages={itemsResponse.totalPages}
                onPageChange={handlePageChange}
              />
            ) : (
              <div className="p-8 text-center bg-gray-50 rounded-lg">
                <p className="text-gray-600 text-lg">
                  {debouncedSearchTerm 
                    ? `No items found for "${debouncedSearchTerm}". Try a different search term.` 
                    : "No items available at the moment."}
                </p>
              </div>
            )}
          </TabsContent>

          {!categoriesLoading && categories?.map((category) => (
            <TabsContent key={category.id} value={category.name} className="mt-6">
              <h1 className="text-3xl font-bold mb-8">{category.name}</h1>
              {itemsLoading ? (
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                  {Array.from({ length: 8 }).map((_, i) => (
                    <div key={i} className="space-y-4">
                      <Skeleton className="h-[200px] w-full rounded-md" />
                      <Skeleton className="h-4 w-[250px]" />
                      <Skeleton className="h-4 w-[200px]" />
                      <Skeleton className="h-10 w-full" />
                    </div>
                  ))}
                </div>
              ) : itemsError ? (
                <div className="p-8 text-center">
                  <p className="text-gray-500">Error loading items</p>
                </div>
              ) : itemsResponse && itemsResponse.content.length > 0 ? (
                <ItemGrid 
                  items={itemsResponse.content} 
                  currentPage={itemsResponse.pageable.pageNumber}
                  totalPages={itemsResponse.totalPages}
                  onPageChange={handlePageChange}
                />
              ) : (
                <div className="p-8 text-center bg-gray-50 rounded-lg">
                  <p className="text-gray-600 text-lg">No items available in this category.</p>
                </div>
              )}
            </TabsContent>
          ))}
        </Tabs>
      </main>
      <Footer />
    </div>
  );
};

export default Index;