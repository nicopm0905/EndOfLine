package es.us.dp1.lx_xy_24_25.your_game_name.util;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;

public final class RestPreconditions {
	
	private RestPreconditions() {
        throw new AssertionError();
    }

    public static <T> T checkNotNull(final T resource,String resourceName, String fieldName, Object fieldValue) {
        if (resource == null) {
            throw new ResourceNotFoundException(resourceName, fieldName, fieldValue);
        }

        return resource;
    }
}
