package es.us.dp1.lx_xy_24_25.your_game_name.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectRetrievalFailureException;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;

class UtilTests {

    // --- RestPreconditions Tests ---

    @Test
    void checkNotNull_ShouldReturnResource_WhenNotNull() {
        String resource = "test";
        String result = RestPreconditions.checkNotNull(resource, "Resource", "id", 1);
        assertEquals(resource, result);
    }

    @Test
    void checkNotNull_ShouldThrowException_WhenNull() {
        assertThrows(ResourceNotFoundException.class, () -> RestPreconditions.checkNotNull(null, "Resource", "id", 1));
    }

    // --- EntityUtils Tests ---

    static class TestEntity extends BaseEntity {
        // ID is Integer in BaseEntity
    }

    @Test
    void getById_ShouldReturnEntity_WhenFound() {
        TestEntity e1 = new TestEntity();
        e1.setId(1);
        TestEntity e2 = new TestEntity();
        e2.setId(2);

        TestEntity result = EntityUtils.getById(List.of(e1, e2), TestEntity.class, 1);
        assertEquals(e1, result);
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        TestEntity e1 = new TestEntity();
        e1.setId(1);

        assertThrows(ObjectRetrievalFailureException.class,
                () -> EntityUtils.getById(List.of(e1), TestEntity.class, 99));
    }

    @Test
    void getById_ShouldThrowException_WhenEmpty() {
        assertThrows(ObjectRetrievalFailureException.class,
                () -> EntityUtils.getById(Collections.emptyList(), TestEntity.class, 1));
    }

    // --- CallMonitoringAspect Tests ---

    @Test
    void aspect_EnableDisable() {
        CallMonitoringAspect aspect = new CallMonitoringAspect();
        assertTrue(aspect.isEnabled());

        aspect.setEnabled(false);
        assertEquals(false, aspect.isEnabled());
    }

    @Test
    void aspect_Invoke_Enabled() throws Throwable {
        CallMonitoringAspect aspect = new CallMonitoringAspect();
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.toShortString()).thenReturn("execution(Foo.bar())");
        when(joinPoint.proceed()).thenReturn("result");

        aspect.invoke(joinPoint);

        assertEquals(1, aspect.getCallCount());
    }

    @Test
    void aspect_Invoke_Disabled() throws Throwable {
        CallMonitoringAspect aspect = new CallMonitoringAspect();
        aspect.setEnabled(false);
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn("result");

        aspect.invoke(joinPoint);

        assertEquals(0, aspect.getCallCount());
    }

    @Test
    void aspect_Reset() throws Throwable {
        CallMonitoringAspect aspect = new CallMonitoringAspect();
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.toShortString()).thenReturn("execution(Foo.bar())");

        aspect.invoke(joinPoint);
        assertEquals(1, aspect.getCallCount());

        aspect.reset();
        assertEquals(0, aspect.getCallCount());
        assertEquals(0, aspect.getCallTime());
    }

    @Test
    void aspect_getCallTime_Zero() {
        CallMonitoringAspect aspect = new CallMonitoringAspect();
        assertEquals(0, aspect.getCallTime());
    }
}
