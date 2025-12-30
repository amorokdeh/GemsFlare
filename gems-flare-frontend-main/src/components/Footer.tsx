import { Link } from "react-router-dom";

const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="bg-black border-t border-green-700/30">
      <div className="container mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div>
            <h3 className="text-lg font-bold mb-4 text-white">Gems Flare</h3>
            <p className="text-gray-400">
              Your one-stop destination for quality products at great prices.
            </p>
          </div>
          
          <div>
            <h3 className="text-lg font-bold mb-4 text-white">Shop</h3>
            <ul className="space-y-2">
              <li><Link to="/" className="text-gray-400 hover:text-green-500">New Arrivals</Link></li>
              <li><Link to="/" className="text-gray-400 hover:text-green-500">Best Sellers</Link></li>
              <li><Link to="/" className="text-gray-400 hover:text-green-500">Sale</Link></li>
              <li><Link to="/" className="text-gray-400 hover:text-green-500">All Products</Link></li>
            </ul>
          </div>
          
          <div>
            <h3 className="text-lg font-bold mb-4 text-white">Customer Service</h3>
            <ul className="space-y-2">
              <li><Link to="/" className="text-gray-400 hover:text-green-500">Contact Us</Link></li>
              <li><Link to="/" className="text-gray-400 hover:text-green-500">Shipping & Returns</Link></li>
              <li><Link to="/" className="text-gray-400 hover:text-green-500">FAQ</Link></li>
              <li><Link to="/" className="text-gray-400 hover:text-green-500">Terms & Conditions</Link></li>
            </ul>
          </div>
          
          <div>
            <h3 className="text-lg font-bold mb-4 text-white">Connect With Us</h3>
            <ul className="space-y-2">
              <li><a href="#" className="text-gray-400 hover:text-green-500">Facebook</a></li>
              <li><a href="#" className="text-gray-400 hover:text-green-500">Instagram</a></li>
              <li><a href="#" className="text-gray-400 hover:text-green-500">Twitter</a></li>
              <li><a href="#" className="text-gray-400 hover:text-green-500">Pinterest</a></li>
            </ul>
          </div>
        </div>
        
        <div className="border-t border-green-700/30 mt-8 pt-8 text-center text-gray-400">
          <p>&copy; {currentYear} Gems Flare. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
