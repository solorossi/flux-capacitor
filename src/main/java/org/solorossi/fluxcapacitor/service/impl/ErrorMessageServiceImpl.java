package org.solorossi.fluxcapacitor.service.impl;

import org.solorossi.fluxcapacitor.service.ErrorMessageService;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class implements the error message service API.
 */
@Service
public class ErrorMessageServiceImpl implements ErrorMessageService {

    public static final String DEFAULT_MESSAGE = "The message code is not found: [%s]";

    MessageSource messageSource;

    public ErrorMessageServiceImpl( MessageSource messageSource ) {

        this.messageSource = messageSource;
    }

    @Override
    public List<String> getMessages( Errors errors ) {

        return errors.getAllErrors().stream()
                .map( error -> messageSource.getMessage( error, LocaleContextHolder.getLocale() ) )
                .collect( Collectors.toList() );
    }

    @Override
    public String getMessage( String code, Object... arguments ) {

        String defaultMessage = String.format( DEFAULT_MESSAGE, code );
        return messageSource.getMessage( code, arguments, defaultMessage, LocaleContextHolder.getLocale() );
    }

    @Override
    public String getMessage( MessageSourceResolvable error ) {

        return messageSource.getMessage( error, LocaleContextHolder.getLocale() );
    }
}
