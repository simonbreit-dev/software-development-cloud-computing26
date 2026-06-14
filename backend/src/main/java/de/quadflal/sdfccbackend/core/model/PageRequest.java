package de.quadflal.sdfccbackend.core.model;

import java.util.List;

public record PageRequest(
        int page,
        int size,
        List<String> sort
) {}
