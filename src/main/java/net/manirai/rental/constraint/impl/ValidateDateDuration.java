package net.manirai.rental.constraint.impl;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.LocalDate;

import net.manirai.rental.constraint.ValidDateDuration;

/**
 * 
 * @author Mani
 *
 */
public class ValidateDateDuration implements ConstraintValidator<ValidDateDuration, Object> {

    private String fromFieldName;
    private String toFieldName;
    
    @Override
    public void initialize(ValidDateDuration annotation) {
        this.fromFieldName = annotation.from();
        this.toFieldName = annotation.to();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        
        // Bean Validation specification recommends, we consider null values as being valid
        if(obj == null) {
            return true;
        }
        
        try {
            LocalDate from = (LocalDate) PropertyUtils.getProperty(obj, fromFieldName);
            LocalDate to = (LocalDate) PropertyUtils.getProperty(obj, toFieldName);
            
            // Bean Validation specification recommends, we consider null values as being valid
            if(from == null || to == null) {
                return true;
            } else {
                if(from.isBefore(to)) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
    }
}