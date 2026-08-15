package com.attachakki.exception.entityNotFound;

public class NotificationIdNotFound extends EntityNotFoundException {
    public NotificationIdNotFound(Long notificationId) {
        super("Notification not found with id "+notificationId, null);
    }
}
