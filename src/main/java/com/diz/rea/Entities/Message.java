package com.diz.rea.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("messages")
@Getter
@Setter
@NoArgsConstructor
public class Message extends AbstractEntity {

    @Id
    private Integer id;

    @Column("mesaj")
    private String message;

    @Column("sender_user_id")
    private Integer senderId;

    @Column("receiver_user_id")
    private Integer receiverId;

    @Column("file_url")
    private String fileUrl;

    @Column("message_type")
    private String messageType;

    @Column("service_id")
    private Integer serviceId;
}
