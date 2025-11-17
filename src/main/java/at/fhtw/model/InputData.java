package at.fhtw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputData implements Serializable {
    /*
    * CSV inkludiert in jede Zeile die ZeilenNummer.
    * deshalb musste ich in dem csv manuell id vor ,timestamp... einfügen
    * TODO: War das nur bei dem Datenset so? eventuell anpassen, damit
    *  nicht jedes mal manuell id (oder ein anderes Attribut)
    *  in der .csv eingefügt werden muss
    * */
    private int id;
    private Date time_stamp;
    private Expression expression_best;
    private float expression_best_confidence;
    private float expression_neutral_confidence;
    private float expression_happy_confidence;
    private float expression_surprise_confidence;
    private float expression_anger_confidence;
    private float presence_confidence;
    private float head_rotation_pitch;
    private float head_rotation_roll;
    private float head_rotation_yaw;
}
