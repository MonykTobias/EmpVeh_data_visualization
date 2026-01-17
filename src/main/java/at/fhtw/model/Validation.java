package at.fhtw.model;

import lombok.Data;

@Data
public class Validation {
    private Integer id;
    private Boolean valid;

    private Expression realEmotion;
    private String comment;

    // No changes -> just take whatever is in InputData
    public Validation (InputData data){
        this.id = data.getId();
        this.realEmotion = data.getExpression_best();

        this.valid = true; // no Emotion changes!
    }

    // No changes -> just take whatever is in InputData + given comment
    public Validation(InputData data, String comment){
        this.id = data.getId();
        this.realEmotion = data.getExpression_best();
        this.comment = comment;

        this.valid = true; // no Emotion changes!
    }

    // Other expression (realEmotion) -> save updated Emotion
    public Validation(InputData data, Expression realEmotion){
        this.id = data.getId();
        this.realEmotion = realEmotion;

        this.valid = false; // changed Emotion!
    }

    // Other expression (realEmotion) -> save updated Emotion + given comment
    public Validation(InputData data, Expression realEmotion, String comment){
        this.id = data.getId();
        this.realEmotion = realEmotion;
        this.comment = comment;

        this.valid = false; // changed Emotion!
    }
}
