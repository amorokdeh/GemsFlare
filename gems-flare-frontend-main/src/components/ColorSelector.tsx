import { useState } from "react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { X } from "lucide-react";

const COLORS = {
  "Red": "#ea384c",
  "Blue": "#0EA5E9",
  "Green": "#22C55E",
  "Purple": "#9b87f5",
  "Yellow": "#EAB308",
  "Orange": "#F97316",
  "Pink": "#EC4899",
  "Gray": "#71717A",
  "Black": "#171717",
  "White": "#FFFFFF",
};

interface ColorSelectorProps {
  colorGroup: string;
  selectedColor: string | undefined;
  onColorSelect: (color: string | undefined) => void;
}

export function ColorSelector({ colorGroup, selectedColor, onColorSelect }: ColorSelectorProps) {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="flex items-center gap-2">
      <Badge variant="secondary" className="px-3 py-1">
        {colorGroup}
      </Badge>
      <Popover open={isOpen} onOpenChange={setIsOpen}>
        <PopoverTrigger asChild>
          {selectedColor ? (
            <div className="flex gap-1 items-center">
              <div
                className="w-6 h-6 rounded-full border border-gray-200 cursor-pointer"
                style={{ backgroundColor: COLORS[selectedColor as keyof typeof COLORS] }}
              />
              <Button
                variant="ghost"
                size="icon"
                className="h-6 w-6"
                onClick={(e) => {
                  e.stopPropagation();
                  onColorSelect(undefined);
                }}
              >
                <X className="h-4 w-4" />
              </Button>
            </div>
          ) : (
            <Button variant="outline" size="sm">
              Select Color
            </Button>
          )}
        </PopoverTrigger>
        <PopoverContent className="w-48 p-2">
          <div className="grid grid-cols-5 gap-2">
            {Object.entries(COLORS).map(([colorName, colorValue]) => (
              <div
                key={colorName}
                className="w-6 h-6 rounded-full border border-gray-200 cursor-pointer hover:scale-110 transition-transform"
                style={{ backgroundColor: colorValue }}
                onClick={() => {
                  onColorSelect(colorName);
                  setIsOpen(false);
                }}
              />
            ))}
          </div>
        </PopoverContent>
      </Popover>
    </div>
  );
}