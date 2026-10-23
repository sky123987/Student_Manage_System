<%--
  全站统一顶部导航。
  侧边栏那套（.sidebar + common.css）在没有样式表生效时会退化成一排蓝链接，
  所以把菜单直接放到 Bootstrap 的 navbar 里，样式由本地 css/common.css 统一控制。
  菜单项仍然来自 MenuService（按角色权限过滤），active 由 MenuItem.isActive() 决定。
--%>
<nav class="navbar navbar-expand-xl navbar-dark bg-dark shadow-sm">
  <div class="container-fluid">
    <span class="navbar-brand d-flex align-items-center gap-2 mb-0">
      <i class="bi bi-mortarboard-fill"></i><span class="fw-semibold">学生成绩管理系统</span>
    </span>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav"
            aria-controls="mainNav" aria-expanded="false" aria-label="切换导航">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="mainNav">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <c:forEach var="menu" items="${menuList}">
          <li class="nav-item">
            <a class="nav-link ${menu.active ? 'active fw-semibold' : ''}"
               href="${pageContext.request.contextPath}${menu.href}">
              <i class="bi ${menu.icon}"></i> ${menu.label}
            </a>
          </li>
        </c:forEach>
      </ul>
      <div class="dropdown">
        <button class="btn btn-outline-light btn-sm dropdown-toggle" type="button"
                data-bs-toggle="dropdown" aria-expanded="false">
          <i class="bi bi-person-circle"></i>
          <c:out value="${sessionScope.user.roleName}"/>
          <span class="text-white-50">·</span>
          <c:out value="${sessionScope.user.username}"/>
        </button>
        <ul class="dropdown-menu dropdown-menu-end shadow">
          <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user?method=toProfile">
            <i class="bi bi-person-lines-fill me-1"></i>个人资料</a></li>
          <li><a class="dropdown-item" href="${pageContext.request.contextPath}/views/change_password.jsp">
            <i class="bi bi-key me-1"></i>修改密码</a></li>
          <li><hr class="dropdown-divider"></li>
          <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/user?method=logout">
            <i class="bi bi-box-arrow-right me-1"></i>退出登录</a></li>
        </ul>
      </div>
    </div>
  </div>
</nav>
<style id="sms-core">
/* 关键样式兜底：即使浏览器缓存了旧的 common.css，导航和内容框架也能立即生效 */
body { background: #f1f5f9; color: #0f172a; }
.navbar.navbar-dark {
  background: linear-gradient(120deg, #0b1f3a 0%, #123a7a 55%, #1d4ed8 100%) !important;
  border-bottom: 3px solid #fbbf24;
  box-shadow: 0 6px 20px rgba(15, 23, 42, .18);
}
.navbar.navbar-dark .navbar-brand { color: #fff !important; font-weight: 700; }
.navbar.navbar-dark .nav-link { color: rgba(255, 255, 255, .82); border-radius: 8px; padding: .45rem .7rem; }
.navbar.navbar-dark .nav-link:hover,
.navbar.navbar-dark .nav-link.active { background: #fbbf24; color: #0b1f3a !important; }
.wrapper { width: 100%; max-width: 1320px; margin: 0 auto; padding: 28px 32px 48px; }
.content > h3, .content > h4 { border-left: 4px solid #1d4ed8; padding-left: 12px; }
body.p-4 { background: #f1f5f9; }
body.p-4 > .container {
  background: #fff; border-radius: 16px; padding: 28px 30px 32px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, .06); border-top: 4px solid #1d4ed8;
}
</style><script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js?v=20261023c"></script>