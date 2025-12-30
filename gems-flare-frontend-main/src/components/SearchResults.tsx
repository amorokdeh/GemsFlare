import { Item } from "@/types";
import { useNavigate } from "react-router-dom";
import { Command, CommandEmpty, CommandGroup, CommandItem } from "@/components/ui/command";

interface SearchResultsProps {
  results: Item[];
  isLoading: boolean;
  onItemClick: (item: Item) => void;
  onClose: () => void;
}

const SearchResults = ({ results, isLoading, onItemClick, onClose }: SearchResultsProps) => {
  const navigate = useNavigate();

  const handleSelect = (item: Item) => {
    onItemClick(item);
    onClose();
    navigate(`/item/${item.number}`);
  };

  // Ensure results is always a valid array
  const safeResults = Array.isArray(results) ? results : [];

  return (
    <Command className="rounded-lg border shadow-md">
      {isLoading ? (
        <CommandEmpty>Searching...</CommandEmpty>
      ) : safeResults.length === 0 ? (
        <CommandEmpty>No results found</CommandEmpty>
      ) : (
        <CommandGroup heading="Search Results">
          {safeResults.map((item) => (
            <CommandItem
              key={item.id}
              value={item.name}
              onSelect={() => handleSelect(item)}
              className="py-2 px-4 cursor-pointer"
            >
              <span className="text-sm">{item.name}</span>
            </CommandItem>
          ))}
        </CommandGroup>
      )}
    </Command>
  );
};

export default SearchResults;
