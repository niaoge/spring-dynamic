这是一个动态的spring项目及范例

动态地生成spring bean,包括entity,dao,manager(service),controller等等 动态类之间可以相互
引用，修改的代码（.java/.groovy）直接增加到app相应目录下，在不需要重启app的情况下，自动
被编译成java class 字节码，并添加（替换）到spring bean中,而且可以重复替换.同时删除的java文件
相应的class也会从spring bean 中删除
在本项目中，.java/.groovy的增删修改如同.jsp/php一样

1-hi-utils 是工具包

2-hi-spring-dynamic 是本项目

3-hi-springMvcWeb 范例(spring mvc+hibernate+spring-dynimic)

3-hi-springScript 是java包,也可以指通常的业务层，因为是动态，propertyEditor ,interceptor也可以写在里面,
它有一个 linked source类型的文件夹,指向 3-hi-springMvcWeb\WebRoot\WEB-INF\script ，而不是在本地文件夹下，
在3-hi-springScript项目中修改后的代码，在测式环境下如同.jsp一样发布即生效，在生产环境下（升级），
直接将(.java/.grooy)拷贝到相应的文件夹下WebRoot\WEB-INF\script，随即生效，不用重启web容器(tomcat)

The spring-dynamic project and it'samples
 
the spring-dynamic project can generate spring bean dynamically,The dynamic java/groovy file can be 
import to another dynamic java/groovy file. They all can be add ,modify,delete,and then the spring context does
the same.
In spring-dyanmaic ,.java/.groovy can be deployed just like .jsp/php

1-hi-utils folder is tool project and depended by the spring-dyamic project
2-hi-spring-dynamic folder contains spring-dynamic project
3-hi-springMvcWeb is the sample(spring mvc+hibernate+spring-dynimic)
3-hi-springScript js java(scprit) project,it has a linked source folder to 3-hi-springMvcWeb\WebRoot\WEB-INF\script
java/groovy file in 3-hi-springScript can be deployed as .jsp file,and not need restart the webapp container(tomcat) 







