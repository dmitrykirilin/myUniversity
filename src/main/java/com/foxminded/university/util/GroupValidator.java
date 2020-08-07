package com.foxminded.university.util;

import com.foxminded.university.model.Group;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class GroupValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return Group.class.equals(aClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Group group = (Group) obj;
        if (!group.getName().matches("[a-zA-Z]{2}-\\d{1,2}")){
            errors.rejectValue("name", "","Имя не соответствует заданному шаблону!");
        }
    }
}
