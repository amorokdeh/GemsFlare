import { useRef, useState } from "react";
import { Canvas, useFrame } from "@react-three/fiber";
import { OrbitControls, PerspectiveCamera } from "@react-three/drei";
import { Mesh } from "three";
import { Button } from "@/components/ui/button";
import { ZoomIn, ZoomOut, RotateCw } from "lucide-react";
import { useLoader } from "@react-three/fiber";
import { OBJLoader } from "three-stdlib";

interface ModelProps {
  url: string;
}

function Model({ url }: ModelProps) {
  const obj = useLoader(OBJLoader, url);
  const mesh = useRef<Mesh>(null);

  // Add a slow horizontal rotation effect using useFrame
  useFrame(() => {
    if (mesh.current) {
      mesh.current.rotation.y += 0.005;  // Rotate slowly on the Y-axis (horizontal)
    }
  });

  return <primitive ref={mesh} object={obj} scale={1} position={[0, 0, 0]} />;
}

interface ModelViewerProps {
  modelUrl: string;
  fallbackImgUrl: string;
}

const ModelViewer = ({ modelUrl, fallbackImgUrl }: ModelViewerProps) => {
  const [zoomLevel, setZoomLevel] = useState(5);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState(false);

  const handleZoomIn = () => {
    setZoomLevel(Math.max(2, zoomLevel - 1));
  };

  const handleZoomOut = () => {
    setZoomLevel(Math.min(10, zoomLevel + 1));
  };

  const handleReset = () => {
    setZoomLevel(5);
  };

  return (
    <div className="relative aspect-square w-full h-full rounded-lg overflow-hidden border">
      {loadError ? (
        <div className="w-full h-full flex items-center justify-center">
          <img
            src={fallbackImgUrl || "/placeholder.svg"}
            alt="Product"
            className="object-contain w-full h-full"
            onError={(e) => {
              (e.target as HTMLImageElement).src = "/placeholder.svg";
            }}
          />
        </div>
      ) : (
        <>
          {loading && (
            <div className="absolute inset-0 flex items-center justify-center bg-gray-50">
              <div className="text-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
                <p className="mt-2 text-sm text-gray-600">Loading 3D model...</p>
              </div>
            </div>
          )}
          <Canvas onCreated={() => setLoading(false)} onError={() => setLoadError(true)}>
            <ambientLight intensity={0.5} />
            <directionalLight position={[10, 10, 5]} intensity={1} />
            <PerspectiveCamera makeDefault position={[0, 0, zoomLevel]} />
            <OrbitControls enableZoom={true} enablePan={true} />
            <Model url={modelUrl} />
          </Canvas>

          <div className="absolute top-2 right-2 flex flex-col gap-2">
            <Button variant="secondary" size="icon" onClick={handleZoomIn} className="bg-white/50 backdrop-blur-sm hover:bg-white/80">
              <ZoomIn className="h-4 w-4" />
            </Button>
            <Button variant="secondary" size="icon" onClick={handleZoomOut} className="bg-white/50 backdrop-blur-sm hover:bg-white/80">
              <ZoomOut className="h-4 w-4" />
            </Button>
            <Button variant="secondary" size="icon" onClick={handleReset} className="bg-white/50 backdrop-blur-sm hover:bg-white/80">
              <RotateCw className="h-4 w-4" />
            </Button>
          </div>
        </>
      )}
      <div className="absolute bottom-2 left-2 text-xs text-white bg-black/50 px-2 py-1 rounded backdrop-blur-sm">
        Click and drag to rotate • Scroll to zoom
      </div>
    </div>
  );
};

export default ModelViewer;
