import { createContext, useContext, useState, ReactNode } from "react";
import { Language } from "@/types";

type LanguageContextType = {
  language: Language;
  setLanguage: (language: Language) => void;
  t: (key: string) => string;
};

const LanguageContext = createContext<LanguageContextType | undefined>(undefined);

// Simple translations object
const translations = {
  EN: {
    "nav.search": "Search products...",
    "nav.login": "Login",
    "nav.logout": "Logout",
    "nav.profile": "My Profile",
    "products.featured": "Featured Products",
    "product.addToCart": "Add to Cart",
    "product.added": "has been added to your cart",
    // Add more translations as needed
  },
  DE: {
    "nav.search": "Produkte suchen...",
    "nav.login": "Anmelden",
    "nav.logout": "Abmelden",
    "nav.profile": "Mein Profil",
    "products.featured": "Ausgewählte Produkte",
    "product.addToCart": "In den Warenkorb",
    "product.added": "wurde in Ihren Warenkorb gelegt",
    // Add more translations as needed
  },
};

export const LanguageProvider = ({ children }: { children: ReactNode }) => {
  const [language, setLanguage] = useState<Language>("EN");

  // Translation function
  const t = (key: string): string => {
    return translations[language][key as keyof typeof translations[typeof language]] || key;
  };

  return (
    <LanguageContext.Provider value={{ language, setLanguage, t }}>
      {children}
    </LanguageContext.Provider>
  );
};

export const useLanguage = (): LanguageContextType => {
  const context = useContext(LanguageContext);
  if (context === undefined) {
    throw new Error("useLanguage must be used within a LanguageProvider");
  }
  return context;
};
