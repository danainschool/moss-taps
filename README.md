<<<<<<< HEAD
# MOSS-TAPS
=======
## MOSS-TAPS
A **MOSS** **T**ool for **A**dding **P**revious **S**emester assignments

MOSS-TAPS is an unofficial tool for submitting multiple assignments to the MOSS software plagiarism detection site hosted at [Alex Aiken's Stanford Moss page] (http://theory.stanford.edu/~aiken/moss/).  MOSS-TAPS provides the following feature beyond those provided with the official script:
*   Pure Java implementation for portability, using the [Java MOJI submission script by Bjoern Zielke] (https://github.com/nordicway/moji)
*   Configurable features in  `mtconfig.txt`.  See `mtconfig-defaults.txt` to view default values and keys.
*   Preprocesses nested and zip files within student submissions
*   Preprocesses multiple languages for submission (e.g. a class allows more than one language implementation)
*   Preprocesses past semester projects and current semester projects so they can be submitted together
*   Filters results so that past project vs past project are not included and current students are not compared with themselves.

## Requirements
* Java 6 or better
* Apache Commons IO 2.3
* moji 1.0.1
* zip4j 1.3.2
* jsoup 1.8.3

## Installation
## Quick Start

#### 1. Preparation
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
## Documentation
## Attribution
## License
MOSS-TAPS is under the MIT License.  Please see corresponding [license file] (https://github.com/danainschool/moss-taps/blob/master/LICENSE).
>>>>>>> branch 'master' of https://github.com/danainschool/moss-taps.git
