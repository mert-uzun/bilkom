package com.bilkom.mailreader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailMessage {
    private String subject;
    private String content;
    private String bloodType;
    private String phoneNumber;

    public MailMessage(String subject, String content) {
        this.subject = subject;
        this.content = content;
        setBloodType();
        setPhoneNumber();
    }

    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public String getBloodType() { if (bloodType == null) return ""; return bloodType; }
    public String getPhoneNumber() { if (phoneNumber == null) return ""; return phoneNumber; }

    public void setBloodType() {
        if (content == null || content.isEmpty()) {
            this.bloodType = null; 
            return;
        }
    
        String upper = content.toUpperCase();
    
        String[] patterns = {
            "(A|B|AB|O)\\s*RH\\s*\\(?([+-])\\)?",
            "\\b(A|B|AB|O)([+-])\\b",
            "\\b(A|B|AB|O)\\s*(POZITIF|NEGATIF)\\b",
            "KAN\\s*GRUBU\\s*[:\\-]?\\s*(A|B|AB|O)\\s*RH\\s*([+-])"
        };
    
        for (String regex : patterns) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(upper);
            if (matcher.find()) {
                String blood = matcher.group(1);
                String sign;
    
                if (matcher.groupCount() >= 2) {
                    sign = matcher.group(2);
                    if (sign.equals("POZITIF")) sign = "+";
                    else if (sign.equals("NEGATIF")) sign = "-";
                } else {
                    sign = matcher.group().contains("-") ? "-" : "+";
                }
    
                this.bloodType = blood + " Rh (" + sign + ")";
                return;
            }
        }
    
        this.bloodType = ""; 
    }
    
    public void setPhoneNumber() {
        if (content == null || content.isEmpty()) {
            this.phoneNumber = null;
            return;
        }
    
        String cleaned = content.replaceAll("\\s+", " "); 
    
        String[] patterns = {
            "\\+\\d{1,3}[\\s\\-]?(\\(\\d{3}\\)|\\d{3})[\\s\\-]?\\d{3}[\\s\\-]?\\d{2,4}[\\s\\-]?\\d{2,4}",
    
            "0\\s?5\\d{2}[\\s\\-]?\\d{3}[\\s\\-]?\\d{2}[\\s\\-]?\\d{2}",
    
            "0\\d{10}",
    
            "\\+1\\s?\\(\\d{3}\\)\\s?\\d{3}[\\-\\s]?\\d{4}"
        };
    
        for (String regex : patterns) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(cleaned);
            if (matcher.find()) {
                this.phoneNumber = matcher.group().trim();
                return;
            }
        }
    
        this.phoneNumber = ""; 
    }
}