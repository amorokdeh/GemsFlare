import { Item } from "@/types";
import ItemCard from "@/components/ItemCard";
import { Pagination, PaginationContent, PaginationEllipsis, PaginationItem, PaginationLink, PaginationNext, PaginationPrevious } from "@/components/ui/pagination";

interface ItemGridProps {
  items: Item[];
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

const ItemGrid = ({ items, currentPage, totalPages, onPageChange }: ItemGridProps) => {
  return (
    <div className="space-y-8">
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 md:gap-6">
        {items.map((item) => (
          <ItemCard key={item.id} item={item} />
        ))}
      </div>
      
      {totalPages > 1 && (
        <Pagination className="mt-8">
          <PaginationContent>
            {currentPage > 0 && (
              <PaginationItem>
                <PaginationPrevious onClick={() => onPageChange(currentPage - 1)} />
              </PaginationItem>
            )}
            
            {Array.from({ length: Math.min(5, totalPages) }).map((_, i) => {
              const pageNumber = i;
              const isVisible = 
                pageNumber === 0 || 
                pageNumber === totalPages - 1 || 
                (pageNumber >= currentPage - 1 && pageNumber <= currentPage + 1);
              
              if (!isVisible) {
                return pageNumber === 1 || pageNumber === totalPages - 2 ? (
                  <PaginationItem key={`ellipsis-${pageNumber}`}>
                    <PaginationEllipsis />
                  </PaginationItem>
                ) : null;
              }
              
              return (
                <PaginationItem key={pageNumber}>
                  <PaginationLink 
                    isActive={pageNumber === currentPage}
                    onClick={() => onPageChange(pageNumber)}
                  >
                    {pageNumber + 1}
                  </PaginationLink>
                </PaginationItem>
              );
            })}
            
            {currentPage < totalPages - 1 && (
              <PaginationItem>
                <PaginationNext onClick={() => onPageChange(currentPage + 1)} />
              </PaginationItem>
            )}
          </PaginationContent>
        </Pagination>
      )}
    </div>
  );
};

export default ItemGrid;
