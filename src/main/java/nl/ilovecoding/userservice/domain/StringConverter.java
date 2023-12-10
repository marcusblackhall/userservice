package nl.ilovecoding.userservice.domain;


import org.springframework.core.convert.converter.Converter;

public class StringConverter implements Converter<String, UserType> {
    @Override
    public UserType convert(String source) {
        return UserType.valueOf(source.toUpperCase());
    }
}


