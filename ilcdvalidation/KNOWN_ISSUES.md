## Known issues

Dependency updates for commons-io and commons-compress (both to 2.16.1) are 
currently not possible to incompatibilities with truevfs. We need to remove
the truevfs dependency first and rework the code accordingly in order for these
dependency updates to be possible.