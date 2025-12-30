import { createContext, useContext, useState, ReactNode } from 'react';
import { useDebounce } from '@/hooks/use-debounce';

interface SearchContextType {
  searchTerm: string;
  debouncedSearchTerm: string;
  setSearchTerm: (term: string) => void;
  clearSearch: () => void;
}

const SearchContext = createContext<SearchContextType | undefined>(undefined);

export const SearchProvider = ({ children }: { children: ReactNode }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearchTerm = useDebounce(searchTerm, 500);

  const clearSearch = () => {
    setSearchTerm('');
  };

  return (
    <SearchContext.Provider 
      value={{ 
        searchTerm, 
        debouncedSearchTerm, 
        setSearchTerm, 
        clearSearch 
      }}
    >
      {children}
    </SearchContext.Provider>
  );
};

export const useSearch = (): SearchContextType => {
  const context = useContext(SearchContext);
  if (context === undefined) {
    throw new Error('useSearch must be used within a SearchProvider');
  }
  return context;
};
