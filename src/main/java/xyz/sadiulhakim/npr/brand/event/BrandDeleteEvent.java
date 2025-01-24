package xyz.sadiulhakim.npr.brand.event;

import org.springframework.modulith.NamedInterface;

@NamedInterface("brand-delete-event")
public record BrandDeleteEvent(
        String name
) {
}
