package xyz.sadiulhakim.npr.product.event;

import xyz.sadiulhakim.npr.event.EntityEventType;

public record ProductEvent(
        String name,
        EntityEventType type
) {

}
