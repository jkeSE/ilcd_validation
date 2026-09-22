CHANGES - ILCD Validation Library
=================================

3.0.0
------

- Require Java 11.
- Improve ValidatorChainFactory to include reasonable defaults for all validators.
- Add working example in `src/example` to demonstrate usage.



2.12.3
------

- Fix an issue where an NPE would be thrown when merging statistics from multiple members of a ValidatorChain under certain circumstances.



2.12.2
------

- Fix an issue where profiles would not be loaded on Windows systems when the path to the application contained spaces.



2.12.1
------

- Fix issue where a validation with only warnings would be treated as failed.



2.12.0
------

- Introduce new `ValidatorChainFactory` class to instantiate entire ValidatorChains from a profile, based on the
  supported or active aspects.
- Deprecate `ValidatorChain.initPresetValidators()` in favor of the above.



2.11.0
------

- Add additional logic to get the latest profile by omitting the version number.



2.10.0
------

- Add functionality to register profiles via Maven coordinates (groupId, artifactId, version).



2.9.0
-----

- Add functionality to get profiles via Maven coordinates (groupId, artifactId, version).
- Change key of internal profile store to Maven coordinates.