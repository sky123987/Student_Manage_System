USE grade_system;
CREATE TABLE IF NOT EXISTS permission (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    perm_code   VARCHAR(100) NOT NULL COMMENT '权限码，格式 模块:方法',
    perm_name   VARCHAR(100) NOT NULL COMMENT '中文名称',
    module_name VARCHAR(100) NOT NULL COMMENT '所属功能模块',
    UNIQUE KEY uk_perm_code (perm_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限字典';

-- 2. 角色-权限关联表
CREATE TABLE IF NOT EXISTS role_permission (
    id        INT PRIMARY KEY AUTO_INCREMENT,
    role      VARCHAR(20) NOT NULL COMMENT 'admin/teacher/student',
    perm_code VARCHAR(100) NOT NULL,
    UNIQUE KEY uk_role_perm (role, perm_code),
    KEY idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联';

-- 3. user 表补 avatar 列（MySQL 8 不支持 ADD COLUMN IF NOT EXISTS，用动态 SQL 判断）
SET @has_avatar = (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'avatar'
);
SET @ddl = IF(@has_avatar = 0,
    'ALTER TABLE `user` ADD COLUMN avatar VARCHAR(255) DEFAULT NULL COMMENT ''头像相对路径''',
    'SELECT ''avatar column already exists'' AS info');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4. 默认管理员账号由应用启动时自动创建（SystemInitListener），
--    密码 123456 经 MD5Util（盐 lut_grade_system）加密，这里不要手动 INSERT，
--    否则写入了错误的哈希会导致 admin 无法登录，且应用不会再修正已存在的账号。

SELECT '升级完成：permission / role_permission 已就绪，user.avatar 已补齐' AS result;