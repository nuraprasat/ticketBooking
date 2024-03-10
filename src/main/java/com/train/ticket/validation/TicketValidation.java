package com.train.ticket.validation;

import com.train.ticket.model.TicketRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

@Component
public class TicketValidation implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return TicketRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        rejectIfEmptyOrWhitespace(errors, "from", "Value can't be blank", "Value can't be blank");
        rejectIfEmptyOrWhitespace(errors, "to", "Value can't be blank", "Value can't be blank");
        rejectIfEmptyOrWhitespace(errors, "price", "Value can't be blank", "Value can't be blank");
        rejectIfEmptyOrWhitespace(errors, "user.firstName", "Value can't be blank", "Value can't be blank");
        rejectIfEmptyOrWhitespace(errors, "user.email", "Value can't be blank", "Value can't be blank");
    }
}
