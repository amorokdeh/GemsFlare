import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";
import { userService } from "@/services/userService";
import { UserProfile, DeliveryAddress, BillAddress } from "@/types";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { AlertCircle, User, MapPin, Trash2 } from "lucide-react";
import Navbar from "@/components/Navbar";
import { EditProfileForm } from "@/components/EditProfileForm";
import { DeleteAccountDialog } from "@/components/DeleteAccountDialog";
import { AddShippingAddressForm } from "@/components/AddShippingAddressForm";
import { AddBillingAddressForm } from "@/components/AddBillingAddressForm";
import { EditShippingAddressForm } from "@/components/EditShippingAddressForm";
import { EditBillingAddressForm } from "@/components/EditBillingAddressForm";
import { toast } from "@/hooks/use-toast";
import { ChangePasswordForm } from "@/components/ChangePasswordForm";

const Profile = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [shippingAddress, setShippingAddress] = useState<DeliveryAddress | null>(null);
  const [billingAddress, setBillingAddress] = useState<BillAddress | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isRemovingShipping, setIsRemovingShipping] = useState(false);
  const [isRemovingBilling, setIsRemovingBilling] = useState(false);

  const fetchUserProfile = useCallback(async () => {
    if (!user) return;
    
    try {
      setIsLoading(true);
      const profileData = await userService.getUserProfile(user.id);
      setProfile(profileData);
      setError(null);
    } catch (err) {
      console.error("Error fetching user profile:", err);
      setError("Failed to load profile data. Please try again later.");
    } finally {
      setIsLoading(false);
    }
  }, [user]);

  const fetchShippingAddress = useCallback(async () => {
    if (!user) return;
    
    try {
      const addressData = await userService.getShippingAddress();
      setShippingAddress(addressData);
    } catch (err) {
      console.error("Error fetching shipping address:", err);
      // 404 is expected if no address exists
      if (!(err instanceof Error && err.message.includes("404"))) {
        toast({
          variant: "destructive",
          title: "Error",
          description: "Failed to load shipping address."
        });
      }
      setShippingAddress(null);
    }
  }, [user]);

  const fetchBillingAddress = useCallback(async () => {
    if (!user) return;
    
    try {
      const addressData = await userService.getBillingAddress();
      setBillingAddress(addressData);
    } catch (err) {
      console.error("Error fetching billing address:", err);
      // 404 is expected if no address exists
      if (!(err instanceof Error && err.message.includes("404"))) {
        toast({
          variant: "destructive",
          title: "Error",
          description: "Failed to load billing address."
        });
      }
      setBillingAddress(null);
    }
  }, [user]);

  const handleRemoveShippingAddress = async () => {
    setIsRemovingShipping(true);
    try {
      await userService.removeShippingAddress();
      setShippingAddress(null);
      toast({
        title: "Address removed",
        description: "Your shipping address has been removed successfully."
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to remove shipping address."
      });
    } finally {
      setIsRemovingShipping(false);
    }
  };

  const handleRemoveBillingAddress = async () => {

    setIsRemovingBilling(true);
    try {
      await userService.removeBillingAddress();
      setBillingAddress(null);
      toast({
        title: "Address removed",
        description: "Your billing address has been removed successfully."
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to remove billing address."
      });
    } finally {
      setIsRemovingBilling(false);
    }
  };

  useEffect(() => {
    // Check if user is logged in
    if (!user) {
      navigate("/login");
      return;
    }

    // Fetch user profile and addresses
    fetchUserProfile();
    fetchShippingAddress();
    fetchBillingAddress();
  }, [user, navigate, fetchUserProfile, fetchShippingAddress, fetchBillingAddress]);

  return (
    <div className="min-h-screen bg-black text-white">
      <Navbar />
      
      <main className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-6 text-white">My Profile</h1>
        
        {isLoading && (
          <div className="flex justify-center items-center h-64">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
          </div>
        )}
        
        {error && (
          <Alert variant="destructive" className="mb-6">
            <AlertCircle className="h-4 w-4" />
            <AlertTitle>Error</AlertTitle>
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}
        
        {profile && !isLoading && (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {/* Personal Information */}
            <Card className="md:col-span-2 bg-gray-900 border-green-700/30">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <User className="h-5 w-5" />
                  Personal Information
                </CardTitle>
              </CardHeader>
              <CardContent>
                <Table>
                  <TableBody>
                    <TableRow>
                      <TableHead className="w-[200px]">Username</TableHead>
                      <TableCell>{profile.username}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableHead>Full Name</TableHead>
                      <TableCell>{profile.name} {profile.lastname}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableHead>Email</TableHead>
                      <TableCell>{profile.email}</TableCell>
                    </TableRow>
                    {profile.telephone && (
                      <TableRow>
                        <TableHead>Phone</TableHead>
                        <TableCell>{profile.telephone}</TableCell>
                      </TableRow>
                    )}
                    <TableRow>
                      <TableHead>Role</TableHead>
                      <TableCell className="capitalize">{profile.role}</TableCell>
                    </TableRow>
                  </TableBody>
                </Table>

                <div className="mt-6 flex gap-2">
                  <EditProfileForm profile={profile} onProfileUpdated={fetchUserProfile} />
                  <ChangePasswordForm onPasswordChanged={fetchUserProfile} />
                </div>
                
                <div className="mt-6">                  
                  
                  <div className="border-t mt-8 pt-6">
                    <p className="text-sm text-muted-foreground mb-4">
                      Once you delete your account, there is no going back. Please be certain.
                    </p>
                    <DeleteAccountDialog />
                  </div>
                </div>
              </CardContent>
            </Card>
            
            {/* Addresses */}
            <Card className="bg-gray-900 border-green-700/30">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <MapPin className="h-5 w-5" />
                  Addresses
                </CardTitle>
              </CardHeader>
              <CardContent>
                {/* Shipping Address Section */}
                <div className="mb-6">
                  <h3 className="font-medium mb-2">Shipping Address</h3>
                  
                  {shippingAddress ? (
                    <div>
                      <p className="font-medium">{shippingAddress.name} {shippingAddress.lastname}</p>
                      <p>{shippingAddress.street} {shippingAddress.housenumber}</p>
                      <p>{shippingAddress.zipcode} {shippingAddress.county}</p>
                      <p>{shippingAddress.country}</p>
                      
                      <div className="flex gap-2 mt-2">
                        <EditShippingAddressForm 
                          address={shippingAddress} 
                          onAddressUpdated={fetchShippingAddress} 
                        />
                        
                        <Button 
                          variant="outline" 
                          size="sm" 
                          className="flex gap-1 text-destructive hover:text-destructive"
                          onClick={handleRemoveShippingAddress}
                          disabled={isRemovingShipping}
                        >
                          <Trash2 className="h-4 w-4" />
                          {isRemovingShipping ? "Removing..." : "Remove"}
                        </Button>
                      </div>
                    </div>
                  ) : (
                    <div>
                      <p className="text-muted-foreground">No shipping address added</p>
                      <AddShippingAddressForm onAddressAdded={fetchShippingAddress} />
                    </div>
                  )}
                </div>
                
                {/* Billing Address Section */}
                <div className="border-t pt-4 mt-4">
                  <h3 className="font-medium mb-2">Billing Address</h3>
                  
                  {billingAddress ? (
                    <div>
                      <p className="font-medium">{billingAddress.name} {billingAddress.lastname}</p>
                      <p>{billingAddress.street} {billingAddress.housenumber}</p>
                      <p>{billingAddress.zipcode} {billingAddress.county}</p>
                      <p>{billingAddress.country}</p>
                      
                      <div className="flex gap-2 mt-2">
                        <EditBillingAddressForm 
                          address={billingAddress} 
                          onAddressUpdated={fetchBillingAddress} 
                        />
                        
                        <Button 
                          variant="outline" 
                          size="sm" 
                          className="flex gap-1 text-destructive hover:text-destructive"
                          onClick={handleRemoveBillingAddress}
                          disabled={isRemovingBilling}
                        >
                          <Trash2 className="h-4 w-4" />
                          {isRemovingBilling ? "Removing..." : "Remove"}
                        </Button>
                      </div>
                    </div>
                  ) : (
                    <div>
                      <p className="text-muted-foreground">No billing address added</p>
                      <AddBillingAddressForm onAddressAdded={fetchBillingAddress} />
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>
        )}
      </main>
    </div>
  );
};

export default Profile;
