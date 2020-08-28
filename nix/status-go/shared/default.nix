{ callPackage }:

{
  x86 = callPackage ./build.nix { platform = "android"; arch = "386"; };
  armeabi = callPackage ./build.nix { platform = "androideabi"; arch = "arm"; };
  arm64 = callPackage ./build.nix { platform = "android"; arch = "arm64"; };
}
