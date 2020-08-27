#
# This Nix expression centralizes the configuration
# for the Android development environment.
#

{ stdenv, config, callPackage, androidenv, openjdk, mkShell }:

androidenv.composeAndroidPackages {
  toolsVersion = "26.1.1";
  platformToolsVersion = "30.0.4";
  buildToolsVersions = [ "30.0.2" ];
  includeEmulator = false;
  platformVersions = [ "29" ];
  includeSources = false;
  includeDocs = false;
  includeSystemImages = false;
  systemImageTypes = [ "default" ];
  cmakeVersions = [ "3.10.2" ];
  includeNDK = true;
  ndkVersion = "21.3.6528147";
  useGoogleAPIs = false;
  useGoogleTVAddOns = false;
  includeExtras = [
    "extras;android;m2repository"
    "extras;google;m2repository"
  ];
  sdkLicenseHashes = [
    "601085b94cd77f0b54ff86406957099ebe79c4d6"
    "24333f8a63b6825ea9c5514f83c2829b004d1fee"
    "84831b9409646a918e30573bab4c9c91346d8abd"
    "d975f751698a77b662f1254ddbeed3901e976f5a"
    "33b6a2b64607f11b759f320ef9dff4ae5c47d97a"
  ];
}
