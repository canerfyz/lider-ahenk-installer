# lider-ahenk-installer

### Prerequisites

* [Maven 3](https://maven.apache.org/download.cgi)
* [Eclipse](http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/mars1) IDE for RCP and RAP Developers (**Luna** or **Mars** version)

### How to Setup Development Environment

1. Project uses [Orbit](http://www.eclipse.org/orbit/) update site to resolve dependencies. To install Orbit, Open Help > Install New Software and Add [Orbit URL](http://download.eclipse.org/tools/orbit/downloads/drops/R20150821153341/) as update side, click Select All and then click Finish to install.
2. Open File > Import > Existing Maven Projects and Browse to root folder of the project, click Finish.
3. Navigate to `dependencies/` folder and run `mvn clean install` to generate OSGI bundle of third party libraries. This way Eclipse can resolve third party dependencies which exist in manifest file.

### How to export product via Maven

1. Navigate to `dependencies/` folder and run `mvn clean install`.
2. Navigate to `build/` folder and run `mvn clean install` to export product for Linux x86 and x64.

