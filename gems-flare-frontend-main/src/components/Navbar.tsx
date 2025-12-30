import { useState, useRef } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger, DropdownMenuSeparator } from "@/components/ui/dropdown-menu";
import { useToast } from "@/hooks/use-toast";
import { Language } from "@/types";
import { 
  Search, 
  Globe, 
  User, 
  ShoppingCart, 
  LogOut, 
  X, 
  Package, 
  ShieldCheck 
} from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { useSearch } from "@/context/SearchContext";
import { CartDropdown } from "@/components/CartDropdown";

const Navbar = () => {
  const [language, setLanguage] = useState<Language>("EN");
  const { toast } = useToast();
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { searchTerm, setSearchTerm, clearSearch } = useSearch();
  const searchInputRef = useRef<HTMLInputElement>(null);

  const shouldShowSearch = location.pathname === "/" || location.pathname === "/my-items";

  const handleLanguageChange = (lang: Language) => {
    setLanguage(lang);
    toast({
      title: "Language changed",
      description: `Language set to ${lang}`,
    });
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setSearchTerm(value);
  };

  // Check if current path is active
  const isActive = (path: string) => {
    return location.pathname === path;
  };

  const isAdmin = user?.role === 'ADMIN';
  const isSeller = user?.role === 'SELLER';

  return (
    <header className="bg-black border-b border-green-700/30 shadow-sm sticky top-0 z-10">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo with Text */}
          <Link to="/" className="flex items-center gap-3">
            <img 
              src="/logo.png" 
              alt="Gems Flare Logo" 
              className="h-12 w-20 object-contain" 
            />
          </Link>

          {/* Search Bar */}
          {shouldShowSearch && (
            <div className="hidden md:flex items-center flex-1 max-w-md mx-4 relative">
              <div className="relative w-full" ref={searchInputRef}>
                <Input
                  type="text"
                  placeholder="Search products..."
                  className="pl-10 pr-8 py-2 w-full bg-gray-900 border-green-700/50 text-gray-200"
                  value={searchTerm}
                  onChange={handleSearchChange}
                />
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                {searchTerm && (
                  <button 
                    onClick={clearSearch}
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-200"
                  >
                    <X size={16} />
                  </button>
                )}
              </div>
            </div>
          )}

          {/* Navigation Links */}
          <nav className="flex items-center space-x-4">
            {/* Language Switcher */}
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="outline" size="sm" className="flex items-center border-green-700/50 bg-gray-900 text-gray-200 hover:bg-gray-800">
                  <Globe size={18} className="mr-1" />
                  {language}
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="bg-gray-900 border-green-700/50 text-gray-200">
                <DropdownMenuItem onClick={() => handleLanguageChange("EN")} className="hover:bg-gray-800">
                  English
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => handleLanguageChange("DE")} className="hover:bg-gray-800">
                  Deutsch
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>

            {/* Cart Icon */}
            <CartDropdown />

            {/* User Account */}
            {user ? (
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="outline" className="flex items-center gap-2 border-green-700/50 bg-gray-900 text-gray-200 hover:bg-gray-800">
                    <User size={18} />
                    <span className="hidden sm:inline-block">
                      {user.name} {user.lastname}
                    </span>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-56 bg-gray-900 border-green-700/50 text-gray-200">
                  <DropdownMenuItem asChild className="hover:bg-gray-800">
                    <Link to="/profile" className="w-full flex items-center">
                      <User size={16} className="mr-2" />
                      My Profile
                    </Link>
                  </DropdownMenuItem>
                  
                  <DropdownMenuItem asChild className="hover:bg-gray-800">
                    <Link to="/orders" className="w-full flex items-center">
                      <ShoppingCart size={16} className="mr-2" />
                      My Orders
                    </Link>
                  </DropdownMenuItem>
                  
                  {/* Only show My Items for sellers and admins */}
                  {(isAdmin || isSeller) && (
                    <DropdownMenuItem asChild className="hover:bg-gray-800">
                      <Link to="/my-items" className="w-full flex items-center">
                        <Package size={16} className="mr-2" />
                        My Items
                      </Link>
                    </DropdownMenuItem>
                  )}
                  
                  {/* Only show Admin CM for admins */}
                  {isAdmin && (
                    <DropdownMenuItem asChild className="hover:bg-gray-800">
                      <Link to="/admin" className="w-full flex items-center">
                        <ShieldCheck size={16} className="mr-2" />
                        Admin CM
                      </Link>
                    </DropdownMenuItem>
                  )}
                  
                  <DropdownMenuSeparator className="bg-green-700/30" />
                  
                  <DropdownMenuItem onClick={logout} className="text-red-400 hover:bg-gray-800">
                    <LogOut size={16} className="mr-2" />
                    Logout
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            ) : (
              <Button asChild variant="default" className="bg-green-700 hover:bg-green-600 text-white">
                <Link to="/login">Login</Link>
              </Button>
            )}
          </nav>
        </div>
        
        {/* Mobile Search Bar */}
        {shouldShowSearch && (
          <div className="md:hidden pb-3 relative" ref={searchInputRef}>
            <div className="relative w-full">
              <Input
                type="text"
                placeholder="Search products..."
                className="pl-10 pr-8 py-2 w-full bg-gray-900 border-green-700/50 text-gray-200"
                value={searchTerm}
                onChange={handleSearchChange}
              />
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
              {searchTerm && (
                <button 
                  onClick={clearSearch}
                  className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-200"
                >
                  <X size={16} />
                </button>
              )}
            </div>
          </div>
        )}
      </div>
    </header>
  );
};

export default Navbar;
