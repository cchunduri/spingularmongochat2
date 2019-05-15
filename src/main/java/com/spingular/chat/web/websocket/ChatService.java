package com.spingular.chat.web.websocket;

import com.spingular.chat.security.SecurityUtils;
import com.spingular.chat.service.ChatmessageService;
import com.spingular.chat.service.dto.ChatmessageDTO;
import com.spingular.chat.web.rest.ChatmessageResource;
import com.spingular.chat.web.rest.errors.BadRequestAlertException;
import com.spingular.chat.web.websocket.dto.MessageDTO;

import io.github.jhipster.web.util.HeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.spingular.chat.config.WebsocketConfiguration.IP_ADDRESS;

@Controller
public class ChatService implements ApplicationListener<SessionDisconnectEvent> {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SimpMessageSendingOperations messagingTemplate;
    
    private ChatmessageService chatmessageService;
    
    private ChatmessageResource chatmessageResource;
    
    private static final String ENTITY_NAME = "chatmessage";
    
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public ChatService(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @SubscribeMapping("/chat/public")
    public void subscribe(StompHeaderAccessor stompHeaderAccessor, Principal principal) {
        String login = SecurityUtils.getCurrentUserLogin().orElse("anonymoususer");
        String ipAddress = stompHeaderAccessor.getSessionAttributes().get(IP_ADDRESS).toString();
        log.debug("User {} subscribed to Chat from IP {}", login, ipAddress);
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setUserLogin("System");
        messageDTO.setTime(dateTimeFormatter.format(ZonedDateTime.now()));
        messageDTO.setMessage(login + " joined the chat");
        messagingTemplate.convertAndSend("/chat/public", messageDTO);
    }

    @MessageMapping("/chat")
    @SendTo("/chat/public")
    public MessageDTO sendChat(@Payload MessageDTO messageDTO, StompHeaderAccessor stompHeaderAccessor, Principal principal) throws URISyntaxException {
        messageDTO.setUserLogin(principal.getName());
    	System.out.println("! ENTERING:  createChatmessage ???????????????????????????????????????????!" + messageDTO);
    	ChatmessageDTO chatmessageDTO = new ChatmessageDTO();
    	chatmessageDTO.setMessage(messageDTO.getMessage());
    	chatmessageDTO.setTime(dateTimeFormatter.format(ZonedDateTime.now()));
    	chatmessageDTO.setUserLogin(messageDTO.getUserLogin());
    	System.out.println("! chatmessageDTO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + chatmessageDTO);
    	log.debug("REST request to save Chatmessage : {}", chatmessageDTO);
        createChatmessage(chatmessageDTO);
        return setupMessageDTO(messageDTO, stompHeaderAccessor, principal);
    }

	// TEST 3
	@PostMapping("/chatmessages")
	public ResponseEntity<ChatmessageDTO> createChatmessage(@RequestBody ChatmessageDTO chatmessageDTO) throws URISyntaxException {
		log.debug("REST request to save Chatmessage : {}", chatmessageDTO);
		if (chatmessageDTO.getId() != null) {
			throw new BadRequestAlertException("A new chatmessage cannot already have an ID", ENTITY_NAME, "idexists");
		}
		ChatmessageDTO result = chatmessageService.save(chatmessageDTO);
		System.out.println("! RESULT:  createChatmessage ???????????????????????????????????????????!" + result);
		return ResponseEntity
				.created(new URI("/api/chatmessages/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	} 
////    TEST 2
////    @MessageExceptionHandler()
//    public void createChatmessage(MessageDTO messageDTO) throws URISyntaxException {
//    	System.out.println("! ENTERING:  createChatmessage ???????????????????????????????????????????!" + messageDTO);
//    	ChatmessageDTO chatmessageDTO = new ChatmessageDTO();
//    	chatmessageDTO.setMessage(messageDTO.getMessage());
//    	chatmessageDTO.setTime(dateTimeFormatter.format(ZonedDateTime.now()));
//    	chatmessageDTO.setUserLogin(messageDTO.getUserLogin());
//    	System.out.println("! chatmessageDTO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + chatmessageDTO);
//    	log.debug("REST request to save Chatmessage : {}", chatmessageDTO);
//        if (chatmessageDTO.getId() != null) {
//            throw new BadRequestAlertException("A new chatmessage cannot already have an ID", ENTITY_NAME, "idexists");
//        }
////        chatmessageService.save(chatmessageDTO);
//        chatmessageResource.createChatmessage(chatmessageDTO);
//    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
//    @PostMapping("/chatmessages")
//    public ResponseEntity<ChatmessageDTO> createChatmessage(@RequestBody ChatmessageDTO chatmessageDTO) throws URISyntaxException {
//        log.debug("REST request to save Chatmessage : {}", chatmessageDTO);
//        if (chatmessageDTO.getId() != null) {
//            throw new BadRequestAlertException("A new chatmessage cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        ChatmessageDTO result = chatmessageService.save(chatmessageDTO);
//        return ResponseEntity.created(new URI("/api/chatmessages/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
    
//    TEST1  
//    public ResponseEntity<ChatmessageDTO> createChatmessage(@RequestBody MessageDTO messageDTO) throws URISyntaxException {
//    	ChatmessageDTO chatmessageDTO = new ChatmessageDTO();
//    	chatmessageDTO.setMessage(messageDTO.getMessage());
//    	chatmessageDTO.setTime(dateTimeFormatter.format(ZonedDateTime.now()));
//    	chatmessageDTO.setUserLogin(messageDTO.getUserLogin());
//    	System.out.println("! chatmessageDTO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + chatmessageDTO);
//    	log.debug("REST request to save Chatmessage : {}", chatmessageDTO);
//        if (chatmessageDTO.getId() != null) {
//            throw new BadRequestAlertException("A new chatmessage cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        ChatmessageDTO result = chatmessageService.save(chatmessageDTO);
//        return ResponseEntity.created(new URI("/api/chatmessages/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
    
//    public ChatmessageDTO saveChat(MessageDTO messageDTO) {
////      System.out.println("! messageDTO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + messageDTO);
//      ChatmessageDTO chatmessageDTO = new ChatmessageDTO();
//      chatmessageDTO.setMessage(messageDTO.getMessage());
//      chatmessageDTO.setTime(dateTimeFormatter.format(ZonedDateTime.now()));
//      chatmessageDTO.setUserLogin(messageDTO.getUserLogin());
//      System.out.println("! chatmessageDTO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + chatmessageDTO);
//      chatmessageService.save(chatmessageDTO);
//    }
    
    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        // when the user disconnects, send a message saying that hey are leaving
        log.info("{} disconnected from the chat websockets", event.getUser().getName());
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setUserLogin("System");
        messageDTO.setTime(dateTimeFormatter.format(ZonedDateTime.now()));
        messageDTO.setMessage(event.getUser().getName() + " left the chat");
        messagingTemplate.convertAndSend("/chat/public", messageDTO);
    }

    private MessageDTO setupMessageDTO (MessageDTO messageDTO, StompHeaderAccessor stompHeaderAccessor, Principal principal) {
        messageDTO.setTime(dateTimeFormatter.format(ZonedDateTime.now()));
        log.debug("Sending user chat data {}", messageDTO);
        return messageDTO;
    }
}
