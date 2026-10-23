# 学生成绩管理系统（Student Manage System）

基于 **Java Servlet + JSP + JDBC + MySQL + Tomcat** 实现的学生成绩管理系统课程设计（JavaWeb 编程技术），采用 MVC 分层与 RBAC 动态权限，支持管理员、教师、学生三种角色的完整业务闭环。

## 功能概览

| 角色 | 主要功能 |
| --- | --- |
| 管理员 | 系统概览、学生/教师/班级/课程管理、选课名单、成绩统计（按班级 / 全校）、成绩 Excel 导出、改分审批、补考审批、账号管理、权限管理、学生与教师名单打印 |
| 教师 | 我的首页、我的课程、上课名单、成绩录入与修改（支持平时成绩分项 + 期末比例模板）、成绩统计与导出、改分申请、补考成绩录入 |
| 学生 | 我的首页、个人信息、选课中心、我的课表、我的成绩、成绩单 Excel 导出、补考申请 |
| 公共 | 登录 / 注册 / 退出、修改密码、头像上传、Session 登录态、Filter 权限拦截、动态侧边栏菜单 |

## 技术栈

- **后端**：Java 25（pom 中 maven-compiler-plugin source/target=25；若使用 JDK 17 编译，请改为 17）、Jakarta Servlet 6.1、JSP、JSTL 3.0
- **数据库**：MySQL 8.x，Druid 连接池，`mysql-connector-j` 8.0.33
- **构建**：Maven（WAR 打包，finalName = `myapp`）
- **容器**：Tomcat 11（Jakarta EE 10，Servlet 6）
- **前端**：Bootstrap 5.3、Bootstrap Icons、jQuery（均已本地化到 `webapp/css`、`webapp/js`，离线可用）、原生 JavaScript，统一视觉主题（`css/common.css`、`css/auth.css`）
- **报表**：Apache POI 5.2.3 导出 `.xlsx`

## 环境要求

| 组件 | 版本建议 |
| --- | --- |
| JDK | 25（pom 中 maven-compiler-plugin 为 source/target=25，已在 JDK 25 下编译通过；用低版本 JDK 需同步下调） |
| Maven | 3.8+ |
| MySQL | 8.0+ |
| Tomcat | 11.x（必须是 Jakarta 命名空间版本，不能使用 Tomcat 9） |

## 目录结构

```
Student_Manage_System/
├── pom.xml
├── DBsql_upgrade_v2.sql              # 权限体系 + 头像列升级脚本（可重复执行）
├── README.md
└── src/main/
    ├── java/com/lut/grade/
    │   ├── controller/               # Servlet 控制器（UserServlet、Admin*、Teacher*、Student*、BaseServlet）
    │   ├── service/                  # 业务逻辑（AccountService、GradeService、PermissionService、MenuService…）
    │   ├── dao/                      # JDBC 数据访问（UserDao、StudentDao、GradeDao…）
    │   ├── model/                    # 实体类（User、Student、Teacher、Course、GradeDetail…）
    │   ├── filter/                   # CharacterEncodingFilter、LoginFilter
    │   └── util/                     # DBUtil、MD5Util、ExcelWriter、PasswordPolicy、SystemInitListener
    ├── resources/druid.properties    # 数据库连接配置
    └── webapp/
        ├── WEB-INF/web.xml           # Filter 映射、JSP UTF-8 配置
        ├── WEB-INF/jspf/             # navbar.jsp、msg.jsp 公共片段
        ├── css/                      # Bootstrap 5.3、Bootstrap Icons（含 fonts/）、common.css 与 auth.css
        ├── js/                       # Bootstrap bundle、jQuery、auth.js（密码显示切换）
        └── views/                    # 页面：login、register、change_password、admin/、teacher/、student/
```

## 快速开始

### 1. 准备数据库

1. 启动 MySQL，创建数据库（字符集必须是 `utf8mb4`）：

   ```sql
   CREATE DATABASE IF NOT EXISTS grade_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
   ```

2. 导入基础业务表。仓库中只包含升级脚本 `DBsql_upgrade_v2.sql`（权限表、`user.avatar` 列），**基础业务表需要事先存在**。业务涉及的表见下方「数据模型」一节；如果本机已有旧的 `grade_system` 库（13 张表齐全），跳过这一步直接执行升级脚本即可。

3. 执行升级脚本（可选，应用启动时 `SystemInitListener` 会自动完成同样的初始化）：

   ```bash
   mysql -u root -p grade_system < DBsql_upgrade_v2.sql
   ```

4. 修改 `src/main/resources/druid.properties` 中的连接信息（默认是 `root/123456`）：

   ```properties
   driver=com.mysql.cj.jdbc.Driver
   url=jdbc:mysql://localhost:3306/grade_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
   username=root
   password=123456
   ```

### 2. 构建

```bash
mvn clean package
```

产物：`target/myapp.war`。

### 3. 部署到 Tomcat

把 `target/myapp.war` 复制到 Tomcat 的 `webapps/` 目录，启动 Tomcat。访问：

```
http://localhost:8080/myapp/
```

未登录时会被 `LoginFilter` 自动重定向到登录页；也可直接访问 `http://localhost:8080/myapp/views/login.jsp`。

### 4. 初始账号

| 账号 | 密码 | 角色 | 说明 |
| --- | --- | --- | --- |
| `admin` | `123456` | 管理员 | 首次启动时由 `SystemInitListener` 自动创建（MD5 加盐 `lut_grade_system`） |

教师、学生账号可以通过登录页的「注册」自助创建，也可以由管理员在「账号管理 / 教师管理 / 学生管理」中创建，管理员创建的账号默认密码为 `123456`。

> ⚠️ 默认密码 `123456` 仅用于首次登录，请登录后立即在「修改密码」中改成符合规则的新密码。

## 密码规则（本次修改）

### 需求

注册密码必须 **不少于 6 位**，且 **同时包含字母和数字**；不符合规则时 **弹出提示**。

### 规则定义

| 输入 | 结果 | 提示 |
| --- | --- | --- |
| 空 | ❌ | 密码不能为空 |
| `12345` | ❌ | 密码长度不能少于 6 位 |
| `abc12` | ❌ | 密码长度不能少于 6 位 |
| `123456` | ❌ | 密码必须同时包含字母和数字，例如 abc123 |
| `abcdef` | ❌ | 密码必须同时包含字母和数字，例如 abc123 |
| `abc123` / `A1b2c3` | ✅ | — |

### 实现位置

规则集中在一个类里，前后端共用同一套文案：

1. **后端（唯一把关点）**：`src/main/java/com/lut/grade/util/PasswordPolicy.java`
   - `PasswordPolicy.check(String password)`：返回 `null` 表示通过，否则返回中文失败原因。
   - `PasswordPolicy.isValid(String password)`：布尔版本。
   - 常量 `MIN_LENGTH = 6`、`RULE_TEXT`（页面提示文案）。

2. **注册**：`UserServlet.register()`
   - 校验顺序：非空 → 两次密码一致 → `PasswordPolicy.check(password)` → 创建账号。
   - 失败时 `backToRegister(...)` 把提示放回 `register.jsp` 的 alert 区块。

3. **修改密码**：`UserServlet.changePassword()`
   - 同样调用 `PasswordPolicy.check(newPwd)`，失败提示前缀「新」，例如「新密码必须同时包含字母和数字，例如 abc123」。
   - 与注册共用规则，避免出现「注册时严格、改密码时随便改回弱密码」的绕过路径。

4. **前端弹窗**：
   - `views/register.jsp`：表单 `onsubmit="return validateRegisterForm()"`，用 `alert()` 依次提示「长度不足」「未同时包含字母和数字」「两次密码不一致」。
   - `views/change_password.jsp`：表单 `onsubmit="return validateNewPassword()"`，规则相同。
   - 页面提示文案与 `PasswordPolicy.RULE_TEXT` 保持一致；原先的 `minlength="6"` 已移除，改由 JavaScript 的 `alert()` 给出更明确的提示，同时保留 `required` 作为空值兜底。

### 设计说明（为什么这样改）

- **规则不能塞进 `AccountService.create(...)`**：管理员创建账号、重置密码的默认密码是 `123456`，本身不满足「字母 + 数字」。如果把规则写进账号创建方法，管理员建号会直接失败。因此规则只作用于「用户自己设置密码」的两个入口（注册、修改密码）。
- **前端只是体验层**：`required` / `alert()` 都能被绕过，真正的校验在 `PasswordPolicy.check(...)`，服务端永远再校验一次。
- **文案一致**：前端 `alert` 与服务端返回的提示逐字一致，避免学生看到两套说法。

### 验证方式

1. 注册页输入 `12345` → 应弹出「密码长度不能少于 6 位」，且不提交。
2. 注册页输入 `123456` 或 `abcdef` → 应弹出「密码必须同时包含字母和数字，例如 abc123」。
3. 注册页输入两次不一致的密码 → 应弹出「两次输入的密码不一致」。
4. 输入 `abc123` → 注册成功，跳转登录页并提示注册成功。
5. 直接构造请求绕过前端（如 curl POST `/user?method=register`）提交弱密码 → 服务端仍返回失败提示，页面显示 alert 区块。

## 数据模型

系统默认库名 `grade_system`，共 13 张表：

| 表名 | 用途 | 主要字段 |
| --- | --- | --- |
| `user` | 登录账号 | id、username、password(MD5)、role、avatar |
| `student` | 学生档案 | user_id、student_number、name、class_id、gender、phone、email |
| `teacher` | 教师档案 | user_id、teacher_number、name、title |
| `class` | 班级 | class_name、grade_year |
| `course` | 课程 | course_name、credit、teacher_id、course_hours、course_type |
| `course_selection` | 选课记录 | student_id、course_id |
| `grade` | 成绩 | student_id、course_id、daily_score_json、final_exam_score、total_score、gpa、status |
| `course_grade_template` | 课程成绩模板 | course_id、daily_ratio、final_ratio、allow_makeup |
| `template_item` | 平时成绩分项 | template_id、item_name、item_ratio |
| `makeup_apply` | 补考申请 | student_id、course_id、status、makeup_score |
| `grade_modify_apply` | 改分申请 | course_id、teacher_id、reason、status |
| `permission` | 权限字典 | perm_code（模块:方法）、perm_name、module_name |
| `role_permission` | 角色-权限关联 | role、perm_code |

## 权限与请求分发设计

- **统一入口**：所有控制器继承 `BaseServlet`，请求通过 `?method=xxx` 反射分发到同名 public 方法；`PUBLIC_METHODS`（login / register / doLogin / doRegister）允许匿名。
- **权限码**：`模块名:方法名`，例如 `/admin/student` 的模块码为 `admin.student`，`list` 方法对应 `admin.student:list`。
- **两层拦截**：
  1. `LoginFilter`（`/*`）：未登录跳登录页；粗粒度限制 `/admin/`、`/teacher/`、`/student/` 角色区域。
  2. `BaseServlet` + `PermissionService`：按数据库 `role_permission` 表逐一校验权限码，无权限返回 403。
- **动态菜单**：`MenuService.menusFor(role, currentCode)` 只渲染当前角色有权限的菜单项。
- **自动初始化**：`SystemInitListener` 在应用启动时建权限表、写入默认权限、创建 `admin` 账号；权限体系初始化失败时会退回内置默认权限，保证系统仍可用。

## 业务规则

- **绩点**：`(总评成绩 - 50) / 10`，低于 0 按 0 计；总评 < 60 不获得学分，也不参与平均绩点加权。
- **等级**：≥90 优、≥80 良、≥70 中、≥60 及格、<60 不及格。
- **成绩构成**：课程可配置平时/期末比例（`course_grade_template`），平时成绩可按 `template_item` 分项录入，期末录入后自动合算总评。
- **补考**：学生申请 → 教师录入补考成绩 → 管理员审批。
- **改分**：教师提交改分申请（附原因）→ 管理员审批后生效。
- **报表**：学生成绩单、班级成绩明细、全校班级统计均可导出 Excel；学生/教师名单支持打印视图。

## 常见问题

**Q：启动后访问 404 / 空白？**
确认部署的是 Tomcat 11，且访问路径带上下文 `/myapp`（WAR 的 finalName 决定上下文名）。

**Q：数据库连接失败？**
检查 MySQL 是否启动、`grade_system` 是否已创建、`druid.properties` 的账号密码是否正确；若报时区错误，确认 URL 带 `serverTimezone=Asia/Shanghai`。

**Q：页面中文乱码？**
项目已配置 `CharacterEncodingFilter` 与 JSP UTF-8；若修改过文件，请确保以 UTF-8（无 BOM）保存。

**Q：登录提示「账号或密码错误」？**
密码使用 MD5 加盐 `lut_grade_system` 存储，不能手动往 `user` 表插入明文密码；请通过注册或管理员建号。`admin` 由程序自动创建，不要手动 INSERT。

**Q：注册/改密码时提示不符合规则？**
新规则要求至少 6 位且同时包含字母和数字，例如 `abc123`、`A1b2c3`；纯数字（如 `123456`）和纯字母都不允许。

## 界面与视觉风格

前端以「校园教务系统」为基调，深蓝（`#1d4ed8`）为主色、金色（`#fbbf24`）为点缀，所有静态资源本地化，断网也能正常显示。

- **认证页统一**：登录、注册、修改密码共用 `css/auth.css`，采用深蓝渐变 + 网格纹理背景、金色校徽、白色圆角卡片与图标输入框；三个页面布局、间距、按钮样式完全一致。
- **修改密码与登录注册同步**：`change_password.jsp` 与登录/注册使用同一套 `auth-shell` / `auth-card` / `auth-input` 结构与 `js/auth.js`（密码显示/隐藏）。
- **全站导航**：`common.css` 将 navbar 改为深蓝渐变，当前菜单金色胶囊高亮；导航使用 `navbar-expand-xl`，中等屏幕自动折叠，避免菜单换行。
- **内容页**：统一 `wrapper + content` 框架、标题左侧强调色竖条、卡片式表格（深蓝表头、悬停高亮、圆角阴影）。
- **角色区分**：`admin.css` / `teacher.css` / `student.css` 分别定义角色主题（管理员蓝、教师墨绿、学生青蓝），标题竖条、表头、筛选胶囊随角色变色；管理端新增/编辑页顶部带角色色条。
- **管理页**：以 `body class="p-4"` 的增删改查页面自动获得居中内容框、卡片化表格；新增/编辑表单页为白色圆角卡片，分组小标题带强调色竖条。
## 本次改动记录

| 文件 | 改动 |
| --- | --- |
| `src/main/java/com/lut/grade/util/PasswordPolicy.java` | 新增：密码规则（≥6 位 + 字母数字混合）唯一实现 |
| `src/main/java/com/lut/grade/controller/UserServlet.java` | 注册、修改密码改为调用 `PasswordPolicy.check(...)`，移除原来的 `length() < 6` 判断 |
| `src/main/webapp/views/register.jsp` | 移除 `minlength`，新增 `validateRegisterForm()` 弹窗校验与规则提示文案 |
| `src/main/webapp/views/change_password.jsp` | 移除 `minlength`，新增 `validateNewPassword()` 弹窗校验与规则提示文案 |
| `pom.xml` | 固定 `maven-war-plugin` 3.4.0，修复旧版 2.2 在 JDK 17+ 下 `mvn package` 报 `Cannot access defaults field of Properties` |
| `src/main/java/com/lut/grade/filter/CharacterEncodingFilter.java` | 不再对静态资源强制 `text/html`，修复浏览器因 MIME 类型不匹配而拒绝加载 CSS/JS 的问题 |
| `src/main/webapp/css/`、`src/main/webapp/js/` | 本地化 Bootstrap 5.3、Bootstrap Icons、jQuery，移除对 `cdn.jsdelivr.net` 的依赖 |
| `src/main/webapp/views/` 下 38 个 JSP、`WEB-INF/jspf/navbar.jsp` | CDN 引用统一改为 `${pageContext.request.contextPath}` 本地路径 |
| `src/main/webapp/css/auth.css`、`src/main/webapp/js/auth.js` | 新增：登录/注册/修改密码共用的认证页样式、密码显示切换脚本 |
| `src/main/webapp/css/common.css` | 全站组件统一：渐变导航、内容区框架、表格卡片、表单页白卡、筛选栏、页签、弹窗、彩色统计卡、按钮与复选框细节 |
| `src/main/webapp/css/admin.css`、`teacher.css`、`student.css` | 角色主题：标题竖条、表头配色（管理员深蓝 / 教师墨绿 / 学生青蓝）、筛选胶囊高亮；管理端表单卡加顶部色条 |
| `src/main/webapp/views/admin/` 下 21 个 JSP | 引入 `admin.css`，管理页面应用管理员主题 |
| `src/main/webapp/views/login.jsp`、`register.jsp`、`change_password.jsp` | 统一为「品牌区 + 白卡 + 图标输入框」，保留原字段、id 与弹窗校验逻辑 |
| `WEB-INF/jspf/navbar.jsp` | 导航改为 `navbar-expand-xl`，避免中等屏幕菜单换行 |
| `src/main/webapp/views/admin/index.jsp` | 提示卡头部改用主题渐变样式 |
| `README.md` | 新增项目说明、部署步骤、密码规则与界面风格文档 |