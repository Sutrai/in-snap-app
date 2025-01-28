package com.oous.imageservice.domain.constants;

import lombok.experimental.FieldNameConstants;
import lombok.experimental.UtilityClass;

import java.util.List;

@FieldNameConstants
@UtilityClass
public class Constants {

    public static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg", "image/png", "image/gif");
    public static final int MIN_WIDTH = 100;
    public static final int MIN_HEIGHT = 100;
    public static final int MAX_WIDTH = 5000;
    public static final int MAX_HEIGHT = 5000;
}
