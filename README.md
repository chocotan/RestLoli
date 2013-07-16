RestLoli
========
Try to provide support for JAX-RS 1.1 Reference Implementation.

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
