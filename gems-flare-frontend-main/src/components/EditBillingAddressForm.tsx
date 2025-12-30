import { useState } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { BillAddress } from "@/types";
import { userService } from "@/services/userService";
import { useAuth } from "@/context/AuthContext";
import { toast } from "@/hooks/use-toast";
import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger } from "@/components/ui/sheet";
import { Button } from "@/components/ui/button";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Pencil } from "lucide-react";

const addressFormSchema = z.object({
  name: z.string().min(2, "Name is required").max(50),
  lastname: z.string().min(2, "Last name is required").max(50),
  street: z.string().min(2, "Street is required"),
  housenumber: z.string().min(1, "House number is required"),
  zipcode: z.string().min(5, "Zipcode is required"),
  county: z.string().min(2, "County is required"),
  country: z.string().min(2, "Country is required"),
});

type AddressFormValues = z.infer<typeof addressFormSchema>;

interface EditBillingAddressFormProps {
  address: BillAddress;
  onAddressUpdated: () => void;
}

export function EditBillingAddressForm({ address, onAddressUpdated }: EditBillingAddressFormProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { user } = useAuth();

  const form = useForm<AddressFormValues>({
    resolver: zodResolver(addressFormSchema),
    defaultValues: {
      name: address.name || "",
      lastname: address.lastname || "",
      street: address.street || "",
      housenumber: address.housenumber || "",
      zipcode: address.zipcode || "",
      county: address.county || "",
      country: address.country || "Germany",
    },
  });

  async function onSubmit(data: AddressFormValues) {
    if (!user) return;
    
    setIsSubmitting(true);
    
    try {
      const addressData: BillAddress = {
        name: data.name,
        lastname: data.lastname,
        street: data.street,
        housenumber: data.housenumber,
        zipcode: data.zipcode,
        county: data.county,
        country: data.country,
      };
      
      await userService.editBillingAddress(addressData);
      
      toast({
        title: "Address updated",
        description: "Your billing address has been updated successfully."
      });
      
      onAddressUpdated();
      setIsOpen(false);
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Update failed",
        description: error instanceof Error ? error.message : "Failed to update billing address. Please try again."
      });
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <Sheet open={isOpen} onOpenChange={setIsOpen}>
      <SheetTrigger asChild>
        <Button variant="outline" size="sm" className="flex gap-1">
          <Pencil className="h-4 w-4" />
          Edit
        </Button>
      </SheetTrigger>
      <SheetContent className="overflow-y-auto">
        <SheetHeader>
          <SheetTitle>Edit Billing Address</SheetTitle>
          <SheetDescription>
            Update your billing address information.
          </SheetDescription>
        </SheetHeader>
        
        <div className="py-4">
          <Form {...form}>
            {/* Use a div instead of a form to prevent the outer form submission */}
            <div className="space-y-4">
              <FormField
                control={form.control}
                name="name"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>First Name</FormLabel>
                    <FormControl>
                      <Input placeholder="First name" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              
              <FormField
                control={form.control}
                name="lastname"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Last Name</FormLabel>
                    <FormControl>
                      <Input placeholder="Last name" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              
              <div className="flex gap-4">
                <FormField
                  control={form.control}
                  name="street"
                  render={({ field }) => (
                    <FormItem className="flex-1">
                      <FormLabel>Street</FormLabel>
                      <FormControl>
                        <Input placeholder="Street name" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                
                <FormField
                  control={form.control}
                  name="housenumber"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>House Number</FormLabel>
                      <FormControl>
                        <Input placeholder="House Number" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>
              
              <div className="flex gap-4">
                <FormField
                  control={form.control}
                  name="zipcode"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Postal Code</FormLabel>
                      <FormControl>
                        <Input placeholder="Postal Code" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                
                <FormField
                  control={form.control}
                  name="county"
                  render={({ field }) => (
                    <FormItem className="flex-1">
                      <FormLabel>City</FormLabel>
                      <FormControl>
                        <Input placeholder="City" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>
              
              <FormField
                control={form.control}
                name="country"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Country</FormLabel>
                    <FormControl>
                      <Input value="Germany" readOnly />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              
              <div className="flex gap-2 pt-4">
                <Button 
                  type="button" 
                  disabled={isSubmitting}
                  onClick={() => {
                    form.handleSubmit(onSubmit)();
                  }}
                >
                  {isSubmitting ? "Saving..." : "Save Address"}
                </Button>
                <Button 
                  type="button" 
                  variant="outline" 
                  onClick={() => setIsOpen(false)}
                >
                  Cancel
                </Button>
              </div>
            </div>
          </Form>
        </div>
      </SheetContent>
    </Sheet>
  );
}

export default EditBillingAddressForm;
