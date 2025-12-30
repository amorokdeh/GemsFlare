import { useLocation, useNavigate } from "react-router-dom";
import { useEffect } from "react";

const NotFound = () => {
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    // Check if this is a PayPal return URL
    if (location.pathname === "/return" || location.search.includes("token=") && location.search.includes("PayerID=")) {
      // Redirect to the order confirmation page with the query parameters
      navigate({
        pathname: "/order-confirmation",
        search: location.search
      });
      return;
    }

    // Check if this is a PayPal cancel URL
    if (location.pathname === "/cancel" || location.search.includes("token=") && location.pathname.includes("cancel")) {
      // Redirect to the checkout page with a cancel parameter
      navigate({
        pathname: "/checkout",
        search: "?cancelled=true"
      });
      return;
    }

    console.error(
      "404 Error: User attempted to access non-existent route:",
      location.pathname
    );
  }, [location.pathname, location.search, navigate]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="text-center">
        <h1 className="text-4xl font-bold mb-4">404</h1>
        <p className="text-xl text-gray-600 mb-4">Oops! Page not found</p>
        <a href="/" className="text-blue-500 hover:text-blue-700 underline">
          Return to Home
        </a>
      </div>
    </div>
  );
};

export default NotFound;
