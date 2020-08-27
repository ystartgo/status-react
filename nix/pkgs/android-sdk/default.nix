#
# This Nix expression centralizes the configuration
# for the Android development environment.
#

{ callPackage }:

let
  pkgs = callPackage ./pkgs.nix { };
  shell = callPackage ./shell.nix { };
in {
  inherit pkgs shell;
}
