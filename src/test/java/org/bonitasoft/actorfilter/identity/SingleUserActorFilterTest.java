package org.bonitasoft.actorfilter.identity;

import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.bonitasoft.engine.connector.EngineExecutionContext;
import org.bonitasoft.engine.filter.UserFilterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.bonitasoft.actorfilter.identity.SingleUserActorFilter.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class SingleUserActorFilterTest {

    @InjectMocks
    private SingleUserActorFilter filter;

    @Mock(lenient = true)
    private APIAccessor apiAccessor;
    @Mock(lenient = true)
    private ProcessAPI processApi;

    @Mock(lenient = true)
    private EngineExecutionContext executionContext;

    @BeforeEach
    void setUp() {
        when(apiAccessor.getProcessAPI()).thenReturn(processApi);
        when(executionContext.getProcessDefinitionId()).thenReturn(1L);
    }

    @Test
    public void should_throw_exception_if_mandatory_input_is_missing() {
        assertThrows(ConnectorValidationException.class, () ->
                filter.validateInputParameters()
        );
    }

    @Test
    public void should_throw_exception_if_mandatory_input_is_not_positive_long() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(USER_ID, -1L);
        filter.setInputParameters(parameters);
        assertThrows(ConnectorValidationException.class, () ->
                filter.validateInputParameters()
        );
    }

    @Test
    public void should_throw_exception_if_mandatory_input_is_not_a_long() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(USER_ID, "1");
        filter.setInputParameters(parameters);
        assertThrows(ConnectorValidationException.class, () ->
                filter.validateInputParameters()
        );
    }

    @Test
    public void should_return_a_list_of_candidates() throws UserFilterException {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(USER_ID, 3L);
        filter.setInputParameters(parameters);

        // When
        List<Long> candidates = filter.filter("MyActor");

        // Then
        assertThat(candidates).as("Only the specified users can be candidates.")
                .containsExactly(3L);

    }

}
