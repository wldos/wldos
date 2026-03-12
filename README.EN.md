<div align="center">
  <img alt="WLDOS" src="http://gitee.com/wldos/wldos/raw/master/modules/wldos-platform/src/main/resources/resources/store/logo.png" width="100" height="100">
  <h1>WLDOS Cloud Application Platform</h1>
  <p><strong>Build Enterprise Multi-Tenant SaaS in 5 Minutes</strong></p>
  <p>
    <a href="http://www.wldos.com">🎬 Live Demo</a> •
    <a href="#-quick-start">🚀 Quick Start</a> •
    <a href="#-community">💬 Community</a> •
    <a href="README.md">中文</a>
  </p>
  <p>
    <img src="https://img.shields.io/badge/WLDOS-V2.3.8.2-deepskyblue.svg" alt="version">
    <img src="https://img.shields.io/badge/SpringBoot-2.7-green.svg" alt="springboot">
    <img src="https://img.shields.io/badge/React-17-blue.svg" alt="react">
    <a href="https://gitee.com/wldos/wldos/stargazers"><img src="https://gitee.com/wldos/wldos/badge/star.svg?theme=dark" alt="star"></a>
  </p>
</div>

---

## ✨ Core Features

| Feature | Description |
|---------|-------------|
| 🏢 **Multi-Tenant** | One codebase serves multiple customers with complete data isolation |
| 🌐 **Multi-Domain** | Different domains display different content, independent brand operation |
| 🔌 **Hot-Pluggable Plugins** | Business modules plug and play, no restart required |
| 🔐 **Complete Permissions** | RBAC + Data permissions + Multi-level organization |
| 📱 **Full-Stack Fusion** | Spring Boot + React, Dynamic Monolithic Architecture |
| 💰 **Content Payment** | Built-in payment system, ready to use |

## 🚀 Quick Start

### Option 1: Maven (Recommended)

```bash
# 1. Clone the project
git clone https://gitee.com/wldos/wldos.git
cd wldos

# 2. Initialize database (MySQL 5.7+)
# Execute wldos-web/db/init.sql

# 3. Configure database
# Edit wldos-web/src/main/resources/application-dev.properties

# 4. Start backend
mvn clean install -pl com.wldos:wldos-web -am -DskipTests "-Dspring.profiles.active=dev"
mvn -pl com.wldos:wldos-web spring-boot:run "-Dspring.profiles.active=dev"

# 5. Start frontend
cd wldos-ui
npm install && npm start

# 6. Visit http://localhost:8000  Account: admin / admin
```

### Option 2: Desktop Installation (Windows)

Download the desktop installer, double-click to run. [Download](http://www.wldos.com)

## 📸 Screenshots

<details>
<summary>Click to expand</summary>

![System Management](https://gitee.com/wldos/wldos/raw/master/zone/dev/wldos.jpeg)
![Content Payment](https://gitee.com/wldos/wldos/raw/master/zone/dev/KPayCMS.jpeg)
![Dynamic Monolithic](https://gitee.com/wldos/wldos/raw/master/zone/dev/wldos2.0.jpeg)

</details>

## 💬 Community

| Channel | Info |
|---------|------|
| QQ Group | 830355512 |
| WeChat | Yuanxi World (id: yuanxiyuzhou) |
| Demo | [www.wldos.com](http://www.wldos.com) |

---

## 📖 Documentation

<details>
<summary>Function Description</summary>

### Function Description

WLDOS is a software family consisting of: development framework, support platform, and content payment system.

Outputs two projects:
- **WLDOS Cloud Application Support Platform** (Management)
- **WLDOS Content Payment System** (Business)

Version 2.0 introduces Dynamic Monolithic Architecture combining frontend-backend separation and microservices.

</details>

<details>
<summary>Technical Stack</summary>

### Technical Stack

| Category | Technology |
|----------|------------|
| Language | Java 8, React 17 |
| Framework | Spring Boot 2.7.18 |
| ORM | Spring Data JDBC 2.4.17 |
| Frontend | React 17, Ant Design Pro V4.5 |
| Middleware | Tomcat 9, Apache/Nginx |
| Auth | JWT, Stateless |

</details>

<details>
<summary>Module Introduction</summary>

### Module Introduction

```
wldos-sdk-api     SDK Contract Module
wldos-framework   Development Framework (Maven Central)
wldos-platform    Support Platform
wldos-kpaycms     Content Payment CMS
wldos-web         Project Entry Module
```

Maven dependency:
```xml
<dependency>
   <groupId>com.wldos</groupId>
   <artifactId>wldos-framework</artifactId>
   <version>${project.parent.version}</version>
</dependency>
```

</details>

<details>
<summary>Project Structure</summary>

### 2.0 Dynamic Monolithic Structure

```
wldos/
├── modules/
│   ├── wldos-kpaycms/          Content Payment CMS
│   ├── wldos-oauth2/           Social Login
│   ├── wldos-platform/         Support Platform
│   └── wldos-store/            App Store
├── wldos-framework/            Development Framework
├── wldos-plugins/              Plugin Module
│   ├── plugin-demo/            Full Stack Plugin Demo
│   └── wldos-plugin-demo/      Backend Plugin Demo
├── wldos-ui/                   Frontend
└── wldos-web/                  Entry Module
```

</details>

<details>
<summary>License</summary>

### License

**Dual-License Model**:

#### Open Source (Apache 2.0)
- ✅ Source code open, commercial use allowed
- ✅ Same as commercial version
- ✅ Full functionality

#### Commercial License
- ⚠️ Non-pure open source parts in wldos-platform
- ⚠️ Commercial support available

**Contact**: 306991142@qq.com

See [LICENSE](LICENSE) and [term.md](term.md) for details.

### Version Notes
- v2.3.8.2+: Apache 2.0, no GPL dependencies
- v2.3.7 and below: Contains GPL dependencies

</details>

---

**If helpful, please give us a ⭐ Star!**
