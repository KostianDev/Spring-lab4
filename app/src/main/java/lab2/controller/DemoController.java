package lab2.controller;

import lab2.service.NotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/demo")
public class DemoController {

    private final ApplicationContext applicationContext;
    private final NotificationService emailService;

    public DemoController(ApplicationContext applicationContext,
                         @Qualifier("emailNotificationService") NotificationService emailService) {
        this.applicationContext = applicationContext;
        this.emailService = emailService;
    }

    @GetMapping
    public String demo(Model model) {
        NotificationService email1 = emailService;
        NotificationService email2 = (NotificationService) applicationContext.getBean("emailNotificationService");
        
        NotificationService sms1 = (NotificationService) applicationContext.getBean("smsNotificationService");
        NotificationService sms2 = (NotificationService) applicationContext.getBean("smsNotificationService");
        
        model.addAttribute("email1", email1.getInfo());
        model.addAttribute("email2", email2.getInfo());
        model.addAttribute("emailSame", email1 == email2);
        
        model.addAttribute("sms1", sms1.getInfo());
        model.addAttribute("sms2", sms2.getInfo());
        model.addAttribute("smsSame", sms1 == sms2);
        
        return "demo";
    }
}
