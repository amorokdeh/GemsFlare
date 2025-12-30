export interface Product {
  id: number;
  name: string;
  price: number;
  image: string;
  currency: string;
}

export interface Item {
  id: string;
  name: string;
  description: string;
  number: string;
  category: string;
  color_groups: string[];
  selected_colors?: { [key: string]: string };
  price: number;
  amount: number;
  img_src: string;
  object_src: string;
}

export interface ItemsResponse {
  content: Item[];
  pageable: {
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    pageNumber: number;
    pageSize: number;
    paged: boolean;
    unpaged: boolean;
  };
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

export type Language = "EN" | "DE";

export interface User {
  id: string;
  username: string;
  name: string;
  lastname: string;
  token: string;
  role?: string;
  email?: string;
  telephone?: string;
}

export interface UserProfile {
  id: string;
  username: string;
  name: string;
  lastname: string;
  role?: string;
  email: string;
  telephone: string;
}

export interface TokenInfo {
  token: string;
  isValid: boolean;
  expirationDate: string;
  valid: boolean;
}

export interface Address {
  street: string;
  city: string;
  postalCode: string;
  country: string;
}

export interface DeliveryAddress {
  name: string;
  lastname: string;
  street: string;
  housenumber: string;
  zipcode: string;
  county: string;
  country: string;
}

export interface BillAddress {
  name: string;
  lastname: string;
  street: string;
  housenumber: string;
  zipcode: string;
  county: string;
  country: string;
}

export interface AddressFormData {
  street: string;
  city: string;
  postalCode: string;
  country: string;
}

export interface CheckoutItem {
  id: string;
  name: string;
  number: string;
  description: string;
  category: string;
  color_groups: string[];
  price: number;
  amount: number;
  img_src: string;
  object_src: string;
}

export interface Checkout {
  id: string;
  userid: string;
  items: CheckoutItem[];
  sum: number;
  paid: boolean;
  date: string | number;
  number: string;
}

export interface OrderItem {
  id: string;
  name: string;
  number: string;
  category: string;
  color_groups: string[];
  price: number;
  amount: number;
  img_src: string;
}

export interface Order {
  id: string;
  items: OrderItem[];
  sum: number;
  date: number;
  number: string;
  state: string | null;
  transaction: string;
}