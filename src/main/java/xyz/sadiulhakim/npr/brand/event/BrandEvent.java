package xyz.sadiulhakim.npr.brand.event;

import org.springframework.modulith.NamedInterface;
import xyz.sadiulhakim.npr.event.EntityEventType;

@NamedInterface("brand-delete-event")
public record BrandEvent(
        String name,
        EntityEventType type
) {
}
