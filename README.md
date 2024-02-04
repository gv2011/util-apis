[![Maven Central](https://img.shields.io/maven-central/v/com.github.gv2011/util-apis.svg)](https://repo1.maven.org/maven2/com/github/gv2011/util-apis/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

# util-apis

Factored out "util" (core) artifact from https://github.com/gv2011/util
to allow independent API versioning.

## Dependency graph

util-apis
+--gsoncore


## Dependencies

    <dependency>
      <groupId>com.github.gv2011</groupId>
      <artifactId>util-apis</artifactId>
      <version>${util-apis.version}</version>
    </dependency>
    
    <dependency>
      <groupId>com.github.gv2011</groupId>
      <artifactId>gcol</artifactId>
      <version>${util.version}</version>
      <scope>runtime</scope>
    </dependency>
    <dependency>
      <groupId>com.github.gv2011</groupId>
      <artifactId>util-beans</artifactId>
      <version>${util.version}</version>
      <scope>runtime</scope>
    </dependency>
    <dependency>
      <groupId>com.github.gv2011</groupId>
      <artifactId>jsong</artifactId>
      <version>${util.version}</version>
      <scope>runtime</scope>
    </dependency>
    <dependency>
      <groupId>com.github.gv2011</groupId>
      <artifactId>util-logback</artifactId>
      <version>${util.version}</version>
      <scope>runtime</scope>
    </dependency>
    
    <dependency>
      <groupId>com.github.gv2011</groupId>
      <artifactId>testutil</artifactId>
      <version>${util.version}</version>
      <scope>test</scope>
    </dependency>
