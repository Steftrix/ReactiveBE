package org.mastersdbis.mtsdreactive.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Maps the messages table.
 * id uses IDENTITY — R2DBC will auto-populate it on insert.
 * Column name is 'mesej' (not 'message') — matches DB schema.
 */
@Table("messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @Column("id")
    private Integer id;

    @Column("date_created")
    private LocalDateTime dateCreated;

    @Column("date_updated")
    private LocalDateTime dateUpdated;

    @Column("file_url")
    private String fileUrl;

    @Column("mesej")
    private String message;   // field name is message, column name is mesej

    @Column("message_type")
    private String messageType;

    @Column("service_id")
    private Integer serviceId;

    @Column("receiver_user_id")
    private Integer receiverId;

    @Column("sender_user_id")
    private Integer senderId;
}
