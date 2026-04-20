package com.mastersdbis.mtsdreactive.DTO;

import com.mastersdbis.mtsdreactive.Entities.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private Integer id;
    private String message;
    private Integer senderId;
    private Integer receiverId;
    private String fileUrl;
    private String messageType;
    private Integer serviceId;

    public static MessageDTO fromMessage(Message m) {
        return new MessageDTO(
            m.getId(),
            m.getMessage(),
            m.getSenderId(),
            m.getReceiverId(),
            m.getFileUrl(),
            m.getMessageType(),
            m.getServiceId()
        );
    }

    public Message toMessage() {
        Message m = new Message();
        m.setSenderId(this.senderId);
        m.setReceiverId(this.receiverId);
        m.setMessage(this.message);
        m.setFileUrl(this.fileUrl);
        m.setMessageType(this.messageType);
        m.setServiceId(this.serviceId);
        return m;
    }
}