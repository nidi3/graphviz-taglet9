# graphviz-taglet9
Use graphviz in javadoc comments.
This is the JDK9 compatible version the [taglet](https://github.com/nidi3/graphviz-java/tree/master/graphviz-taglet)
module of [graphviz-java](https://github.com/nidi3/graphviz-java).

## Usage
## Javadoc
To use graphviz inside javadoc comments, add this to `pom.xml`:
```xml
<build>
  <plugins>
    <plugin>
      <artifactId>maven-javadoc-plugin</artifactId>
      <version>3.1.0</version>
      <configuration>
        <taglet>guru.nidi.graphviz.GraphvizTaglet</taglet>
        <tagletArtifact>
          <groupId>guru.nidi</groupId>
          <artifactId>graphviz-taglet9</artifactId>
          <version>0.10.0</version>
        </tagletArtifact>
      </configuration>
    </plugin>
  </plugins>
</build>
```

The usage inside javadoc is then as follows:
```java
/**
 * Support graphviz inside javadoc.
 * <p>
 * {@graphviz
 * graph test { a -- b }
 * }
 * </p>
 * So easy.
 */
public class GraphvizTaglet implements Taglet {}
```

