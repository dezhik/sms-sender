package ru.dezhik.sms.sender.test.smsru;

import org.junit.BeforeClass;
import ru.dezhik.sms.sender.SenderService;
import ru.dezhik.sms.sender.SenderServiceConfiguration;
import ru.dezhik.sms.sender.SenderServiceConfigurationBuilder;

import java.io.FileInputStream;
import java.io.IOException;

public abstract class SMSRuIntegrationTest {
    protected static SenderServiceConfiguration senderConfiguration;
    protected static SenderService senderService;

    @BeforeClass
    public static void before() throws IOException {
        senderConfiguration = SenderServiceConfigurationBuilder.create()
                .load(new FileInputStream("test.properties"))
                .build();
        senderService = new SenderService(senderConfiguration);
    }

}
