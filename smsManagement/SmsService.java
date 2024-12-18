package smsManagement;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsService {
    // Replace these with your Twilio account details
    private static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    private static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");

    static {
        // Initialize Twilio SDK
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    // Method to send an SMS
    public static void sendSms(String to, String from, String body) {
        try {
            // Ensure the phone number starts with +92
            if (!to.startsWith("+92")) {
                to = to.replaceFirst("^0", "+92");
            }

            Message message = Message.creator(
                    new PhoneNumber(to),   // Recipient's phone number
                    new PhoneNumber(from), // Twilio phone number
                    body                   // SMS body
            ).create();

            System.out.println("Message sent successfully! SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
    }

}
