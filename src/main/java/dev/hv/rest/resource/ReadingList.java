package dev.hv.rest.resource;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import dev.hv.model.Reading;

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(value = "readings")
public class ReadingList extends ArrayList {

    public ReadingList(List<Reading> readings) {
        addAll(readings);
    }
}
