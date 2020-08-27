# This file controls the pinned version of nixpkgs we use for our Nix environment
# as well as which versions of package we use, including their overrides.
{ config ? { } }:

let
  inherit (import <nixpkgs> { }) fetchFromGitHub;

  # For testing local version of nixpkgs
  #nixpkgsSrc = (import <nixpkgs> { }).lib.cleanSource "/home/jakubgs/work/nixpkgs";

  # Our own nixpkgs fork with custom fixes
  nixpkgsSrc = fetchFromGitHub {
    name = "nixpkgs-source";
    owner = "numinit";
    repo = "nixpkgs";
    rev = "1f930f80c1d407734b19dcf67d9e7d7fce8b985e";
    sha256 = "1skhlnnnx5zj2apiiammzhgjdyg7g5wsafpq6xah9p017mv1kk46";
    # To get the compressed Nix sha256, use:
    # nix-prefetch-url --unpack https://github.com/${ORG}/nixpkgs/archive/${REV}.tar.gz
  };

  # Status specific configuration defaults
  defaultConfig = import ./config.nix;

  # Override some packages and utilities
  pkgsOverlay = import ./overlay.nix;
in
  # import nixpkgs with a config override
  (import nixpkgsSrc) {
    config = defaultConfig // config;
    overlays = [ pkgsOverlay ];
  }
