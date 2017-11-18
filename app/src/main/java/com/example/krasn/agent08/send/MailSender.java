package com.example.krasn.agent08.send;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailSender extends Authenticator {
    private static final String MAIL_HOST = "smtp.ukr.net";
    private static final String PORT = "2525";
    private String password;
    private Session session;
    private String user;

    private class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        ByteArrayDataSource(byte[] data, String type) {
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (this.type == null) {
                return "application/octet-stream";
            }
            return this.type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(this.data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }

    static {
        Security.addProvider(new JSSEProvider());
    }

    public MailSender(String user, String password) {
        this.user = user.toLowerCase();
        this.password = password;
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", MAIL_HOST);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.socketFactory.port", PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");
        this.session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.user, this.password);
    }

    public synchronized void sendMail(String subject, String body, String file, String sender, String recipients) throws Exception {
        MimeMessage message = new MimeMessage(this.session);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        message.setSubject(subject);
        message.setDataHandler(handler);
        try {
            message.setFrom(new InternetAddress(sender, sender));
            message.setReplyTo(new InternetAddress[]{new InternetAddress(sender, sender)});
        } catch (Exception e) {
            e.printStackTrace();
            message.setFrom(new InternetAddress(sender));
            message.setReplyTo(new InternetAddress[]{new InternetAddress(sender)});
        }
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler(new DataHandler(new FileDataSource(file)));
        messageBodyPart.setFileName(new File(file).getName());
        multipart.addBodyPart(messageBodyPart);
        message.setSentDate(new Date());
        message.setContent(multipart);
        if (recipients.indexOf(44) > 0) {
            message.setRecipients(RecipientType.TO, InternetAddress.parse(recipients));
        } else {
            message.setRecipient(RecipientType.TO, new InternetAddress(recipients));
        }
        Transport.send(message);
    }
}
