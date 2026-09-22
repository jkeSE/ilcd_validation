# ILCD Validation Tool

The ILCD Validation Tool can be used for the technical validation of datasets in ILCD format. It can be used  to check syntax, categories, nomenclature, references between datasets and other technical aspects.



## Obtaining the tool

Download the latest version here:

https://bitbucket.org/okusche/ilcdvalidationtool/downloads

Builds are provided for different operating systems (Windows, macOS, Linux) and also processor architectures. Choose the one appropriate for your operating system. 



## Running the tool

There are no external dependencies, the tool straight from the unzipped folder after downloading. 

Platform specific notes:

On Windows, the Windows Defender may complain about the software being unsafe due to a missing signature. Just choose to run the software anyway. Proper signatures will be added in a future release.

On macOS, it is recommended to copy the application to the `/Applications` folder in order for online updates to work.



## Online updates

The tool supports online updates, so it is not necessary to manually download the latest version if you already have it installed on your system. After launching the tool, it will automatically check whether updates are available and download and install them when you confirm the corresponding dialog. You might need to manually relaunch the program after the update is complete.

During the online update, the latest versions of the validation profiles that are shipping with the tool by default will also be updated. You can always use previous versions of the profiles if necessary (see below for more details on how).



## Validation aspects

The tool offers to perform validations of the following aspects:

Dataset level:

•	ILCD Format Syntax: the dataset conforms to the ILCD Format specification
•	Profile specific riles: the dataset conforms to more specific  rules
•	Compliance to a reference nomenclature: the dataset references only elementary flows (and optionally flow properties and unit groups) that are defined by a reference nomenclature (such as the ILCD or EF system)



On a set of datasets:

•	Links:      all local references (links) between datasets can be resolved
•	Orphaned datasets: in a set of datasets, there are no extra datasets present that are not referenced from any other local dataset



On an archive:

•	Archive Structure: does the ZIP archive comply with the ILCD Format specification regarding its internal folder structure and optional manifest file

The aspects to validate against can be selected using the check boxes in the "Validation Aspects" component.

 

## Validation Profiles

Rules for multiple aspects 

The tool allows for checking against different sets of rules, which are called profiles. In addition to the default ILCD profile, distinct profiles are available to check against ILCD Entry Level (EL) and Environmental Footprint (EF) requirements.
As profiles are sometimes updated, they carry a version number, where the latest version number is always the highest one.



New profiles can be added (or existing ones updated) using the "Add Profile..." menu entry in the profile selector.



## Validating data

Simply drag and drop a ZIP file or folder you have generated from your LCA modelling tool to the area labelled "Drop files or folders here" of the validation tool.





Then select the desired validation profile on the right. The nessary aspects will be automatically preselected for each profile.



Then start the validation by selecting the  green  "Play" button.



The progress of the validation will be displayed. If a file or folder has been validated successfully, a green checkmark will appear next to it. In case errors have been detected, a red "X" will be shown instead.



Validation messages are issued on the bottom section of the application window with details for the respective  dataset.
When validating a folder, you can right-click on an individual message in order to reveal the offending dataset file on the file system. This does not work for a ZIP file (obviously, as it is a single file), however, you can extract the ZIP file first and then validate the extracted folder in order to use this feature.



## Validation Messages
The entire log or single entries may be copied to the system clipboard using the entries in the context menu.

 

## Batch mode

In the Options pane, there's a check box "Batch mode". If activated, validation messages will not be written to the log on the bottom of the application window, but instead into a report spreadsheet document which will have the same name as the file or folder to be validated (just with an `.xlsx` suffix) and is placed in the same folder as the file or folder to be validated.

The report spreadsheet will contain individual sheets for each aspect where issues have been found, plus a summary sheet.



## Options

The Options pane offers a few options to change the behavior of the validator. Usually the defaults should be okay for most use cases. You can hover with your mouse pointer over each option in order to display a tooltip that offers some more explanation. 