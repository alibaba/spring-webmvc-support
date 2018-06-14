# spring-webmvc-support

An support project of Spring Web MVC



## Dependencies & Compatibility

| Dependencies   | Compatibility |
| -------------- | ------------- |
| Java           | 1.6 +         |
| Servlet        | 2.5 +         |
| Spring Web MVC | 3.2 +         |




## Release version

````xml
<dependencies>

    ......

     <!-- Spring Web MVC -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
        <version>${spring.framework.version}</version>
    </dependency>

    <!-- Spring Web MVC Support -->
    <dependency>
        <groupId>com.alibaba.spring</groupId>
        <artifactId>spring-webmvc-support</artifactId>
        <version>1.0.0.RELEASE</version>
    </dependency>

    ......

</dependencies>
````



If your project failed to resolve the dependency, try to add the following repository:
```xml
    <repositories>
        <repository>
            <id>sonatype-nexus</id>
            <url>https://oss.sonatype.org/content/repositories/releases</url>
            <releases>
                <enabled>true</enabled>
            </releases>
        </repository>
    </repositories>
```