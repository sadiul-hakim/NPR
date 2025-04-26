package xyz.sadiulhakim.npr.brand.event;

import xyz.sadiulhakim.npr.event.EntityEventType;

public record BrandEvent(
        String name,
        EntityEventType type
) {
}
