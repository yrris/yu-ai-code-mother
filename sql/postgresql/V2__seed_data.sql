-- =====================================================
-- PostgreSQL Seed Data
-- Sample data for development and testing
-- =====================================================

-- =====================================================
-- Sample Users
-- =====================================================
-- Password: "12345678" (hashed with BCrypt)
INSERT INTO "user" (user_account, user_password, user_name, user_avatar, user_profile, user_role) VALUES
('admin', '$2a$10$rDdJU0vQZjHE4KN6V0N9JeYvN5xJ5x5vQ5N5vN5vN5vN5vN5vN5vN5', 'Administrator', 'https://via.placeholder.com/150', 'System Administrator', 'admin'),
('demo_user', '$2a$10$rDdJU0vQZjHE4KN6V0N9JeYvN5xJ5x5vQ5N5vN5vN5vN5vN5vN5vN5', 'Demo User', 'https://via.placeholder.com/150', 'Demo account for testing', 'user'),
('john_doe', '$2a$10$rDdJU0vQZjHE4KN6V0N9JeYvN5xJ5x5vQ5N5vN5vN5vN5vN5vN5vN5', 'John Doe', 'https://via.placeholder.com/150', 'Software Developer from Sydney', 'user')
ON CONFLICT (user_account) DO NOTHING;

-- =====================================================
-- Sample Applications
-- =====================================================
INSERT INTO app (app_name, cover, init_prompt, code_gen_type, deploy_key, priority, user_id) VALUES
(
    'Todo List App',
    'https://via.placeholder.com/400x300',
    'Create a simple todo list application with add, delete, and mark as complete features. Use modern styling.',
    'vue_project',
    'todo-app-demo-001',
    10,
    (SELECT id FROM "user" WHERE user_account = 'demo_user' LIMIT 1)
),
(
    'Landing Page',
    'https://via.placeholder.com/400x300',
    'Create a modern landing page for a SaaS product with hero section, features, pricing, and contact form.',
    'html',
    'landing-page-demo-001',
    8,
    (SELECT id FROM "user" WHERE user_account = 'demo_user' LIMIT 1)
),
(
    'Portfolio Website',
    'https://via.placeholder.com/400x300',
    'Create a personal portfolio website for a web developer. Include sections for about, skills, projects, and contact.',
    'multi_file',
    'portfolio-demo-001',
    5,
    (SELECT id FROM "user" WHERE user_account = 'john_doe' LIMIT 1)
)
ON CONFLICT (deploy_key) DO NOTHING;

-- =====================================================
-- Sample Chat History
-- =====================================================
INSERT INTO chat_history (message, message_type, app_id, user_id) VALUES
(
    'Create a simple todo list application with add, delete, and mark as complete features. Use modern styling.',
    'user',
    (SELECT id FROM app WHERE deploy_key = 'todo-app-demo-001' LIMIT 1),
    (SELECT id FROM "user" WHERE user_account = 'demo_user' LIMIT 1)
),
(
    'I''ve created a Vue 3 todo list application with the following features:
- Add new tasks
- Mark tasks as complete
- Delete tasks
- Modern, responsive design
The application is now ready to use!',
    'ai',
    (SELECT id FROM app WHERE deploy_key = 'todo-app-demo-001' LIMIT 1),
    (SELECT id FROM "user" WHERE user_account = 'demo_user' LIMIT 1)
),
(
    'Can you make the design more colorful?',
    'user',
    (SELECT id FROM app WHERE deploy_key = 'todo-app-demo-001' LIMIT 1),
    (SELECT id FROM "user" WHERE user_account = 'demo_user' LIMIT 1)
),
(
    'I''ve updated the design with vibrant colors:
- Bright header with gradient
- Colorful task items
- Animated hover effects
The application now has a more lively appearance!',
    'ai',
    (SELECT id FROM app WHERE deploy_key = 'todo-app-demo-001' LIMIT 1),
    (SELECT id FROM "user" WHERE user_account = 'demo_user' LIMIT 1)
);

-- =====================================================
-- Verify data
-- =====================================================
-- SELECT 'Users' as table_name, COUNT(*) as count FROM "user" UNION ALL
-- SELECT 'Apps' as table_name, COUNT(*) as count FROM app UNION ALL
-- SELECT 'Chat History' as table_name, COUNT(*) as count FROM chat_history;
