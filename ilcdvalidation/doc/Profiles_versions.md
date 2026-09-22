Version 1.8
-----------

add property

`Profile-Readme`

which points to a text file (MarkDown format) with a general description of the validation profile.



Version 1.7
-----------

add property

`Profile-SupportedAspects`

This carries a comma separated list of aspect descriptors which are supported by this profile.
No validation of unsupported aspects should be performed against a profile. 



Version 1.6
-----------

add properties 

`Profile-Changelog-Technical` and
`Profile-Changelog-Semantic`

which point to text files (MarkDown format) with the changes for the profile, one for technical and one for semantic changes (rules). 



Version 1.5
-----------

add property `Profile-ActiveAspects`

This carries a comma separated list of aspect descriptors which will be pre-selected by default when loading this profile. 

Allowed values are

`Archive Structure`
`Links`
`Orphaned Items`
`Reference Flows`
`XML Schema Validity`
`Custom Validity`
`Categories`



Version 1.4
-----------

add property `Profile-Description` - a description of the profile.

 