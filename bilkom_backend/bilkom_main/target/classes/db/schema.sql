-- Table for users
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name Varchar(255) NOT NULL,
    bilkent_id VARCHAR(15) NOT NULL UNIQUE,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    blood_type VARCHAR(5) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP DEFAULT created_at,
    user_role ENUM('USER', 'CLUB_HEAD', 'CLUB_EXECUTIVE', 'ADMIN') DEFAULT 'USER',
    email_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE,
    profile_visibility ENUM('PUBLIC', 'MEMBERS', 'PRIVATE') DEFAULT 'PUBLIC',
    avatar_path ENUM('AVATAR_1', 'AVATAR_2', 'AVATAR_3', 'AVATAR_4', 'AVATAR_5', 'AVATAR_6', 'AVATAR_7', 'AVATAR_8', 'AVATAR_9', 'AVATAR_10', 'AVATAR_11', 'AVATAR_12', 'AVATAR_13', 'AVATAR_14', 'AVATAR_15', 'AVATAR_16') DEFAULT 'AVATAR_1',
    verification_token VARCHAR(255)
);

-- Table for clubs
CREATE TABLE clubs (
    club_id INT PRIMARY KEY DEFAULT 1 AUTO_INCREMENT,
    club_name VARCHAR(255) NOT NULL UNIQUE,
    club_description TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    club_head BIGINT,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    FOREIGN KEY (club_head) REFERENCES users(user_id)
);

-- Table for club_executives
CREATE TABLE club_executives (
    executive_id BIGINT PRIMARY KEY,
    club_id INT,
    FOREIGN KEY (club_id) REFERENCES clubs(club_id),
    FOREIGN KEY (executive_id) REFERENCES users(user_id),
    position VARCHAR(255) NOT NULL,
    join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    leave_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    UNIQUE (club_id, executive_id)    
);

-- Table for club_members
CREATE TABLE club_members (
    club_id INT,
    member_id BIGINT,
    join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    leave_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (club_id) REFERENCES clubs(club_id),
    FOREIGN KEY (member_id) REFERENCES users(user_id),
    PRIMARY KEY (club_id, member_id)
);

-- Table for events
CREATE TABLE events (
    event_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_name VARCHAR(255) NOT NULL,
    event_description TEXT NOT NULL,
    creator_id BIGINT,
    is_club_event BOOLEAN DEFAULT FALSE, -- Indicates if the event is a club event or not
    club_id INT,
    max_participants INT NOT NULL,
    current_participants_number INT DEFAULT 0,
    event_location VARCHAR(255) NOT NULL,
    event_date DATE NOT NULL,
    event_status ENUM('upcoming', 'past') NOT NULL DEFAULT 'upcoming',
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (creator_id) REFERENCES users(user_id),
    FOREIGN KEY (club_id) REFERENCES clubs(club_id)
);

-- Table for tags
CREATE TABLE tags (
    tag_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT,
    user_id BIGINT,
    tag_name VARCHAR(255) NOT NULL UNIQUE,
    FOREIGN KEY (event_id) REFERENCES events(event_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Table for event participants
CREATE TABLE event_participants (
    event_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (event_id, user_id),
    FOREIGN KEY (event_id) REFERENCES events(event_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Tablo for emergency alerts
CREATE TABLE emergency_alerts  (
    alert_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    alert_type VARCHAR(255) NOT NULL DEFAULT "Blood Infusion",
    blood_type VARCHAR(5) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    alert_description TEXT NOT NULL,
    alert_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE 
);

-- Add index to event_participants for faster user and event lookups
ALTER TABLE event_participants ADD INDEX idx_event_participants_user (user_id);
ALTER TABLE event_participants ADD INDEX idx_event_participants_event (event_id);