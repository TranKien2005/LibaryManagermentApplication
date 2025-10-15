{ pkgs, ... }: {
	# Which nixpkgs channel to use.
	channel = "stable-23.11"; # Or "unstable"
	# Use https://search.nixos.org/packages to find packages
	packages = [
		pkgs.jdk21
		pkgs.maven
	];
	# Sets environment variables in the workspace
	env = {};
	# Search for the extensions you want on https://open-vsx.org/ and use "publisher.id"

}
