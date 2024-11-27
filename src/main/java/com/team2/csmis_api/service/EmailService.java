package com.team2.csmis_api.service;

import com.team2.csmis_api.entity.Lunch;
import com.team2.csmis_api.entity.Settings;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.entity.UserHasLunch;
import com.team2.csmis_api.repository.LunchRepository;
import com.team2.csmis_api.repository.SettingRepository;
import com.team2.csmis_api.repository.UserHasLunchRepository;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private UserHasLunchRepository userHasLunchRepository;

    @Autowired
    private LunchRepository lunchRepository;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private LocalTime reminderTime;

    @PostConstruct
    public void init() {
        // Fetch the reminder time from the database
        Settings settings = settingRepository.findById(1).orElse(null);
        if (settings != null) {
            reminderTime = settings.getLunchReminderTime();
            scheduleTaskAtReminderTime();
        }
    }

    private void scheduleTaskAtReminderTime() {
        if (reminderTime != null) {
            // Get the current time
            LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);

            // Calculate the delay between now and the reminder time
            long delayInSeconds = currentTime.until(reminderTime, ChronoUnit.SECONDS);

            // If the reminder time has already passed for today, schedule it for the same time tomorrow
            if (delayInSeconds < 0) {
                delayInSeconds += 24 * 60 * 60; // Add 24 hours to delay
            }

            // Schedule the task to run at the reminder time
            scheduler.scheduleAtFixedRate(this::sendLunchReminder, delayInSeconds, 24 * 60 * 60, TimeUnit.SECONDS); // Run every 24 hours
        }
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Async("emailTaskExecutor")
    public void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true); // `true` enables HTML

        emailSender.send(mimeMessage);
    }

    public void sendLunchReminder() {
        Settings settings = settingRepository.findById(1).orElse(null);

        if(settings != null) {
            LocalTime reminderTime = settings.getLunchReminderTime();
            LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);

            if(currentTime.equals(reminderTime)) {
                LocalDate currentDate = LocalDate.now();
                List<UserHasLunch> userHasLunchList = userHasLunchRepository.findByCurrentDate();
                Lunch lunch = lunchRepository.findLunchByCurrentDate();
                for(UserHasLunch userHasLunch: userHasLunchList) {
                    User user = userHasLunch.getUser();
                    if(user.getReceivedMail()) {
                        String subject = "Daily Reminder Notification";
                        String[] items = lunch.getMenu().split(",");
                        String body = "<p>Dear, " + user.getName() + "</p>" +
                                "<h3>ယနေ့အတွက်နေ့လည်စာဟင်းပွဲများ</h3>" +
                                "<ul>";
                        for(String item : items) {
                            body += "<li>" + item.trim() + "</li>";
                        }
                        body += "</ul>" +
                                "<p>We hope you enjoy your meal today!</p>" +
                                "<p>Best regards,<br>Admin Team</p>";

                        MimeMessage mimeMessage = emailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

                        try {
                            helper.setTo(user.getEmail());
                            helper.setSubject(subject);
                            helper.setText(body, true); // `true` enables HTML
                            emailSender.send(mimeMessage);
                        } catch (MessagingException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            }
        }
    }

    public void sendOTP(String email, String otp) {
        String subject = "Your OTP for Password Reset";
        String message = "Your OTP for password reset is: " + otp;
        sendSimpleMessage(email, subject, message);
    }
}