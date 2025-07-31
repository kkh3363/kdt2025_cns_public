
build.gradles
```
jar {
    ...
    from { configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) } }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
```

build.gradles.kts 에 다음을 추가
```
tasks.jar {
    manifest { attributes["Main-Class"] = "{testjson.Maintest}" }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
```
