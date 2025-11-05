package at.fhtw.model.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseConverter<T> {
    protected ObjectMapper mapper = new ObjectMapper();
    protected Class<T> type;

    public BaseConverter(Class<T> type) {
        this.type = type;
    }

    public String serialize(T t){
        try{
            return mapper.writeValueAsString(t);
        }catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    public T deserialize(String s){
        try{
            return mapper.readValue(s, type);
        }catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }
}
