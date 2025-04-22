package com.bilkom.mailreader;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;

import java.time.Instant;
import java.util.Date;
import java.time.temporal.ChronoUnit;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    public List<MailMessage> fetchBloodMails() {
        List<MailMessage> result = new ArrayList<>();

        String email = "bilkomproje@gmail.com";
        String appPassword = "idps dgqu wvug ulhc";

        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.enable", "true");

        try {
            Session session = Session.getInstance(props, null);
            Store store = session.getStore();
            store.connect("imap.gmail.com", email, appPassword);

            Folder inbox = store.getFolder("[Gmail]/All Mail");
            inbox.open(Folder.READ_ONLY);

            int count = inbox.getMessageCount();
            int start = Math.max(1, count - 500); 
            Message[] messages = inbox.getMessages(start, count);

            Date cutoff = Date.from(Instant.now().minus(48, ChronoUnit.HOURS));

            for (Message message : messages) {
                Date sentDate = message.getSentDate();
                if (sentDate == null || sentDate.before(cutoff)) {
                    continue; // Skip if older than 48 hours
                }
            
                String subject = message.getSubject();
                if (subject != null && subject.contains("ACİL KAN İHTİYACI")) {
                    String content = getTextFromMessage(message);
                    result.add(new MailMessage(subject, content, sentDate));
                }
            }            

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            result.add(new MailMessage("ERROR", e.getMessage(), new Date()));
        }

        return result;
    }


    @Scheduled(fixedRate = 60000) 
    public void scheduledMailCheck() {
        List<MailMessage> newMails = fetchBloodMails();
        log.info("Checked mail at {} — found {} blood-related messages", java.time.LocalTime.now(), newMails.size());
    }

    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result.append(bodyPart.getContent());
                }
            }
            return result.toString().replaceAll("\r\n","");
        }
        return "";
    }
}