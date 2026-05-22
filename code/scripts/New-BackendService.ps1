param(
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[a-z][a-z0-9-]*$')]
    [string]$Name
)

$root = Split-Path -Parent $PSScriptRoot
$serviceRoot = Join-Path $root "backend\services\$Name"

if (Test-Path $serviceRoot) {
    Write-Error "Backend service already exists: $serviceRoot"
    exit 1
}

$dirs = @(
    $serviceRoot,
    "$serviceRoot\src\main\java",
    "$serviceRoot\src\main\resources",
    "$serviceRoot\src\test\java"
)

foreach ($dir in $dirs) {
    New-Item -ItemType Directory -Path $dir -Force | Out-Null
}

$className = (($Name -replace '^psm-', '') -split '-' | ForEach-Object {
    $_.Substring(0, 1).ToUpper() + $_.Substring(1)
}) -join ''
$className = "$className`Application"
$packageSegment = ($Name -replace '^psm-', '') -replace '-', ''
$packageName = "com.fgroupboss.ai.psm.$packageSegment"

@"
# $Name

后端微服务骨架。

职责：

- 待补充。

"@ | Set-Content -Path "$serviceRoot\README.md" -Encoding UTF8

@"
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.fgroupboss.ai</groupId>
        <artifactId>psm-platform-backend</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../../pom.xml</relativePath>
    </parent>
    <artifactId>$Name</artifactId>
    <name>$Name</name>
    <dependencies>
        <dependency>
            <groupId>com.fgroupboss.ai</groupId>
            <artifactId>psm-common-core</artifactId>
            <version>`$\{project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j2</artifactId>
        </dependency>
    </dependencies>
</project>
"@ | Set-Content -Path "$serviceRoot\pom.xml" -Encoding UTF8

$javaDir = Join-Path "$serviceRoot\src\main\java" ($packageName -replace '\.', '\')
New-Item -ItemType Directory -Path $javaDir -Force | Out-Null

@"
package $packageName;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class $className {

    public static void main(String[] args) {
        SpringApplication.run($className.class, args);
    }
}
"@ | Set-Content -Path "$javaDir\$className.java" -Encoding UTF8

@"
server:
  port: 0

spring:
  application:
    name: $Name
"@ | Set-Content -Path "$serviceRoot\src\main\resources\application.yml" -Encoding UTF8

New-Item -ItemType File -Path "$serviceRoot\src\test\java\.gitkeep" -Force | Out-Null

Write-Host "Created backend service: $serviceRoot"
