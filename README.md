RestLoli
========
Try to provide support for JAX-RS 1.1 Reference Implementation.

###Restloli has bean published to the Central Repository###
* The latest version is 0.0.1
```
<dependency>
    <groupId>io.loli</groupId>
    <artifactId>restloli</artifactId>
    <version>0.0.1-RELEASE</version>
</dependency>
```

###Usage###
*  Add these to web.xml
*  Change `<url-pattern>change me</url-pattern>` to anything you like.
*  Change `<param-value>` to the package your restful classes placed.

```
<servlet>
	<servlet-name>restloli</servlet-name>
	<servlet-class>io.loli.restloli.core.servlet.LoliEnterServlet</servlet-class>
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
###LICENSE###
This project is under the MIT license.
See [LICENSE](https://github.com/chocotan/RestLoli/blob/master/LICENSE) for more information.
