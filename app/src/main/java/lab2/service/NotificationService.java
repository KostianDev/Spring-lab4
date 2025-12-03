package lab2.service;

import java.time.LocalDateTime;

public class NotificationService {

    private final String type;
    private final String server;
    private final LocalDateTime createdAt;
    private final String instanceId;

    public NotificationService(String type, String server) {
        this.type = type;
        this.server = server;
        this.createdAt = LocalDateTime.now();
        this.instanceId = java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    public void sendNotification(String message) {
        System.out.println("[" + type + "] Sending via " + server + ": " + message);
    }

    public String getType() {
        return type;
    }

    public String getServer() {
        return server;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getInfo() {
        return String.format("NotificationService[type=%s, server=%s, instanceId=%s, created=%s]",
                type, server, instanceId, createdAt);
    }
}
