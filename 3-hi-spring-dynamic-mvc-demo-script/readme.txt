tomcat运行起来后，试着改变增加删除 3-hi-spring-dynamic-mvc-demo-script项目下的srcipt文件夹
的java或groovy文件，就知道了

after run tomcat ,try to change/add/delete java/groovy file in source folder of 
3-hi-spring-dynamic-mvc-demo-script project ,you will know



script is a Link source folder ,Link to WORKSPACE_LOC\1-hi-parent\3-hi-springMvcWeb\WebRoot\WEB-INF\script
you and add or edit entity, dao, manager,controller ...etc.
there are all dynamic, when finished just ctrl+s or copy to your webapp's folder ,the tomcat where read and compile the source as class and add to spring context

script 是一个链接的文件夹，指向 WORKSPACE_LOC\1-hi-parent\3-hi-spring-Dynamic-mvc-demo\WebRoot\WEB-INF\script
在这里，你可以动态地增加修改entity, dao, manager,controller等等，
这些代码是动态的，当完成编辑，按ctrl+s 或者复制到到你的webapp 相应的文件夹下，tomcat 将会读取并编译相应的source,这些类在spring容器内可以找到

/**
 *@Author: niaoge(Zhengsheng Xia)
 *@Email 78493244@qq.com
 *@Date: 2015-6-16
 */