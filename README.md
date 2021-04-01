# nacos spring cloud gateway

#### 介绍
centit-framework-cloud

#### 软件架构
软件架构说明

#### NACOS
nacos下载
官网地址：
https://nacos.io/zh-cn/docs/quick-start.html

下载地址：
https://github.com/alibaba/nacos/releases

启动脚本
bin目录下启动，1.4.1默认为集群默认，参考官网指南调整conf目录下cluster.conf的配置
测试环境请注意调整启动文件中单机模式或集群模式的默认内存分配

访问链接地址：
http://127.0.0.1:8848/nacos/index.html

默认账号密码
账号：nacos
密码：nacos
本工程已启用外部mysql数据库持久化配置，修改conf目录下application.properties中
### If use MySQL as datasource:
选项的属性设置

Nacos关闭服务
切换到bin目录，执行命令：sh shutdown.sh

本工程nacos-common包封装了nacos的服务发现和动态配置包
如果开发环境配置连接远程nacos服务使用application.yml文件请直接引入spring-cloud-starter-alibaba-nacos-discovery包，
不能引入spring-cloud-starter-alibaba-nacos-config包。
使用bootstrap.yml配置直接引入nacos-common包。


#### Gateway网关
gateway网关路由有两种配置方式:
1.在配置文件yml中配置
2.代码中注入RouteLocator的Bean

####predicates使用介绍  官方常用11种
https://docs.spring.io/spring-cloud-gateway/docs/2.2.5.RELEASE/reference/html/#gateway-request-predicates-factories

####Gateway网关集成security
web-demo页面登录测试

