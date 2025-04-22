-- Table for users
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT DEFAULT 1,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name Varchar(255) NOT NULL,
    bilkent_id VARCHAR(15) NOT NULL UNIQUE,
    user_role VARCHAR(20) NOT NULL,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    blood_type VARCHAR(5) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP DEFAULT created_at
);

-- Table for clubs
CREATE TABLE clubs (
    club_id INT PRIMARY KEY DEFAULT 1 AUTO_INCREMENT,
    club_name VARCHAR(255) NOT NULL UNIQUE,
    club_description TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    club_head BIGINT,
    FOREIGN KEY (club_head) REFERENCES users(user_id)
);

-- Table for club_executives
CREATE TABLE club_executives (
    executive_id BIGINT PRIMARY KEY,
    club_id INT,
    FOREIGN KEY (club_id) REFERENCES clubs(club_id),
    FOREIGN KEY (executive_id) REFERENCES users(user_id),
    position VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    UNIQUE (club_id, executive_id)    
);

-- Table for club_members
CREATE TABLE club_members (
    club_id INT,
    member_id BIGINT,
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


