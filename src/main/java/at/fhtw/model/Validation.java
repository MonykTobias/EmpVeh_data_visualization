package at.fhtw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Validation {
    private Integer id;
    private Boolean validated; // Renamed from 'valid' to 'validated' to indicate if user has checked it

    private Expression realEmotion;
    private String comment;

    // Default initialization from InputData
    public Validation (InputData data){
        this.id = data.getId();
        this.realEmotion = data.getExpression_best();
        this.validated = false; // Default to false
        this.comment = "";
    }

    // Constructor for when user validates
    public Validation(InputData data, Expression realEmotion, String comment){
        this.id = data.getId();
        this.realEmotion = realEmotion;
        this.comment = comment;
        this.validated = true; // Set to true when user validates
    }
}
