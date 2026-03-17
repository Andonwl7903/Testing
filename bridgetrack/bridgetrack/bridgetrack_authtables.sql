-- BridgeTrack Auth & Permissions Tables
-- Please implement these tables one at a time rather than attempting to execute the whole file at once -Zach

CREATE TABLE `instructors` (
  `instructor_id`  INT          NOT NULL AUTO_INCREMENT,
  `first_name`     VARCHAR(100) NOT NULL,
  `last_name`      VARCHAR(100) NOT NULL,
  `email`          VARCHAR(200) NOT NULL,
  `phone`          VARCHAR(30)  DEFAULT NULL,
  `status`         VARCHAR(50)  NOT NULL DEFAULT 'active',  -- active | inactive
  `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`instructor_id`),
  UNIQUE KEY `uq_instructor_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `roles` (
  `role_id`     INT          NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(50)  NOT NULL,   -- student | instructor | admin
  `description` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uq_role_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `roles` (`name`, `description`) VALUES
  ('student',    'Can view own profile, enrollment history, and enroll in courses'),
  ('instructor', 'Can manage assigned sections, take attendance, and review past attendance records'),
  ('admin',      'Full access: manage students, instructors, courses, sections, terms, and all records');

CREATE TABLE `permissions` (
  `permission_id` INT          NOT NULL AUTO_INCREMENT,
  `name`          VARCHAR(100) NOT NULL,   -- snake_case action name
  `description`   VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `uq_permission_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `permissions` (`name`, `description`) VALUES
  -- Student permissions
  ('view_own_profile',        'View own student profile and personal information'),
  ('view_own_enrollments',    'View own enrollment history and current enrollments'),
  ('enroll_in_course',        'Enroll in or join waitlist for a course section'),
  ('view_own_attendance',     'View own attendance records'),

  -- Instructor permissions
  ('view_assigned_sections',  'View sections assigned to the instructor'),
  ('take_attendance',         'Record attendance for a session'),
  ('review_attendance',       'View previously recorded attendance for assigned sections'),
  ('view_enrolled_students',  'View roster of students enrolled in assigned sections'),

  -- Admin permissions
  ('manage_students',         'Create, edit, and deactivate student accounts'),
  ('manage_instructors',      'Create, edit, and deactivate instructor accounts'),
  ('manage_courses',          'Create, edit, and delete courses'),
  ('create_section',          'Create new course sections'),
  ('edit_section',            'Edit existing course sections'),
  ('delete_section',          'Delete or deactivate course sections'),
  ('manage_terms',            'Create and edit academic terms'),
  ('manage_enrollments',      'Override or manually manage student enrollments'),
  ('manage_attendance',       'Edit or delete any attendance record'),
  ('manage_users',            'Create, edit, deactivate, and assign roles to user accounts'),
  ('view_all_reports',        'Access all system-wide reports and data exports');

CREATE TABLE `role_permissions` (
  `id`            INT NOT NULL AUTO_INCREMENT,
  `role_id`       INT NOT NULL,
  `permission_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_role_permission` (`role_id`, `permission_id`),
  CONSTRAINT `fk_rp_role`       FOREIGN KEY (`role_id`)       REFERENCES `roles`       (`role_id`),
  CONSTRAINT `fk_rp_permission` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Assign student permissions (role_id = 1)
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT 1, `permission_id` FROM `permissions`
WHERE `name` IN (
  'view_own_profile',
  'view_own_enrollments',
  'enroll_in_course',
  'view_own_attendance'
);

-- Assign instructor permissions (role_id = 2)
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT 2, `permission_id` FROM `permissions`
WHERE `name` IN (
  'view_assigned_sections',
  'take_attendance',
  'review_attendance',
  'view_enrolled_students'
);

-- Assign admin permissions (role_id = 3) — all permissions
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT 3, `permission_id` FROM `permissions`;

CREATE TABLE `users` (
  `user_id`          INT          NOT NULL AUTO_INCREMENT,
  `username`         VARCHAR(100) NOT NULL,
  `email`            VARCHAR(200) NOT NULL,
  `password_hash`    VARCHAR(255) NOT NULL,   -- bcrypt hash; never store plaintext
  `entity_type`      VARCHAR(20)  NOT NULL,   -- 'student' | 'instructor' | 'admin'
  `entity_id`        INT          DEFAULT NULL, -- FK to students.student_id or instructors.instructor_id; NULL for standalone admins
  `is_active`        TINYINT(1)   NOT NULL DEFAULT 1,
  `last_login`       DATETIME     DEFAULT NULL,
  `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uq_user_username` (`username`),
  UNIQUE KEY `uq_user_email`    (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_roles` (
  `id`          INT      NOT NULL AUTO_INCREMENT,
  `user_id`     INT      NOT NULL,
  `role_id`     INT      NOT NULL,
  `assigned_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_role` (`user_id`, `role_id`),
  CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


