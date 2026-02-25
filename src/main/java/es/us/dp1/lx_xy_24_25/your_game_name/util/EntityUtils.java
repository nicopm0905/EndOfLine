package es.us.dp1.lx_xy_24_25.your_game_name.util;

import java.util.Collection;

import org.springframework.orm.ObjectRetrievalFailureException;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;

public abstract class EntityUtils {

	public static <T extends BaseEntity> T getById(Collection<T> entities, Class<T> entityClass, int entityId)
			throws ObjectRetrievalFailureException {
		for (T entity : entities) {
			if (entity.getId() == entityId && entityClass.isInstance(entity)) {
				return entity;
			}
		}
		throw new ObjectRetrievalFailureException(entityClass, entityId);
	}

}
