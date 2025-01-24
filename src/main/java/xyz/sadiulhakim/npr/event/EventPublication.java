package xyz.sadiulhakim.npr.event;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "event_publication")
public class EventPublication {

    @Id
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp completionDate;

    @Column(length = 255)
    private String eventType;

    @Column(length = 255)
    private String listenerId;

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp publicationDate;

    @Column(length = 255)
    private String serializedEvent;

    public EventPublication() {
    }

    public EventPublication(String id, Timestamp completionDate, String eventType, String listenerId, Timestamp publicationDate, String serializedEvent) {
        this.id = id;
        this.completionDate = completionDate;
        this.eventType = eventType;
        this.listenerId = listenerId;
        this.publicationDate = publicationDate;
        this.serializedEvent = serializedEvent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Timestamp completionDate) {
        this.completionDate = completionDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getListenerId() {
        return listenerId;
    }

    public void setListenerId(String listenerId) {
        this.listenerId = listenerId;
    }

    public Timestamp getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Timestamp publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getSerializedEvent() {
        return serializedEvent;
    }

    public void setSerializedEvent(String serializedEvent) {
        this.serializedEvent = serializedEvent;
    }
}
