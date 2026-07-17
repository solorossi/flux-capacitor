package org.solorossi.fluxcapacitor.service;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.Errors;

import java.util.List;

/**
 * This interface defines a service to resolve Spring error and message codes into readable I18N messages.
 */
public interface ErrorMessageService {

    List<String> getMessages( Errors errors );

    String getMessage( String code, Object... arguments );

    String getMessage( MessageSourceResolvable error );
}
