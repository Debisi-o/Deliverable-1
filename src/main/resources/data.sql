INSERT INTO courses (title, instructor, description, category, difficulty, duration_hours, price, created_at)
SELECT 'Java Foundations', 'Chidi Okafor', 'Build a strong programming foundation with modern Java, object-oriented design, and practical exercises.', 'PROGRAMMING', 'BEGINNER', 24, 35000.00, CURRENT_TIMESTAMP - INTERVAL '6' DAY
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Java Foundations');

INSERT INTO courses (title, instructor, description, category, difficulty, duration_hours, price, created_at)
SELECT 'Data Storytelling with Power BI', 'Amina Bello', 'Turn raw business data into clear dashboards and compelling stories that support confident decisions.', 'DATA_SCIENCE', 'INTERMEDIATE', 18, 48000.00, CURRENT_TIMESTAMP - INTERVAL '5' DAY
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Data Storytelling with Power BI');

INSERT INTO courses (title, instructor, description, category, difficulty, duration_hours, price, created_at)
SELECT 'UX Design Essentials', 'Tolu Adeyemi', 'Learn user research, wireframing, prototyping, and usability testing through a complete product design project.', 'DESIGN', 'BEGINNER', 20, 30000.00, CURRENT_TIMESTAMP - INTERVAL '4' DAY
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'UX Design Essentials');

INSERT INTO courses (title, instructor, description, category, difficulty, duration_hours, price, created_at)
SELECT 'Digital Marketing Strategy', 'Ngozi Eze', 'Plan measurable campaigns across search, social media, email, and content channels for growing brands.', 'MARKETING', 'INTERMEDIATE', 16, 42000.00, CURRENT_TIMESTAMP - INTERVAL '3' DAY
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Digital Marketing Strategy');

INSERT INTO courses (title, instructor, description, category, difficulty, duration_hours, price, created_at)
SELECT 'Entrepreneurship and Business Models', 'Kunle Adebayo', 'Validate an idea, design a sustainable business model, and understand the numbers behind a new venture.', 'BUSINESS', 'BEGINNER', 14, 25000.00, CURRENT_TIMESTAMP - INTERVAL '2' DAY
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Entrepreneurship and Business Models');

INSERT INTO courses (title, instructor, description, category, difficulty, duration_hours, price, created_at)
SELECT 'Spring Boot API Engineering', 'Ifeanyi Nwosu', 'Design production-ready REST APIs using Spring Boot, validation, security fundamentals, testing, and JPA.', 'PROGRAMMING', 'ADVANCED', 32, 65000.00, CURRENT_TIMESTAMP - INTERVAL '1' DAY
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Spring Boot API Engineering');

INSERT INTO courses (title, instructor, description, category, difficulty, duration_hours, price, created_at)
SELECT 'Applied Machine Learning', 'Zainab Musa', 'Train, evaluate, and explain practical machine-learning models with Python and real-world datasets.', 'DATA_SCIENCE', 'ADVANCED', 40, 78000.00, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Applied Machine Learning');
