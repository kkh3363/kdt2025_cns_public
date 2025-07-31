```
tasks.jar {
    manifest { attributes["Main-Class"] = "{testjson.Maintest}" }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
```
