package xyz.sadiulhakim.npr.product.event;

import org.springframework.modulith.NamedInterface;
import xyz.sadiulhakim.npr.event.EntityEventType;

@NamedInterface("product-event")
public record ProductEvent(
        String name,
        EntityEventType type
) {

}
