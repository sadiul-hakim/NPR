package xyz.sadiulhakim.npr.category.event;

import org.springframework.modulith.NamedInterface;
import xyz.sadiulhakim.npr.event.EntityEventType;

@NamedInterface("category-delete-event")
public record CategoryEvent(
        String name,
        EntityEventType type
) {
}
