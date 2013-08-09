RestLoli
========
Try to provide support for JAX-RS 1.1 Reference Implementation.


###Build Status###
[![Build Status](https://drone.io/github.com/chocotan/RestLoli/status.png)](https://drone.io/github.com/chocotan/RestLoli/latest)

[![Build Status](https://buildhive.cloudbees.com/job/chocotan/job/RestLoli/badge/icon)](https://buildhive.cloudbees.com/job/chocotan/job/RestLoli/)


###Maven###
*  RestLoli has bean published to the Central Repository
*  The latest version is 0.0.2

```
<dependency>
    <groupId>io.loli</groupId>
    <artifactId>restloli</artifactId>
    <version>0.0.2-RELEASE</version>
</dependency>
```
If you want to build from the source of the latest version, use the following command ,

```
git clone git@github.com:chocotan/RestLoli.git
cd RestLoli
mvn clean install
```

###Usage###
*  Add these to web.xml
*  Change `<url-pattern>change me</url-pattern>` to anything you like.
*  Change `<param-value>` to the package your restful classes placed.

```
<servlet>
  <servlet-name>restloli</servlet-name>
  <servlet-class>io.loli.restloli.core.servlet.LoliServlet</servlet-class>
  <init-param>
    <param-name>io.loli.restloli.core.init.PackageName</param-name>
    <param-value>io.loli.restloli.core.servlet</param-value>
  </init-param>
  <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
  <servlet-name>restloli</servlet-name>
  <url-pattern>/rest/*</url-pattern>
</servlet-mapping>
```
###License###
This project is under the MIT license.
See [LICENSE](https://github.com/chocotan/RestLoli/blob/master/LICENSE) for more information.
