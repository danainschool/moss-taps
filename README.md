# MOSS-TAPS
=======
## MOSS-TAPS
A **MOSS** **T**ool **A**ddressing **P**lagiarism at **S**cale

MOSS-TAPS is an unofficial tool for submitting multiple assignments to the MOSS software plagiarism detection site hosted at [Alex Aiken's Stanford Moss page] (http://theory.stanford.edu/~aiken/moss/).  MOSS-TAPS provides the following feature beyond those provided with the official script:
*   Pure Java implementation for portability, using the [Java MOJI submission script by Bjoern Zielke] (https://github.com/nordicway/moji)
*   Configurable features in  `mtconfig.txt`.  See `mtconfig-sample.txt` to view default values and keys.
*   Preprocesses nested and zip files within student submissions
*   Preprocesses multiple languages for submission (e.g. a class allows more than one language implementation)
*   Preprocesses past semester projects and current semester projects so they can be submitted together
*   Filters results so that past project vs past project are not included and current students are not compared with themselves.

## Requirements
* Java 6 or better
* Apache Commons IO 2.4
* moji 1.0.1
* zip4j 1.3.2
* jsoup 1.8.3

## Installation
## Quick Start

### 1. Preparation
Set up past and current projects in the following file structures:

```
Original
|- Project 1
   |- Student 1
      |- files.java, files.py, files.zip, directories, etc.
   |- Student 1
      |- files.java, files.py, files.zip, directories, etc.
   |- ...
|- Project 2
   |- Student 1
      |- files.java, files.py, files.zip, directories, etc.
   |- Student 1
      |- files.java, files.py, files.zip, directories, etc.
   |- ...
   
Current
|- Project 1
   |- Student 1
      |- files.java, files.py, files.zip, directories, etc.
   |- Student 1
      |- files.java, files.py, files.zip, directories, etc.
   |- ...
|- Project 2
   |- Student 1
      |- files.java, files.py, files.zip, directories, etc.
   |- Student 1
      |- files.java, files.py, files.zip, directories, etc.
   |- ...
```
### 2. Multi-platform executable JAR files
* To run MOSS-TAPS on any platform that has Java 6 or better installed, download the zip file `MossTaps.zip` which contains `MossTaps.jar`, a directory of libraries `MossTaps_lib`, and an empty directory `data`.  
* Load the previously prepared `Original` and `Current` project directories (see above) under the `data` directory.  If there are starter code files that should be excluded from scrutiny, these may be loaded in a directory under `data` named `Base`.
* From a terminal window, navigate to the directory containing `MossTaps.jar`, the `MossTaps_lib` directory, and the `data` directory.  
*  Execute  the jar with the command `java -jar MossTaps.jar`
*  If this is the first time MossTaps has been run, the program will ask for the 9-digit userID.
*  The program will run, giving some feedback as to when files are uploaded and when results have been received from MOSS.  Final results will be placed CSV files tagged by date.

## Documentation
In addition to this README, the `mtconfig-sample.txt` file provides details on the default configuration values and how they may be overwritten. 
## Attribution
This project was created as an Educational Technology project in the Georgia Tech OMSCS program class CS6460.  It was inspired by the need for a management tool when submitting large online class canons to MOSS in the program, and in particular the previous scripting work for this problem done by Dr. David Joyner.
This project would not work at all without the other pieces of the puzzle: [MOSS created by Alex Aiken's & hosted at Stanford] (http://theory.stanford.edu/~aiken/moss/) as well as the [Java MOJI submission script by Bjoern Zielke] (https://github.com/nordicway/moji).  In addition, the following open source libraries have made working with file structures and parsing web pages a breeze:
* [Apache Commons] (https://commons.apache.org/)
* zip4j [by Srikanth Reddy Lingala] (http://www.lingala.net/zip4j/)
* jsoup [from jsoup.org] (http://jsoup.org/)

## License
MOSS-TAPS is under the MIT License.  Please see corresponding [license file] (https://github.com/danainschool/moss-taps/blob/master/LICENSE).
