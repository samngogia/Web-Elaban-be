package NgoGiaSam.Web_Elaban_be.controller;


import NgoGiaSam.Web_Elaban_be.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    @Autowired
    private ChatbotService chatService;

    @PostMapping
    public String chat(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        return chatService.askRasa(message);
    }
}
