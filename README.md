# View Point Finder
### Prerequisite 
* Java 17

### Build
Run `./gradlew clean build` (or `./gradlew.bat clean build` on Windows) to build the project.

Afterwards, the Jar can be found in `build/libs` and executed running:
```
java -jar view-point-finder.jar <Path to mesh json> <numberOfViewPoints>
```

### Important Note
Transitive neighbors are not being processed. Only direct neighbors are being considered.
