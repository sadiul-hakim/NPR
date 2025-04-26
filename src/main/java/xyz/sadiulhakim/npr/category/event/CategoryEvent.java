package xyz.sadiulhakim.npr.category.event;

import xyz.sadiulhakim.npr.event.EntityEventType;

public record CategoryEvent(
        String name,
        EntityEventType type
) {
}
