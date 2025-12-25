[中文版 README](README.md)
<p align="center">
 <img alt="WLDOS" src="http://gitee.com/wldos/wldos/raw/master/modules/wldos-platform/src/main/resources/resources/store/logo.png" width="120" height="120" >
</p>
<h3 align="center" style="margin:30px 0 30px;font-weight:bold;font-size:30px;">Cloud Application Support Platform - IoT Driver</h3>
<h5 align="center" style="margin:30px 0 30px;font-size:20px;">Develop IoT applications based on WLDOS, focusing on, opening up, and managing your ecosystem</h5>
<p align="center">
 <a href="http://wldos.com/" target="__blank"><img alt="WLDOS-V2.0" src="https://img.shields.io/badge/WLDOS-V2.0-deepskyblue.svg"></a>
 <a href="https://spring.io/projects/spring-boot#learn" target="__blank"><img alt="SpringBoot-2.7" src="https://img.shields.io/badge/SpringBoot-2.7-bluegreen.svg"></a>
 <a href="https://gitee.com/wldos/wldos/stargazers" target="__blank"><img alt="star" src="https://gitee.com/wldos/wldos/badge/star.svg?theme=dark"></a>
 <a href="https://gitee.com/wldos/wldos/members" target="__blank"><img alt="fork" src="https://gitee.com/wldos/wldos/badge/fork.svg?theme=dark"></a>
</p>    

### Project Introduction

WLDOS (pronounced: ['wou'da'si], World Operating System) is developed in Java and is dedicated to providing a foundational support framework and extension system for cloud, management, and terminal applications in IoT scenarios. It is suitable for localized tools, building websites, developing SaaS services, and developing business middle platforms.

Pain Points Solved: If you want to expand your business using online technology but are confused about how to implement and land it, you need a sustainable and expandable platform that does not require expensive infrastructure.

**Suitable for plugin tools, personal websites, enterprise websites, business middle platforms, R&D platforms, and comprehensive support systems. Ideal for teams starting in technology or looking to develop projects in the IoT field.**

If you find it valuable, please <a href="https://gitee.com/wldos/wldos" style="color: red">star us</a>!

### Project Features

1. **Easy to Use**:  
   Low cost, low threshold, only requires traditional infrastructure. Top architects have encapsulated steep technology libraries into traditional technology styles for developers, requiring minimal learning costs and ready to use.

   The framework only uses open-source libraries like Spring Boot, forming a sustainable framework called wldos-framework, on which the wldos support platform is built, allowing for self-research and growth from scratch.

2. **Flexible**:  
   Not blindly following industry trends, we advocate for independent research and development, shielding complex implementation details to lower technical barriers, and using familiar technical paradigms to achieve advanced functionality. Deeply customized interfaces support flexible expansion.

   Built on Spring Boot, you can customize your tech stack at any time, such as introducing MyBatis as an ORM framework or Flowable as your business process engine.

3. **Pragmatic**:  
   Independently developed and operated, combining theory with practice, R&D with operations, problem-oriented, and embracing the future pragmatically.

For more features, please read the code.

#### Technical Exchange

QQ Group: Group 1: 792559964 (Full) Group 2: 830355512   
Author's WeChat Public Account: Yuanxi World (id: yuanxiyuzhou)

![Author's WeChat Public Account: Yuanxi World](zone/dev/元悉世界公众号.jpg)

Demo Address: <a href="http://www.wldos.com/user/login?redirect=http%3A%2F%2Fwldos.com%2Fadmin%2Fres%2Fapp">Click to visit</a> (The official website is the demo environment)

### Function Description

WLDOS is a software family currently consisting of three main sections: development framework, support platform, and content payment. The framework and support platform are general support, and content payment is a best practice based on general support.

Outputs two projects: WLDOS Cloud Application Support Platform (Management End) and WLDOS Content Payment System (Business End). The application function structure is as follows:
![WLDOS Support Platform](https://gitee.com/wldos/wldos/raw/master/zone/dev/wldos.jpeg)
![WLDOS Content Payment](https://gitee.com/wldos/wldos/raw/master/zone/dev/KPayCMS.jpeg)

### Technical Description
**Language:** Java8, ReactJs17.  
**Framework:** Spring Boot 2.7.18 (Theoretically supports Java17).  
**ORM:** Spring Data JDBC 2.4.17, a friendly framework between Spring Data JPA and JDBC, combining the advantages of both. The connection pool uses Boot's built-in Hikari.  
**Frontend:** ReactJs17, AntD ProV4.5.  
**Middleware:** Tomcat9 (supports replacement with others), Apache2 or Nginx.  
**Auxiliary:** Built-in cache, built-in JWT, built-in file service.  
**Compatibility:** Backend JDK1.8, frontend IE11+, Google Chrome, Edge, etc.

**Application Architecture:** Frontend and backend separation, frontend ReactJs, backend Spring MVC (2.0 introduces WebFlux architecture version), JWT authentication, stateless, natively compatible with Spring Cloud, supports integration into Service Mesh.

**1.0 Core Functions:** System management (applications, resources, permissions, users, organizations, tenants, multi-domain, classification), content management, information publishing, content creation, content payment (online payment, online delivery).

**2.0 Planned Functions:** Service integration and open capabilities, plugin extension management capabilities, class Service Mesh Ad-hoc architecture support capabilities.

**3.0 Planned Functions:** Software factory, IoT support capabilities, intelligent modeling, and robot systems.

### Module Introduction

wldos-common: General module
wldos-framework: The WLDOS development framework has been published to the Maven Central Repository along with the common package. It is recommended to directly depend on it in your project to prevent divergence from the official version.
````
<dependencies>
   <dependency>
      <groupId>com.wldos</groupId>
      <artifactId>wldos-framework</artifactId> <!-- The framework by default depends on the common package, so there's no need to separately include the common package dependency -->
      <version>${project.parent.version}</version>
      <scope>compile</scope>
   </dependency>
</dependencies>
````
wldos-platform: The WLDOS support platform, where all system management functions are implemented in this module.      
wldos-kpaycms: Content payment management system developed on the support platform. If only the support platform is needed, you can run only the wldos-platform module and clear the CMS menu in the system management backend after startup;    
wldos-web: Project entry module, resource configuration, start from here to package WAR or executable JAR.

### Installation Tutorial

#### Start the Backend:
1. Download the backend project locally and open it with IDEA.
2. Install the MySQL database script to generate the database.  
   The database script is under wldos-web/db, MySQL 5.7, and the database username and password are in wldos-web/resources/application-dev.properties.

3. Set the file storage location.  
   Default: ${project-root}\\wldos-web\\Temp. To modify, find the corresponding option configuration in properties under wldos-platform.
4. Update the Maven library for the project. The server port number is 8080 by default.
5. Install the project:  
   mvn clean install -pl com.wldos:wldos-web -am -DskipTests  
   Start the project:    
   mvn -pl com.wldos:wldos-web spring-boot:run.

#### Start the Frontend:
1. Download the frontend project locally, open the project, and execute tyarn to install the dependency JS library.
2. Execute npm start to start the frontend project. Frontend access path: http://localhost:8000
3. Log in. Super administrator admin, password same as the name.

#### Deploy to Server:
    1. Install MySQL 5.7, Tomcat 9, Apache 2.4 on the server, configure a user with DBA permissions for the database, and update the configuration to platform/application*.properties;  
    2. Deploy the WAR package generated in step 5 above to Tomcat. You can configure a virtual host after decompression or directly configure the WAR package to the virtual host;  
    3. Start Tomcat, and the system will automatically complete the database creation and initialization;  
    4. Deploy the frontend dist to the server, configure the static resource server Apache or Nginx's virtual host and reverse proxy to Tomcat;  
    5. Access the domain name resolved by the server in the browser to open the homepage, indicating successful installation.
    6. A local installation version will be launched later, supporting local installation and deployment on Windows systems.

### Open Source License and Commercial License

The WLDOS platform has applied for software copyright, and the source code and software have been open-sourced and can be used commercially (no need for major modifications ^0^), consistent with the commercial version code.   
Modules that have not been open-sourced follow internal agreements, what you see is what you get, and do not affect personal use. Distribution requires commercial authorization.
Commercial cooperation: 306991142@qq.com.

Official website: http://gitee.com/wldos/ or 306991142@qq.com

*Appendix:*
#### Project Backend Structure (subject to actual conditions)

#### 2.0 Dynamic Monolithic Project Structure
````
wldos root directory
├── modules---------------------------------Multi-module sub-projects
│   ├── wldos-kpaycms-----------------------Content Payment CMS System
│   │   ├── pom.xml
│   │   └── src
│   ├── wldos-oauth2------------------------Social Login (Not Open)
│   │   ├── pom.xml
│   │   └── src
│   ├── wldos-platform----------------------WLDOS Support Platform
│   │   ├── pom.xml
│   │   └── src
│   └── wldos-store-------------------------Application Store (Not Open)
│       ├── pom.xml
│       └── src
├── wldos-common----------------------------General Package
│   ├── pom.xml
│   └── src
├── wldos-framework-------------------------WLDOS Development Framework
│   ├── pom.xml
│   └── src
├── wldos-installer-------------------------Desktop Version (Not Open)
│   ├── pom.xml
│   ├── src
├── wldos-plugins---------------------------Plugin Module
│   ├── plugin-config-template.yml----------Plugin Configuration Template
│   ├── plugin-demo-------------------------Full Stack Plugin Demo
│   │   ├── CHANGELOG.md
│   │   ├── README.md
│   │   ├── build-ui.bat
│   │   ├── build-ui.sh
│   │   ├── plugin.yml-----------------------Plugin Configuration
│   │   ├── pom.xml
│   │   └── src------------------------------Plugin Source Code (Backend + Frontend)
│   ├── pom.xml
│   └── wldos-plugin-demo-------------------Pure Backend Plugin Demo
│       ├── plugin.properties
│       ├── plugin.yml
│       ├── pom.xml
│       └── src
├── wldos-ui--------------------------------Frontend (Dynamic Monolithic)
│   ├── config
│   ├── node--------------------------------Built-in Node (Development Dependency)
│   ├── package.json
│   ├── src
├── wldos-web-------------------------------Project Web Entry Module
│   ├── plugins-----------------------------Built-in Plugin Directory
│   │   ├── plugin-demo---------------------Installed Plugin Directory (Full Stack Plugin Demo)
│   │   ├── plugin-demo.zip-----------------Full Stack Plugin Demo Installation Package
│   │   ├── wldos-plugin-demo---------------Backend Plugin Demo Installation Directory
│   │   └── wldos-plugin-demo.zip-----------Backend Plugin Demo Installation Package
│   ├── pom.xml
│   ├── src---------------------------------Source Code Directory
│   └── store-------------------------------Attachment Directory (Local Storage Service)
│       ├── 2025----------------------------Random Attachment File Directory
│       └── plugins-------------------------Built-in Plugin Directory (Plugin Market)
└── Development Log.txt

````

[中文版README](README.md)