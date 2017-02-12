# scala-dependencies

Solution for creating structure representing dependencies between classes, fields and methods in Java programs.

This README file (and the whole project actually) is work in progress.

Coming soon: 
- [ ] Full and useful README file
- [ ] UI (based on ScalaFX) 
- [ ] More new features

# Getting started
To begin you may use some examples that placed in `Dependencies.main`. 
There you will find two simplest possible ways to use this solution:  

```
Dependencies.createFromJavaFiles("d:/path/to/project/src/")
Dependencies.createFromClassFiles("d:/path/to/project/out/")
```

# What you need to run this project
SBT, Scala, Java (of course) and (most important) the desire to find all the dependencies in your Java project!