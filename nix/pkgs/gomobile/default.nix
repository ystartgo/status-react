{ stdenv, utils, callPackage, fetchgit, buildGoPackage,
  ncurses5, zlib, makeWrapper, patchelf, androidPkgs, xcodeWrapper
}:

let
  inherit (stdenv) isDarwin;
  inherit (stdenv.lib) optional optionalString strings;
in buildGoPackage rec {
  pname = "gomobile";
  version = "20200801-${strings.substring 0 7 rev}";
  rev = "973feb4309de5c3aad0553fc987d76abf1fa58f7";
  sha256 = "0jwz7jq1cqp9x6b9kpfrmw84sg7dg5v4xaa8zdbgq14gskmjx72d";

  goPackagePath = "golang.org/x/mobile";
  subPackages = [ "bind" "cmd/gobind" "cmd/gomobile" ];
  goDeps = ./deps.nix;

  buildInputs = [ makeWrapper ]
    ++ optional isDarwin xcodeWrapper;

  # Ensure XCode and the iPhone SDK are present, instead of failing at the end of the build
  preConfigure = optionalString isDarwin utils.enforceiPhoneSDKAvailable;

  patches = [ ./resolve-nix-android-sdk.patch ];

  postPatch = ''
    substituteInPlace cmd/gomobile/install.go --replace "\`adb\`" "\`${androidPkgs}/bin/adb\`"
    
    # Prevent a non-deterministic temporary directory from polluting the resulting object files
    substituteInPlace cmd/gomobile/env.go \
      --replace \
        'tmpdir, err = ioutil.TempDir("", "gomobile-work-")' \
        "tmpdir = filepath.Join(os.Getenv(\"NIX_BUILD_TOP\"), \"gomobile-work\")" \
      --replace '"io/ioutil"' ""
    substituteInPlace cmd/gomobile/init.go \
      --replace \
        'tmpdir, err = ioutil.TempDir(gomobilepath, "work-")' \
        "tmpdir = filepath.Join(os.Getenv(\"NIX_BUILD_TOP\"), \"work\")"
  '';

  preBuild = ''
    mkdir $NIX_BUILD_TOP/gomobile-work $NIX_BUILD_TOP/work
  '';

  # Necessary for GOPATH when using gomobile.
  postInstall = ''
    echo "Creating $out"
    mkdir -p $out/src/$goPackagePath
    echo "Copying from $src"
    cp -a $src/. $out/src/$goPackagePath
  '';

  src = fetchgit {
    inherit rev sha256;
    name = "gomobile";
    url = "https://go.googlesource.com/mobile";
  };

  meta = with stdenv.lib; {
    description = "A tool for building and running mobile apps written in Go.";
    longDescription = "Gomobile is a tool for building and running mobile apps written in Go.";
    homepage = https://go.googlesource.com/mobile;
    license = licenses.bsdOriginal;
    maintainers = with maintainers; [ sheenobu pombeirp ];
    platforms = with platforms; linux ++ darwin;
  };
}
