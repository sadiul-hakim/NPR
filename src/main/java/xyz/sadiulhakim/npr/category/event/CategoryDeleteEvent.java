package xyz.sadiulhakim.npr.category.event;

import org.springframework.modulith.NamedInterface;

@NamedInterface("category-delete-event")
public record CategoryDeleteEvent(
        String name
) {
}
