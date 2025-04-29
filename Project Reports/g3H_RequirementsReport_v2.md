# CS102 – Spring 2024/2025

**Instructor:** Uğur Güdükbay  
**Section No:** 3  
**Assistant:** Ebrar Bozkurt  

---

# ~ Bilkom ~  
**Group0 (3H)**

## Group Members

| Name          | Surname    |
| -------------- | ---------- |
| Salih Mert     | Uzun       |
| Sıla           | Bozkurt    |
| Elif           | Bozkurt    |
| Ali İhsan      | Sevindi    |
| Utku           | Kabukçu    |

---

# Requirements Report V2
**Due:** 30 March 2025

---

# 1. Introduction

University life is not solely defined by academic achievement; it is also a critical period for personal growth, social development, and mental well-being. As students transition to college, they encounter opportunities to form meaningful relationships, engage in extracurricular activities, and develop skills that extend far beyond the classroom. Research indicates that active participation in campus events and social activities significantly enhances students’ sense of belonging and overall mental health (Inside Higher Ed, 2023). Moreover, Lee et al. (2022) highlight that social isolation and loneliness are strongly linked to depressive symptoms and heightened academic stress among college students.

Despite the available opportunities, many students still struggle to navigate the complexities of campus life, leading to feelings of disconnection and isolation. The traditional ways of social engagement often fall short when addressing the needs of a diverse student body, particularly in an era where rapid social changes can promote feelings of alienation or loneliness. These challenges highlight the urgent need for innovative solutions to facilitate more effective campus social interactions.

Considering all of this, developing a dedicated platform designed to help students connect and socialize more effectively represents a timely and essential intervention. Such a platform could provide structured support for building social networks, enhancing the overall campus experience, and fostering a healthier, more vibrant, and active community.

Therefore, as Group0, we introduce **Bilkom**, a user-friendly platform designed to enhance the social and academic experience of Bilkent students. Bilkom connects students with like-minded peers for activities, hobbies, and events, fostering a vibrant campus life.

The platform also supports student clubs, encourages socialization to reduce academic stress, ensures swift emergency communication, and aims to facilitate carpooling and travel coordination.

---

# 2. Details

## 2.1 Activity Matching and Shared Interests

Bilkom simplifies finding partners for activities ranging from sports to collaborative projects. Users post activities with a participant quota, connect with organizers, and receive notifications when new participants join.

## 2.2 Enriching Campus Life Through Activities and Student Clubs

Student clubs can advertise their events with quotas and times, reaching wider audiences compared to traditional announcement methods like posters or WhatsApp groups.

## 2.3 Combating Depression and Managing Academic Stress Through Socialization

Bilkom helps manage academic stress and depression by enhancing students' sense of belonging and providing a supportive local network, backed by research (Cohen & Wills, 1985; Taylor, 2011).

## 2.4 Report System

Bilkom features a reporting system against harassment and unwanted behavior, with official investigations ensuring a safe environment.

## 2.5 Similar Applications and Tools

### 2.5.1 Application Logic

Inspired by Reddit’s posting system, merged with Bilkent students’ needs for activity coordination.

### 2.5.2 User Interface

Influenced by Reddit and X’s user-friendly UI designs.

## 2.6 Software Features

### 2.6.1 User Management

Only Bilkent students can register using verified `bilkent.edu.tr` emails. Users can manage their interests.

### 2.6.2 Activity and Event Management

Users can create, filter, and join activities/events with interest tags, quotas, and notifications.

## 2.7 Software Requirements

### 2.7.1 Backend System

- **Java with Spring Boot**
- **MySQL/PostgreSQL**
- **Hibernate (JPA)**
- **JWT-based Authentication**
- **Cloud Hosting (AWS, DigitalOcean, or Heroku)**

### 2.7.2 Frontend System

- **HTML, CSS, JavaScript**
- **Bootstrap or Tailwind CSS**
- *(Optional Future Upgrade: React.js)*

### 2.7.3 Security & Data Protection

- **JWT Authentication**
- **Data Encryption (HTTPS, AES)**
- **Role-Based Access Control**

### 2.7.4 Performance & Scalability

- **Caching (Redis or Ehcache)**
- **Efficient Database Indexing**
- **Automated Cloud Backups**

---

# 3. Possible Extensions

## 3.1 Emergency Alerts and Rapid Community Response

Instant notifications for emergencies like fires or health crises.

## 3.2 Weather Report and Livestream News Feed

Real-time weather forecasts and Bilkent news on the homepage.

## 3.3 Carpooling, Travel Coordination, and Campus Hitchhiking Culture

Safe and reliable ride-sharing exclusively for Bilkent students.

## 3.4 Similar Applications That Inspired Extensions

- **Widgets** for weather and news integration.
- **Martı TAG** for carpooling inspiration.

## 3.5 Software Features for Extensions

### 3.5.1 Emergency Situations

- Real-time alerts and blood donation requests.

### 3.5.2 Weather and Bilkent News Feed

- OpenWeatherMap API, RSS Feed Parsing, or JSoup for news.

### 3.5.3 Carpooling Service

- Google Maps API, live GPS, and in-app messaging.

## 3.6 Software Requirements for Extensions

### 3.6.1 Emergency Alerts & Blood Donation

- **WebSockets**
- **Backend API**
- **Targeted Alerts**

### 3.6.2 Weather & Bilkent News

- **OpenWeatherMap API**
- **RSS Feed Parsing / JSoup**

### 3.6.3 Carpooling & GPS Features

- **Google Maps API / OpenStreetMap API**
- **Real-Time Messaging (WebSockets or Firebase)**

---

# 4. Conclusion

Bilkom addresses the communication and organizational challenges at Bilkent University. It enhances activity matching, student club promotion, academic stress management, and fosters a vibrant, trustworthy campus community.

---

# References

- Cohen, S., & Wills, T. A. (1985). *Stress, social support, and the buffering hypothesis.* Psychological Bulletin, 98(2), 310–357. https://doi.org/10.1037/0033-2909.98.2.310
- Inside Higher Ed. (2023). *Survey: Getting more college students involved in campus life.* Retrieved from https://www.insidehighered.com/news/student-success/college-experience/2023/11/03/survey-getting-more-college-students-involved
- Lee, J. C., Chiang, J. J., & Almeida, D. M. (2022). *Loneliness and mental health: A daily diary study of young adults.* Social Psychological and Personality Science, 13(5), 948–957. Retrieved from https://pmc.ncbi.nlm.nih.gov/articles/PMC9636084/
- Taylor, S. E. (2011). *Social support: A review.* Oxford Handbook of Health Psychology, 189–214.

